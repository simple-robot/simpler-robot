/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.test.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.EventResult
import kotlin.jvm.JvmStatic

/**
 * Mock [EventProcessor].
 * @since 4.13.0
 * @author ForteScarlet
 */
public interface MockEventProcessor : EventProcessor {
    public companion object {
        public inline operator fun invoke(crossinline block: suspend (Event) -> EventResult): MockEventProcessor {
            return create { event -> flow { block(event) } }
        }

        @JvmStatic
        public fun create(block: (Event) -> Flow<EventResult>): MockEventProcessor {
            return MockEventProcessorImpl(block)
        }
    }
}


private class MockEventProcessorImpl(private val processor: (Event) -> Flow<EventResult>) : MockEventProcessor {
    override fun push(event: Event): Flow<EventResult> = processor(event)
}
