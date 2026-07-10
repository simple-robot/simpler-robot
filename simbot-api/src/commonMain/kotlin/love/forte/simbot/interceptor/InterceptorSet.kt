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

@file:JvmName("InterceptorSets")

package love.forte.simbot.interceptor

import kotlin.jvm.JvmName

/**
 * 拦截器集合。
 *
 * 一个集合通常表示当前运行环境中已注册的所有拦截器，例如 Application 持有的全局拦截器列表。
 * 集合本身不规定拦截器的执行顺序稳定性、并发可见性或生命周期；
 * 这些约束由具体实现提供。
 *
 * ```Kotlin
 * val set: InterceptorSet = SimpleInterceptorSet()
 * val interceptors = set.all().filterIsInstance<TextInterceptor>()
 * ```
 *
 * @see Interceptor
 * @see AggregationInterceptorContext
 * @see InterceptorSet.intercept
 * @since 5.0
 *
 * @author Forte Scarlet
 */
public interface InterceptorSet {
    /**
     * 获取当前集合中的所有拦截器。
     *
     * 返回值使用 [Sequence] 以便调用方继续按类型过滤或延迟构建上下文。
     * 具体实现可以返回快照序列，也可以返回基于当前集合状态的序列。
     *
     * ```Kotlin
     * val textInterceptors = set.all().filterIsInstance<TextInterceptor>()
     * ```
     */
    public fun all(): Sequence<Interceptor<*, *>>
}

/**
 * 从 [InterceptorSet] 中筛选类型为 [T] 的拦截器，构建上下文并执行一次拦截流程。
 *
 * [contextFactory] 会接收已经按 [T] 过滤后的拦截器序列，并负责构建最终的 [AggregationInterceptorContext]。
 * 通过 [contextFactory] 构建出来的 [C] 必须聚合完整拦截流程：
 * 第一次调用 [Interceptor.Context.invoke] 时应从最外层拦截器开始推进，
 * 后续由上下文自身决定如何继续进入下一层拦截器或最终目标逻辑。
 *
 * 此函数只负责筛选、构建和触发第一次 [Interceptor.Context.invoke]；
 * 不会缓存上下文，也不会规定上下文的重复调用语义。
 *
 * 一个实现参考：
 * ```Kotlin
 * class ExampleInterceptorContext(
 *     override val interceptors: Iterator<ExampleInterceptor>,
 *     private val id: suspend () -> String
 * ) : IteratorAggregationInterceptorContext<ExampleInterceptor, String>() {
 *     override suspend fun invokeId(): String = id()
 *
 *     override suspend fun doIntercept(interceptor: ExampleInterceptor): String {
 *         // 再次用于拦截行为的 context 应当是一个新的实例：因为 context 的 invoke 只能被使用一次。
 *         return interceptor.intercept(ExampleInterceptorContext(interceptors, id))
 *     }
 * }
 * ```
 *
 * 示例使用：
 * ```Kotlin
 * val set = SimpleInterceptorSet()
 * set.add(TextInterceptor { context -> context.invoke().uppercase() })
 *
 * // set.intercept<TextInterceptor, TextContext, String> { ... }
 * // 后面两个泛型参数通常可以省略。
 * val result = set.intercept<TextInterceptor, _, _> { interceptors ->
 *     TextContext(interceptors.iterator()) { "text" }
 * }
 * ```
 *
 * @see AggregationInterceptorContext
 * @see IteratorAggregationInterceptorContext
 * @since 5.0
 */
public suspend inline fun <
    reified T : Interceptor<C, R>,
    C : AggregationInterceptorContext<R>,
    R
    > InterceptorSet.intercept(
    contextFactory: (Sequence<T>) -> C,
): R {
    val interceptors = all().filterIsInstance<T>()
    val context = contextFactory(interceptors)
    return context.invoke()
}
