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
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.common.attribute.MutableAttributeMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext

/**
 * 将返回 [CompletionStage] 的 [interceptor] 转为标准挂起拦截器。
 *
 * ```java
 * Interceptor<String, String, MyPoint> interceptor = Interceptors.async(
 *     (point, context, chain) -> chain.proceed(context).thenApply(String::trim));
 * ```
 *
 * @param context 执行回调及下游协程的附加上下文。默认 `null`，继承当前调用。
 * @param interceptor 异步回调，返回代表本次拦截结果的 stage。
 * @throws IllegalArgumentException 当 [context] 包含 Job，试图替换当前调用的取消关系时。
 * @return 对应的 [AsyncInterceptor] 适配实例。
 * @since 5.0
 */
@Api4J
@JvmOverloads
public fun <C : Any, R, P : InterceptionPoint<C, R>> async(
    context: CoroutineContext? = null,
    interceptor: AsyncInterceptor.Handler<C, R, P>
): Interceptor<C, R, P> = object : AsyncInterceptor<C, R, P>(context) {
    override fun interceptAsync(point: P, context: C, chain: Chain<C, R>): CompletionStage<R> =
        interceptor.intercept(point, context, chain)
}

/**
 * 通过 [CompletionStage] 实现异步拦截器，Java 子类只需实现 [interceptAsync]。
 *
 * 回调与下游继承调用协程的执行上下文，等待 stage 不阻塞线程。
 * 调用取消会取消回调返回 stage 对应的 [CompletableFuture]；
 * 下游任务由 [Chain.proceed] 返回的 future 管理生命周期，取消该 future 会取消对应下游。
 * 下游失败通过 future 传递，允许用户处理失败并再次继续执行。
 * 框架不会在拦截结束时统一取消其他下游任务，用户负责取消自行启动且未等待的 future。
 *
 * CompletableFuture 的取消不会沿 `thenApply` / `thenCompose` 自动反向传播到源 future；
 * 返回组合 stage 时，用户如需取消其下游，应显式取消 [Chain.proceed] 返回的源 future。
 * 应在当前拦截调用内启动和组合下游，不应将 chain 留到调用结束后使用。
 *
 * 外部 stage 是否真正停止其自身任务，取决于该 stage 的实现；
 * 回调及其 completion 回调不应使用 `join` / `get` 阻塞等待下游。
 *
 * 附加上下文不得包含 [Job]，以保留当前调用的取消关系。
 *
 * @param asyncContext 执行回调及下游的附加上下文，默认继承当前调用。
 * @since 5.0
 */
@Api4J
public abstract class AsyncInterceptor<C : Any, R, P : InterceptionPoint<C, R>> @JvmOverloads constructor(
    private val asyncContext: CoroutineContext? = null
) : JvmPlatformInterceptor<C, R, P>() {
    init {
        require(asyncContext?.get(Job) == null) { "Interceptor context must not contain a Job" }
    }

    @JvmSynthetic
    final override suspend fun intercept(point: P, context: C, chain: Interceptor.Chain<C, R>): R {
        suspend fun interceptInCurrentContext(): R =
            interceptAsync(point, context, Chain(chain, currentCoroutineContext().minusKey(Job))).await()
        return if (asyncContext == null) {
            interceptInCurrentContext()
        } else {
            withContext(asyncContext) { interceptInCurrentContext() }
        }
    }

    /**
     * 启动异步拦截逻辑，返回代表最终结果的 stage，可以组合 [chain] 返回的 futures。
     * 同步抛出的异常和 stage 的异常完成均向调用方传播。
     * @param point 当前拦截点。
     * @param context 当前业务上下文。
     * @param chain 后续链的异步视图，不应阻塞等待它的结果。
     */
    @Throws(Exception::class)
    public abstract fun interceptAsync(point: P, context: C, chain: Chain<C, R>): CompletionStage<R>

    /**
     * 供 Java Lambda 使用的回调接口，允许抛出受检异常。
     * @since 5.0
     */
    public fun interface Handler<C : Any, R, P : InterceptionPoint<C, R>> {
        /**
         * 使用当前 [point]、业务 [context] 和后续 [chain] 处理本次操作。
         * 返回值及异常遵循 [interceptAsync] 的约定。
         */
        @Throws(Exception::class)
        public fun intercept(point: P, context: C, chain: Chain<C, R>): CompletionStage<R>
    }

    /**
     * [Interceptor.Chain] 的异步视图，下游任务的生命周期由各自的 future 管理。
     * 每次 [proceed] 都启动独立执行并返回新 future，所有执行共享同一属性 Map。
     * @since 5.0
     */
    public class Chain<C : Any, R> internal constructor(
        private val source: Interceptor.Chain<C, R>,
        private val coroutineContext: CoroutineContext
    ) {
        /**
         * 与原始 chain 相同的共享属性 Map，并发访问须自行协调。
         */
        public val attributes: MutableAttributeMap get() = source.attributes

        /**
         * 使用 [context] 启动固定后续链，返回其结果 future。
         * 取消或从外部完成此 future 会取消对应下游执行。
         * 调用结束不会自动取消其他 future；组合 stage 的取消也不会反向取消源 future。
         */
        @OptIn(DelicateCoroutinesApi::class)
        public fun proceed(context: C): CompletableFuture<R> =
            // 不挂靠调用 Job，由返回的 future 独立管理任务，避免隐式清理和失败传播。
            GlobalScope.future(coroutineContext) { source.proceed(context) }
    }
}
