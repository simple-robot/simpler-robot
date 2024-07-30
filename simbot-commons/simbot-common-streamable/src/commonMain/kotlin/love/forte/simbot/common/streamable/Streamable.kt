/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.common.streamable


/**
 *
 * 一个可流化的包装器类型。
 * 用于提供更便捷地对 [Sequence] 进行转化的API。
 *
 * [Streamable] 是一种用于便捷操作的**转化器**，
 * 而不是直接对流进行操作的类型。
 * 它的作用是减小不同平台间对 [Sequence] 的部分操作差异。
 *
 * [Streamable] 会有部分平台专供的API，例如在 JVM 中可转化为 `Stream`。
 *
 * @since 4.4.0
 *
 * @author ForteScarlet
 */
public expect class Streamable<out T> private constructor(seq: Sequence<T>) : Iterable<T> {
    /**
     * 转化为 [Sequence].
     */
    public fun asSequence(): Sequence<T>

    override fun iterator(): Iterator<T>

    /**
     * 将结果转化为 [List]
     */
    public fun collectToList(): List<T>

    /**
     * 将内部的序列结果收集到 [C] 中。
     */
    public fun <C : MutableCollection<in T>> collectTo(collection: C): C

    public companion object {
        /**
         * 将 [Sequence] 转化为 [Streamable]
         */
        public fun <T> Sequence<T>.asStreamable(): Streamable<T>
    }
}
