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

package love.forte.simbot.common.time

import kotlin.time.Instant

/**
 * 使用 Kotlin 标准库中的 [Instant] 作为时间戳基准的 [Timestamp] 实现。
 *
 * @author ForteScarlet
 * @since 5.0
 */
public class StandardInstantTimestamp(private val instant: Instant) : Timestamp {
    override val milliseconds: Long
        get() = instant.toEpochMilliseconds()

    /**
     * 直接获取当前对象内包装的 [Instant] 对象实例，不会产生额外的对象创建开销。
     *
     * @since 5.0
     */
    override fun toInstant(): Instant = instant

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Timestamp) return false

        if (other is StandardInstantTimestamp) {
            return instant == other.instant
        }

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int {
        return instant.hashCode()
    }

    override fun toString(): String {
        return "StandardInstantTimestamp($instant)"
    }
}
