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

package love.forte.simbot.suspendrunner.reserve

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.annotations.InternalSimbotAPI
import kotlin.coroutines.CoroutineContext

/**
 * 一个用于表示“预备”挂起函数的类型，
 * 用于提供一层非挂起的“中转”转化器。
 *
 * Java 中提供部分默认可应用于 [transform] 的转化器，可参考
 * `SuspendReserves` 中提供的各项静态工厂。
 *
 * e.g.:
 * ```java
 * foo.runReserve().transform(SuspendReserves.mono()); // 将结果转化为 reactor 中的 Mono<T>
 * ```
 *
 * _注：此接口对外仅供使用，接口的定义可能会增减，不保证对第三方的实现稳定。_
 *
 *
 * @param T the type of the result produced by the reserve
 */
public interface SuspendReserve<out T> {

    /**
     * 通过一个转化器 [transformer], 将当前预备的挂起函数经由此转化器转化为目标结果 [R].
     */
    public fun <R> transform(transformer: Transformer<T, R>): R

    /**
     * 应用于 [SuspendReserve.transform] 中的转化器。
     * [Transformer] 应当由 Kotlin 实现，并面向其他非 Kotlin 语言使用。
     * （因为涉及到针对挂起函数的操作，一般来讲其他语言无法实现）
     *
     * Java 中提供部分默认实现的转化器，可参考
     * `SuspendReserves` 中提供的各项静态工厂。
     */
    public interface Transformer<in T, out R> {
        /**
         * 执行转化
         */
        public operator fun <T1 : T> invoke(
            scope: CoroutineScope,
            context: CoroutineContext,
            block: suspend () -> T1
        ): R
    }
}

/**
 * 得到 [SuspendReserve] 实例。
 */
@InternalSimbotAPI
public fun <T> suspendReserve(
    scope: CoroutineScope,
    context: CoroutineContext,
    block: suspend () -> T
): SuspendReserve<T> =
    SuspendReserveImpl(scope, context, block)


private class SuspendReserveImpl<out T>(
    private val scope: CoroutineScope,
    private val context: CoroutineContext,
    private val block: suspend () -> T
) : SuspendReserve<T> {
    override fun <R> transform(transformer: SuspendReserve.Transformer<T, R>): R =
        transformer.invoke(scope, context, block)

    override fun toString(): String = "SuspendReserve(scope=$scope, context=$context, block=$block)"
}

