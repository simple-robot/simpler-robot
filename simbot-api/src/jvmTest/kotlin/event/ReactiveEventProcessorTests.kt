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

package event

import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.event.*
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.util.stream.Collectors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame

/**
 * Verifies reactive event dispatch and collection of Reactive Streams results.
 */
@OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
class ReactiveEventProcessorTests {
    /**
     * Each subscription dispatches the event once. The collection overloads aggregate the results,
     * and each subscription to the supplier overload receives its own collection.
     */
    @Test
    fun pushStartsOnEachSubscription() = runTest {
        val expected = listOf(EventResult.invalid(), EventResult.empty())
        var pushes = 0
        val processor = object : EventProcessor {
            override fun push(event: Event): Flow<EventResult> {
                pushes++
                return expected.asFlow()
            }
        }
        val event = mockk<Event>()
        val results = processor.pushReactive(event)

        assertEquals(0, pushes)
        assertEquals(expected, results.asFlow().toList())
        assertEquals(expected, processor.pushAndCollectToListReactive(event).awaitSingle())
        assertEquals(2L, processor.pushAndCollectToReactive(event, Collectors.counting()).awaitSingle())

        val collections = processor.pushAndCollectToReactive(event) { ArrayList() }
        val first = collections.awaitSingle()
        val second = collections.awaitSingle()
        assertEquals(expected, first)
        assertEquals(expected, second)
        assertNotSame(first, second)
        assertEquals(5, pushes)
    }

    /**
     * A synchronous failure from [EventProcessor.push] reaches the subscriber as an error.
     */
    @Test
    fun synchronousPushFailureReachesSubscriber() = runTest {
        val processor = object : EventProcessor {
            override fun push(event: Event): Flow<EventResult> = error("Push failed")
        }
        val results = processor.pushReactive(mockk<Event>())

        assertEquals("Push failed", assertFailsWith<IllegalStateException> { results.awaitSingle() }.message)
    }

    /**
     * An event result containing a plain [Publisher] collects all elements when Reactor is present.
     */
    @Test
    fun genericPublisherIsCollectedWhenReactorIsPresent() = runTest {
        val publisher = Publisher { subscriber -> Flux.just(1, 2).subscribe(subscriber) }
        val result = EventResult.of(publisher).collected()

        assertEquals(listOf(1, 2), result.content)
    }
}
