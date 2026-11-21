/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("EventInterceptors")
@file:JvmMultifileClass

package love.forte.simbot.event

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import love.forte.simbot.common.coroutines.VirtualOrIO
import love.forte.simbot.suspendrunner.runInBlocking
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

/**
 * 以异步的方式实现 [EventInterceptor].
 *
 * Java 中可以直接通过 `EventInterceptors.async` 构建。
 * 默认下游继承当前调用的执行环境。返回组合 future 时，
 * 取消不会自动反向传递到源 future，源任务由用户管理。
 *
 * @see EventInterceptor
 */
public fun interface JAsyncEventInterceptor : EventInterceptor {
    /**
     * [JAsyncEventInterceptor] 的异步 Context 代理，通过指定 [scope] 执行下游。
     */
    public data class Context(
        /**
         * 被代理的事件拦截上下文。
         */
        public val source: EventInterceptor.Context,
        /**
         * 下游 future 的执行作用域，其取消关系由调用方选择。
         */
        public val scope: CoroutineScope,
    ) {
        /**
         * 启动下游执行，取消返回的 future 会取消对应任务。
         */
        public fun invoke(): CompletableFuture<EventResult> =
            scope.future { source.invoke() }

        /**
         * 使用替换后的事件监听上下文启动下游执行。
         */
        public fun invoke(eventListenerContext: EventListenerContext): CompletableFuture<EventResult> =
            scope.future { source.invoke(eventListenerContext) }
    }

    /**
     * 构建 [Context]。可以实现此函数来重定义实现逻辑，例如变更默认的异步执行作用域。
     * 正常拦截入口使用当前调用的执行环境。手动调用此方法时回退到事件调度上下文。
     */
    public fun EventInterceptor.Context.createContext(): Context =
        Context(this, this as? EventInterceptorEntryContext ?: eventListenerContext.context)

    /**
     * 以异步的形式实现 [EventInterceptor.intercept].
     *
     * @see EventInterceptor.intercept
     */
    @Throws(Exception::class)
    public fun intercept(context: Context): CompletableFuture<EventResult>

    @JvmSynthetic
    override suspend fun EventInterceptor.Context.intercept(): EventResult {
        val source = EventInterceptorEntryContext(this, currentCoroutineContext())
        return intercept(source.createContext()).await()
    }
}

/**
 * 构建一个 [JAsyncEventInterceptor].
 *
 * 用于在 Java 中提供给参数为 [EventInterceptor] 的函数。例如：
 *
 * ```java
 * public void run(EventInterceptor interceptor) {
 *     // ...
 * }
 *
 * public void run() {
 *     run(EventInterceptors.async(context -> {
 *         // ...
 *         return CompletableFuture.completedFuture(EventResult.empty());
 *     }));
 * }
 * ```
 *
 * 而避免出现编译错误，也避免需要额外定义变量来接收 Lambda。
 * （因为 [EventInterceptor] 也是函数接口，但是单一函数被隐藏了。）
 *
 */
public fun async(function: JAsyncEventInterceptor): JAsyncEventInterceptor = function


/**
 * 以阻塞的方式实现 [EventInterceptor]
 * Java 回调在 [Dispatchers.VirtualOrIO] 执行，下游恢复调用入口的完整协程环境。
 * 父调用取消会取消挂起下游并中断 Java 回调线程，仅支持协作式取消或可中断的阻塞操作。
 * 回调抛出的 [InterruptedException] 会转为 [kotlinx.coroutines.CancellationException]，其他业务异常直接传播。
 *
 * @see EventInterceptor
 */
public fun interface JBlockEventInterceptor : EventInterceptor {
    /**
     * [JBlockEventInterceptor] 的阻塞 Context 代理。
     * 框架创建的代理保留入口执行环境与取消关系。手动构造时沿用全局阻塞桥接配置。
     */
    public data class Context(public val source: EventInterceptor.Context) {
        /**
         * 阻塞等待下游执行结果，异常直接向调用方传播。
         */
        public fun invoke(): EventResult = invokeInBlocking { source.invoke() }

        /**
         * 使用替换后的事件监听上下文阻塞等待下游结果。
         */
        public fun invoke(eventListenerContext: EventListenerContext): EventResult =
            invokeInBlocking { source.invoke(eventListenerContext) }

        /**
         * 入口环境由委托上下文携带，保留数据类原有构造和复制语义。
         */
        private fun invokeInBlocking(block: suspend () -> EventResult): EventResult {
            val entryContext = (source as? EventInterceptorEntryContext)?.entryContext
            return if (entryContext == null) {
                runInNoScopeBlocking(block = block)
            } else {
                runInBlocking(entryContext) { block() }
            }
        }
    }

    /**
     * 以阻塞的形式实现 [EventInterceptor.intercept].
     *
     * Java 中可以直接通过 `EventInterceptors.block` 构建。
     *
     * @see EventInterceptor.intercept
     */
    @Throws(Exception::class)
    public fun Context.intercept(): EventResult

    @JvmSynthetic
    override suspend fun EventInterceptor.Context.intercept(): EventResult {
        val source = EventInterceptorEntryContext(this, currentCoroutineContext())
        return runInterruptible(Dispatchers.VirtualOrIO) { Context(source).intercept() }
    }
}

/**
 * 通过委托携带调用入口环境，保留公开 Context 数据类及非挂起 createContext 扩展点。
 */
private class EventInterceptorEntryContext(
    source: EventInterceptor.Context,
    val entryContext: CoroutineContext
) : EventInterceptor.Context by source, CoroutineScope {
    // 阻塞桥接需要入口 Job，独立 future 则只继承入口的执行环境。
    override val coroutineContext: CoroutineContext = entryContext.minusKey(Job)
}

/**
 * 构建一个 [JBlockEventInterceptor].
 *
 * 用于在 Java 中提供给参数为 [EventInterceptor] 的函数。例如：
 *
 * ```java
 * public void run(EventInterceptor interceptor) {
 *     // ...
 * }
 *
 * public void run() {
 *     run(EventInterceptors.block(context -> {
 *         // ...
 *         return EventResult.empty();
 *     }));
 * }
 * ```
 *
 * 而避免出现编译错误，也避免需要额外定义变量来接收 Lambda。
 * （因为 [EventInterceptor] 也是函数接口，但是单一函数被隐藏了。）
 *
 */
public fun block(function: JBlockEventInterceptor): JBlockEventInterceptor = function
