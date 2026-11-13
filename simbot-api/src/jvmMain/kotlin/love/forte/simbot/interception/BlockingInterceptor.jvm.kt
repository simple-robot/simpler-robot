/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

@file:JvmName("Interceptors")
@file:JvmMultifileClass

package love.forte.simbot.interception

import kotlinx.coroutines.*
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.common.coroutines.VirtualOrIO
import love.forte.simbot.suspendrunner.runInBlocking
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * JVM 平台拦截器适配的公共基类。
 *
 * Java 用户可继承 [BlockingInterceptor]、[AsyncInterceptor] 或 [ReactiveInterceptor]，
 * 也可以通过 `Interceptors.blocking`、`async`、`reactive` 工厂传入 Lambda。
 * 该密封类由框架维护，业务代码应使用上述具体适配类型。
 *
 * @param C 非空业务上下文类型。
 * @param R 操作返回值类型。
 * @param P 拦截点类型。
 * @since 5.0
 */
public sealed class JvmPlatformInterceptor<C : Any, R, P : InterceptionPoint<C, R>> : Interceptor<C, R, P>

/**
 * 将阻塞 [interceptor] 转为标准挂起拦截器，适合 Java Lambda。
 *
 * ```java
 * Interceptor<String, String, MyPoint> interceptor =
 *     Interceptors.blocking((point, context, chain) -> chain.proceed(context));
 * ```
 *
 * @param context 阻塞逻辑的协程上下文，默认为 [Dispatchers.VirtualOrIO]，为 `null` 时在调用线程执行。
 * 作为 Java 使用端建议至少配置为一个适合 IO 或阻塞调用的调度器，例如 [Dispatchers.IO] 或 [Dispatchers.VirtualOrIO]。
 * @param interceptor 可通过阻塞 chain 继续执行及访问共享属性的回调。
 * @throws IllegalArgumentException 当 [context] 包含 Job，试图替换当前调用的取消关系时。
 * @return 对应的 [BlockingInterceptor] 适配实例。
 * @see BlockingInterceptor
 * @since 5.0
 */
@Api4J
@JvmOverloads
public fun <C : Any, R, P : InterceptionPoint<C, R>> blocking(
    context: CoroutineContext? = Dispatchers.VirtualOrIO,
    interceptor: BlockingInterceptor.Handler<C, R, P>
): Interceptor<C, R, P> = object : BlockingInterceptor<C, R, P>(context) {
    override fun interceptBlocking(point: P, context: C, chain: Chain<C, R>): R =
        interceptor.intercept(point, context, chain)
}

/**
 * 将阻塞 [interceptor] 转为标准挂起拦截器，适合 Java Lambda。
 *
 * ```java
 * Interceptor<String, String, MyPoint> interceptor =
 *     Interceptors.blocking(executor, (point, context, chain) -> chain.proceed(context));
 * ```
 *
 * @param executor 阻塞逻辑的自定义调度器。作为 Java 使用端建议至少配置为一个适合 IO 或阻塞调用的调度器，
 * 避免阻塞原本普通的协程上下文，例如使用虚拟线程池，或者一个自定义的线程池。
 * @param interceptor 可通过阻塞 chain 继续执行及访问共享属性的回调。
 * @return 对应的 [BlockingInterceptor] 适配实例。
 * @see BlockingInterceptor
 * @since 5.0
 */
@Api4J
public fun <C : Any, R, P : InterceptionPoint<C, R>> blocking(
    executor: Executor,
    interceptor: BlockingInterceptor.Handler<C, R, P>
): Interceptor<C, R, P> = blocking(executor.asCoroutineDispatcher(), interceptor)

/**
 * 以阻塞方式实现拦截器，Java 子类只需实现 [interceptBlocking]。
 *
 * Java 回调使用 [blockingContext]，chain 通过 [runInBlocking] 在入口上下文中同步执行下游。
 * 桥接保留父调用的取消关系，下游异常直接抛回 Java 回调，允许捕获后再次继续执行。
 * 父调用取消会取消挂起下游并中断 Java 回调线程，仅支持协作式取消或可中断的阻塞操作。
 * 回调抛出的 [InterruptedException] 会转为 [CancellationException]，其他业务异常直接传播。
 * 阻塞回调不能独占下游恢复执行所需的唯一线程，否则可能死锁。
 *
 * 附加上下文不得包含 [Job]，以保留当前调用的取消关系。
 *
 * @param blockingContext 阻塞拦截逻辑的执行上下文，默认 IO；为 `null` 时使用调用线程。
 * @since 5.0
 */
@Api4J
public abstract class BlockingInterceptor<C : Any, R, P : InterceptionPoint<C, R>> @JvmOverloads constructor(
    private val blockingContext: CoroutineContext? = Dispatchers.IO
) : JvmPlatformInterceptor<C, R, P>() {
    init {
        require(blockingContext?.get(Job) == null) { "Interceptor context must not contain a Job" }
    }

    @JvmSynthetic
    final override suspend fun intercept(point: P, context: C, chain: Interceptor.Chain<C, R>): R {
        val entryContext = currentCoroutineContext()
        return runInterruptible(blockingContext ?: EmptyCoroutineContext) {
            interceptBlocking(point, context, Chain(chain, entryContext))
        }
    }

    /**
     * 执行阻塞拦截逻辑；可通过 [chain] 继续执行、访问共享属性，或直接返回以短路。
     * 回调在 [blockingContext] 指定的上下文中执行，结果及异常传回原调用。
     * @param point 当前拦截点。
     * @param context 当前业务上下文。
     * @param chain 后续链的阻塞视图，只应在本次回调内使用。
     */
    @Throws(Exception::class)
    public abstract fun interceptBlocking(point: P, context: C, chain: Chain<C, R>): R

    /**
     * 供 Java Lambda 使用的回调接口，允许抛出受检异常。
     * @since 5.0
     */
    public fun interface Handler<C : Any, R, P : InterceptionPoint<C, R>> {
        /**
         * 使用当前 [point]、业务 [context] 和后续 [chain] 处理本次操作。
         * 返回值及异常遵循 [interceptBlocking] 的约定。
         */
        @Throws(Exception::class)
        public fun intercept(point: P, context: C, chain: Chain<C, R>): R
    }

    /**
     * [Interceptor.Chain] 的阻塞视图，每次 [proceed] 同步等待固定的后续链返回。
     * 不复制属性，不缓存结果；调用次数及并发限制由拦截点契约决定。
     * @since 5.0
     */
    public class Chain<C : Any, R> internal constructor(
        private val source: Interceptor.Chain<C, R>,
        private val coroutineContext: CoroutineContext
    ) {
        /**
         * 与原始 chain 相同的共享属性 Map。
         */
        public val attributes: MutableAttributeMap get() = source.attributes

        /**
         * 在完整入口上下文中使用 [context] 执行固定后续链，阻塞等待结果；异常及取消直接抛出。
         */
        @Throws(Exception::class)
        public fun proceed(context: C): R = runInBlocking(coroutineContext) { source.proceed(context) }
    }
}
