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

import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.publish
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.common.attribute.MutableAttributeMap
import org.reactivestreams.Publisher
import kotlin.coroutines.CoroutineContext

/**
 * 将返回 Reactive Streams [Publisher] 的 [interceptor] 转为非空结果的挂起拦截器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * ```java
 * Interceptor<String, String, MyPoint> interceptor = Interceptors.reactive(
 *     (point, context, chain) -> reactor.core.publisher.Mono.from(chain.proceed(context)));
 * ```
 *
 * @param interceptor 返回恰好一个非空结果并完成的 publisher 的回调。
 * @return 对应的 [ReactiveInterceptor] 适配实例。
 * @see reactiveNullable
 * @since 5.0
 */
@Api4J
public fun <C : Any, R : Any, P : InterceptionPoint<C, R>> reactive(
    interceptor: ReactiveInterceptor.Handler<C, R, P>
): Interceptor<C, R, P> = object : ReactiveInterceptor<C, R, P>() {
    override fun interceptReactive(point: P, context: C, chain: Chain<C, R>): Publisher<out R> =
        interceptor.intercept(point, context, chain)
}

/**
 * 将返回 Reactive Streams [Publisher] 的 [interceptor] 转为可空结果的挂起拦截器。
 * 空完成表示 `null`，一个元素表示有值，多个元素报错。元素本身不得为 `null`。
 *
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * [R] 是非空结果类型，拦截点的结果类型为 `R?`。
 *
 * ```java
 * Interceptor<String, String, MyPoint> interceptor = Interceptors.reactiveNullable(
 *     (point, context, chain) -> reactor.core.publisher.Mono.from(chain.proceed(context)));
 * ```
 *
 * @param interceptor 返回零个或一个非空结果并完成的 publisher 的回调。
 * @return 对应的 [NullableReactiveInterceptor] 适配实例。
 * @see NullableReactiveInterceptor
 * @since 5.0
 */
@Api4J
public fun <C : Any, R : Any, P : InterceptionPoint<C, R?>> reactiveNullable(
    interceptor: NullableReactiveInterceptor.Handler<C, R, P>
): Interceptor<C, R?, P> = object : NullableReactiveInterceptor<C, R, P>() {
    override fun interceptReactive(point: P, context: C, chain: ReactiveInterceptor.Chain<C, R?>): Publisher<out R> =
        interceptor.intercept(point, context, chain)
}

/**
 * 通过 Reactive Streams [Publisher] 实现非空结果的拦截器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * 等待唯一结果及完成信号，空完成抛出 [NoSuchElementException]，
 * 多元素抛出 [IllegalArgumentException]，错误信号向调用方传播。
 *
 * 回调、初始订阅及下游协程继承调用上下文，publisher 协程由订阅管理生命周期。
 * 调用取消会取消对返回 publisher 的订阅；正确组合的下游随订阅取消。
 * 用户自行订阅的任务及其取消由用户管理，框架不会在拦截结束时统一清理。
 *
 * 下游失败通过 publisher 传播，允许使用响应式操作符处理或重试。
 * 每次订阅重新执行后续链，可能重复外部副作用。
 *
 * 回调及操作符应避免阻塞等待下游。
 *
 * @see NullableReactiveInterceptor
 * @since 5.0
 */
@Api4J
public abstract class ReactiveInterceptor<C : Any, R : Any, P : InterceptionPoint<C, R>> :
    JvmPlatformInterceptor<C, R, P>() {
    @JvmSynthetic
    final override suspend fun intercept(point: P, context: C, chain: Interceptor.Chain<C, R>): R =
        interceptReactive(point, context, Chain(chain, currentCoroutineContext().minusKey(Job))).awaitSingle()

    /**
     * 返回恰好包含一个非空结果并完成的 publisher。
     * 可以直接返回 [chain] 的 publisher，或使用响应式操作符组合它。
     * @param point 当前拦截点。
     * @param context 当前业务上下文。
     * @param chain 后续链的响应式视图，仅在本次拦截调用内有效。
     */
    @Throws(Exception::class)
    public abstract fun interceptReactive(point: P, context: C, chain: Chain<C, R>): Publisher<out R>

    /**
     * 供 Java Lambda 使用的回调接口，允许抛出受检异常。
     * @since 5.0
     */
    public fun interface Handler<C : Any, R : Any, P : InterceptionPoint<C, R>> {
        /**
         * 使用当前 [point]、业务 [context] 和后续 [chain] 处理本次操作。
         * 返回值及异常遵循 [interceptReactive] 的约定。
         */
        @Throws(Exception::class)
        public fun intercept(point: P, context: C, chain: Chain<C, R>): Publisher<out R>
    }

    /**
     * [Interceptor.Chain] 的响应式视图，每次订阅启动独立下游执行。
     * 取消订阅会取消该次执行；独立订阅的任务由订阅者负责取消。
     * 应在当前拦截调用内构建并组合 publisher，不应将 chain 保存到调用之外使用。
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
         * 构建使用 [context] 执行固定后续链的冷 publisher，调用本方法不启动下游。
         * 每次订阅重新执行；非空结果发送一次，`null` 转为空完成，失败发送错误信号。
         * 列表等容器作为一个元素发送，不会自动展开。
         */
        public fun proceed(context: C): Publisher<R & Any> = publish(coroutineContext) {
            val result = source.proceed(context)
            if (result != null) {
                send(result)
            }
        }
    }
}

/**
 * 通过 Reactive Streams [Publisher] 实现可空结果的拦截器。
 * 运行时依赖、上下文、取消和重订阅语义与 [ReactiveInterceptor] 相同。
 *
 * [R] 是非空结果类型，拦截点返回 `R?`。空完成返回 `null`，
 * 一个元素等待完成后返回该值，多个元素抛出 [IllegalArgumentException]。
 * 错误信号向调用方传播，包括已发送一个值之后的错误。
 * 列表等容器仍作为一个元素发送；空列表与 `null` 相互独立。
 *
 * @since 5.0
 */
@Api4J
public abstract class NullableReactiveInterceptor<C : Any, R : Any, P : InterceptionPoint<C, R?>> :
    JvmPlatformInterceptor<C, R?, P>() {
    @JvmSynthetic
    final override suspend fun intercept(point: P, context: C, chain: Interceptor.Chain<C, R?>): R? =
        interceptReactive(point, context, ReactiveInterceptor.Chain(chain, currentCoroutineContext().minusKey(Job)))
            .asFlow()
            .map<R, R?> { it }
            .onEmpty { emit(null) }
            .single()

    /**
     * 返回包含零个或一个非空结果并完成的 publisher，空完成表示 `null`。
     * @param point 当前拦截点。
     * @param context 当前业务上下文。
     * @param chain 后续链的响应式视图，仅在本次拦截调用内有效。
     */
    @Throws(Exception::class)
    public abstract fun interceptReactive(
        point: P,
        context: C,
        chain: ReactiveInterceptor.Chain<C, R?>
    ): Publisher<out R>

    /**
     * 供 Java Lambda 使用的可空结果回调接口，允许抛出受检异常。
     * @since 5.0
     */
    public fun interface Handler<C : Any, R : Any, P : InterceptionPoint<C, R?>> {
        /**
         * 使用当前 [point]、业务 [context] 和后续 [chain] 处理本次操作。
         * 返回值及异常遵循 [NullableReactiveInterceptor.interceptReactive] 的约定。
         */
        @Throws(Exception::class)
        public fun intercept(point: P, context: C, chain: ReactiveInterceptor.Chain<C, R?>): Publisher<out R>
    }
}
