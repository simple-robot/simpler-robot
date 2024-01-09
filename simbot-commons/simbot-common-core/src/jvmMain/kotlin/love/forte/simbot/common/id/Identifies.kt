/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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
package love.forte.simbot.common.id

import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.LongID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UIntID.Companion.ID
import love.forte.simbot.common.id.ULongID.Companion.ID

/**
 * 提供给 Java 的 [ID] 辅助工具类。
 *
 * Kotlin 可以选择直接使用各类型的伴生对象中的扩展函数，例如 [LongID.ID]。
 */
public object Identifies {

    /**
     * @see [Long.ID]
     */
    @JvmStatic
    public fun of(value: Long): LongID = value.ID

    /**
     * @see [ULong.ID]
     */
    @JvmStatic
    @JvmName("ofULong")
    public fun ofULong(value: ULong): ULongID = value.ID

    /**
     * @see [IntID.ID]
     */
    @JvmStatic
    public fun of(value: Int): IntID = value.ID

    /**
     * @see [UInt.ID]
     */
    @JvmStatic
    @JvmName("ofUInt")
    public fun ofUInt(value: UInt): UIntID = value.ID

    /**
     * @see [String.ID]
     */
    @JvmStatic
    public fun of(value: String): StringID = value.ID

    /**
     * @see [CharSequence.ID]
     */
    @JvmStatic
    public fun of(value: CharSequence): StringID = value.ID
}

