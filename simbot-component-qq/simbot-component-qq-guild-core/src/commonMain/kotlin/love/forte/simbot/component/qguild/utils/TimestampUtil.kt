/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
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
}
