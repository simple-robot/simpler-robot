/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("CoreListeners")

package love.forte.simbot.core.event

import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListener
import java.util.*


/**
 * 向目标 [EventListener] 外层包装 [EventFilter].
 *
 * @see withMatcher
 */
public operator fun EventListener.plus(filter: EventFilter): EventListener {
    return withMatcher(filter::test)
}

/**
 * 向目标 [EventListener] 外层包装多个 [EventFilter].
 *
 * @see withMatcher
 */
public operator fun EventListener.plus(filters: Iterable<EventFilter>): EventListener {
    val sortedFilters = filters.sortedBy { it.priority }
    return withMatcher {
        sortedFilters.all { filter -> filter.test(this) }
    }
}
