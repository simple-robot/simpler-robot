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

package love.forte.simbot.interceptor

/**
 * 一个基于 [Iterator] 游标推进的聚合式拦截器上下文实现。
 *
 * [invoke] 每次执行时都会尝试从 [interceptors] 中取得下一个拦截器：
 *
 * - 如果存在下一个拦截器，则通过 [doIntercept] 进入它的拦截逻辑；
 * - 如果不存在下一个拦截器，则通过 [invokeId] 执行被拦截的最终目标逻辑。
 *
 * 因此，一个完整的放行链路通常表现为：
 * 外层拦截器调用 [Interceptor.Context.invoke]，上下文继续推进到下一层拦截器，
 * 直到所有拦截器都已消费后执行 [invokeId]。
 *
 * [IteratorAggregationInterceptorContext] 不会缓存结果，也不会阻止重复调用 [invoke]。
 * 当 [interceptors] 已经被消费完毕后，后续再次调用 [invoke] 会直接执行 [invokeId]。
 * 子类应当为一次拦截流程提供独立的 [Iterator] 实例；同一个上下文不应被并发调用。
 *
 * 一个最小实现示例：
 * ```Kotlin
 * class TextContext(
 *     override val interceptors: Iterator<TextInterceptor>,
 *     private val block: suspend () -> String
 * ) : IteratorAggregationInterceptorContext<TextInterceptor, String>() {
 *     override suspend fun invokeId(): String = block()
 *
 *     override suspend fun doIntercept(interceptor: TextInterceptor): String {
 *         return interceptor.intercept(this)
 *     }
 * }
 * ```
 *
 * @see AggregationInterceptorContext
 * @see InterceptorSet.intercept
 * @author Forte Scarlet
 */
public abstract class IteratorAggregationInterceptorContext<
    T : Interceptor<*, R>,
    R
    > : AggregationInterceptorContext<R> {
        // TODO 比起 iterator 游标，获取可以改为 list + sublist 组合？

    /**
     * 当前拦截流程尚未执行的拦截器。
     *
     * 此迭代器的游标即为聚合上下文的推进状态。
     */
    protected abstract val interceptors: Iterator<T>

    /**
     * 执行被拦截的最终目标逻辑。
     *
     * 当 [interceptors] 已经没有剩余元素时，[invoke] 会调用此函数。
     *
     * ```Kotlin
     * override suspend fun invokeId(): String = block()
     * ```
     */
    protected abstract suspend fun invokeId(): R

    /**
     * 执行当前取得的 [interceptor]。
     *
     * 通常实现为调用 [Interceptor.intercept]，并传入当前上下文实例。
     *
     * ```Kotlin
     * override suspend fun doIntercept(interceptor: TextInterceptor): String {
     *     return interceptor.intercept(this)
     * }
     * ```
     */
    protected abstract suspend fun doIntercept(interceptor: T): R

    override suspend fun invoke(): R {
        return if (interceptors.hasNext()) {
            doIntercept(interceptors.next())
        } else {
            invokeId()
        }
    }
}
