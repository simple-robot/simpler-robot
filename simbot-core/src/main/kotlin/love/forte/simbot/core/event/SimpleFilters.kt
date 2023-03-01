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
@file:JvmName("SimpleFilterUtil")

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.utils.runWithInterruptible
import java.util.function.Predicate

/**
 * 构建一个 [EventFilter].
 */
@JvmSynthetic
public fun simpleFilter(
    priority: Int = PriorityConstant.NORMAL,
    tester: suspend (context: EventListenerProcessingContext) -> Boolean,
): EventFilter =
    SimpleFilter(priority, tester)


/**
 * 使用阻塞的逻辑构建一个 [EventFilter].
 *
 * [tester] 会在 [runWithInterruptible] 中默认以 [kotlinx.coroutines.Dispatchers.IO] 作为调度器执行。
 *
 * @see simpleFilter
 * @see EventFilter
 */
@Api4J
@JvmOverloads
@JvmName("simpleFilter")
public fun blockingSimpleFilter(
    priority: Int = PriorityConstant.NORMAL,
    tester: Predicate<EventListenerProcessingContext>,
): EventFilter =
    simpleFilter(priority) {
        runWithInterruptible { tester.test(it) }
    }


private class SimpleFilter(
    override val priority: Int,
    private val func: suspend (EventListenerProcessingContext) -> Boolean,
) : EventFilter {
    
    override suspend fun test(context: EventListenerProcessingContext): Boolean = func(context)
}

