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

import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi

/**
 * Parse iso 8601 datetime string to [Timestamp]
 *
 * @see String.toInstant
 */
@ExperimentalQGApi
public fun String.toTimestamp(): Timestamp = KxInstantTimestamp(Instant.parse(this))

private class KxInstantTimestamp(private val instant: Instant) : Timestamp {
    override val milliseconds: Long = instant.toEpochMilliseconds()
    override fun timeAs(unit: TimeUnit): Long {
        return when (unit) {
            TimeUnit.SECONDS -> instant.epochSeconds
            TimeUnit.MILLISECONDS -> milliseconds
            else -> return unit.convert(milliseconds, TimeUnit.MILLISECONDS)
        }
    }

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
