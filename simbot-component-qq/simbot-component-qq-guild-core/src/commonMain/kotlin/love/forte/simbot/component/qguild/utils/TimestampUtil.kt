/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.utils

import love.forte.simbot.common.time.StandardInstantTimestamp
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.common.time.Timestamp
import kotlin.time.Instant

/**
 * Parse iso 8601 datetime string to [Timestamp]
 *
 * @see Instant.parse
 */
public fun String.toTimestamp(): Timestamp = StandardInstantTimestamp(Instant.parse(this))
// @ExperimentalQGApi

@Deprecated("Use [StandardInstantTimestamp] instead.")
private class KxInstantTimestamp(private val instant: Instant) : Timestamp {
    override val milliseconds: Long = instant.toEpochMilliseconds()
    override fun timeAs(unit: TimeUnit): Long {
        return when (unit) {
            TimeUnit.SECONDS -> instant.epochSeconds
            TimeUnit.MILLISECONDS -> milliseconds
            else -> unit.convert(milliseconds, TimeUnit.MILLISECONDS)
        }
    }

    @Suppress("DEPRECATION")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Timestamp) return false
        if (other is KxInstantTimestamp) {
            return instant == other.instant
        }

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int {
        return instant.hashCode()
    }

    override fun toString(): String {
        return "KxInstantTimestamp(instant=$instant)"
    }


}
