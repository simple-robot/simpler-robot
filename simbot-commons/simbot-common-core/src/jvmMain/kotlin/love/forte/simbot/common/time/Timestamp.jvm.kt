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

import java.io.Serializable
import java.time.Instant


/**
 * 基于 [Instant] 的 [Timestamp] 实现。
 *
 * @property instant [Instant]
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class InstantTimestamp private constructor(public val instant: Instant) : Timestamp, Serializable {
    override val milliseconds: Long
        get() = instant.toEpochMilli()

    override fun timeAs(unit: TimeUnit): Long {
        return when (unit) {
            TimeUnit.MILLISECONDS -> milliseconds
            TimeUnit.SECONDS -> instant.epochSecond
            else -> unit.convert(milliseconds, TimeUnit.MILLISECONDS)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Timestamp) return false
        if (other is InstantTimestamp) return instant == other.instant

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int = instant.hashCode()
    override fun toString(): String = "InstantTimestamp(milliseconds=$milliseconds, instant=$instant)"

    public companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L

        /**
         * 通过 [Instant] 得到一个 [InstantTimestamp]。
         */
        @JvmStatic
        @JvmName("of")
        public fun Instant.toTimestamp(): InstantTimestamp = InstantTimestamp(this)
    }
}

/**
 * 通过 [System.currentTimeMillis] 获取当前时间戳并转化为 [Timestamp]。
 *
 */
internal actual fun nowInternal(): Timestamp =
    Timestamp.ofMilliseconds(System.currentTimeMillis())
