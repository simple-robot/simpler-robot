/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.kook.stdlib.internal

/**
 * 基于枚举的元素映射表。
 *
 * [ActualEnumMap] 构建之初就需要对所有枚举元素对应的 [V] 进行初始化，
 * 并基于此确保 [get] 不会得到未确认元素。
 *
 * 线程不安全。
 *
 * @author ForteScarlet
 */
internal class ActualEnumMap<E : Enum<E>, V> private constructor(private val values: Array<V>) {
    companion object {
        inline fun <reified E : Enum<E>, reified V> create(init: (E) -> V): ActualEnumMap<E, V> {
            val values = enumValues<E>()
            return create(Array(values.size) { init(values[it]) })
        }

        internal fun <E : Enum<E>, V> create(values: Array<V>): ActualEnumMap<E, V> {
            return ActualEnumMap(values)
        }
    }

    /**
     * @throws IndexOutOfBoundsException if [key] is out of range
     */
    operator fun get(key: E): V = values[key.ordinal]

    /**
     * @throws IndexOutOfBoundsException if [key] is out of range
     */
    operator fun set(key: E, value: V) {
        values[key.ordinal] = value
    }
}
