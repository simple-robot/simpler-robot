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

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

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
 * [IteratorAggregationInterceptorContext] 不会缓存结果，并禁止重复调用 [invoke]。
 * 当 [interceptors] 已经被消费完毕后，后续再次调用 [invoke] 会抛出 [DuplicateContextInvocationException]。
 * 子类应当为一次拦截流程提供独立的 [Iterator] 实例、每一次拦截器的执行提供独立的 Context 实例；
 * 同一个上下文不应被并发或重复调用。
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
 *         // 再次用于拦截行为的 context 应当是一个新的实例：因为 context 的 invoke 只能被使用一次。
 *         return interceptor.intercept(TextContext(interceptors, block))
 *     }
 * }
 * ```
 *
 * @see AggregationInterceptorContext
 * @see intercept
 * @author Forte Scarlet
 */
@OptIn(ExperimentalAtomicApi::class)
public abstract class IteratorAggregationInterceptorContext<
    T : Interceptor<*, R>,
    R
    > : AggregationInterceptorContext<R> {
    protected val invoked: AtomicBoolean = AtomicBoolean(false)

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
     *         // 再次用于拦截行为的 context 应当是一个新的实例：因为 context 的 invoke 只能被使用一次。
     *         return interceptor.intercept(TextContext(interceptors, block))
     * }
     * ```
     */
    protected abstract suspend fun doIntercept(interceptor: T): R

    override suspend fun invoke(): R {
        if (!invoked.compareAndSet(expectedValue = false, newValue = true)) {
            throw DuplicateContextInvocationException(message = "Context.invoke has already been invoked")
        }

        return if (interceptors.hasNext()) {
            doIntercept(interceptors.next())
        } else {
            invokeId()
        }
    }
}
