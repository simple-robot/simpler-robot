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

package love.forte.simbot.common.time

/**
 * 使用 [毫秒级时间戳][milliseconds] 作为基础数据进行简单包装的 [Timestamp] 实现。
 *
 * @see Timestamp
 *
 * @author ForteScarlet
 */
public class MillisecondTimestamp(override val milliseconds: Long) : Timestamp {

    /**
     * [MillisecondTimestamp] 与其他 [Timestamp] 始终通过 [milliseconds] 进行比较。
     *
     */
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Timestamp) return false

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int = milliseconds.hashCode()
    override fun toString(): String = "Timestamp(milliseconds=$milliseconds)"
}
