/*
 *     Copyright (c) 2024-2025. ForteScarlet.
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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.extension.continuous.session.*
import love.forte.simbot.plugin.find
import kotlin.test.*

/**
 * Tests for [EventContinuousSessionContext] plugin functionality and event-specific behavior.
 *
 * @author ForteScarlet
 */
class EventContinuousSessionContextTest {

    @Test
    fun installEventSessionContextTest() = runTest {
        val app = launchSimpleApplication {
            install(EventContinuousSessionContext)
        }

        val context = app.plugins.find<EventContinuousSessionContext>()
        assertNotNull(context)
        assertIs<EventContinuousSessionContext>(context)
    }

    @Test
    fun installEventSessionContextWithConfigurationTest() = runTest {
        val customDispatcher = Dispatchers.Default

        val app = launchSimpleApplication {
            install(EventContinuousSessionContext) {
                coroutineDispatcher = customDispatcher
            }
        }

        val context = app.plugins.find<EventContinuousSessionContext>()
        assertNotNull(context)
        assertIs<EventContinuousSessionContext>(context)
    }

    @Test
    fun eventSessionBasicFunctionalityTest() = runTest {
        val app = launchSimpleApplication {
            install(EventContinuousSessionContext)
        }

        val context = app.plugins.find<EventContinuousSessionContext>()!!
        val key = UnitContinuousSessionKey()

        // Create a mock event
        val mockEvent = MockEvent("test-event")

        val session = context.session(key) {
            val received = await { event ->
                if (event.value.id.literal == "test-event") {
                    EventResult.empty()
                } else {
                    EventResult.invalid()
                }
            }
            assertEquals(mockEvent, received.value)
        }

        val result = session.push(mockEvent)
        assertEquals(EventResult.empty(), result)

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun eventSessionMultipleEventsTest() = runTest {
        val app = launchSimpleApplication {
            install(EventContinuousSessionContext)
        }

        val context = app.plugins.find<EventContinuousSessionContext>()!!
        val key = UnitContinuousSessionKey()

        val events = listOf(
            MockEvent("event-1"),
            MockEvent("event-2"),
            MockEvent("event-3")
        )

        val session = context.session(key) {
            val results = mutableListOf<String>()
            repeat(3) { index ->
                val received = await { event ->
                    results.add(event.value.id.literal)
                    EventResult.of(isTruncated = true)
                }
                assertEquals(events[index], received.value)
            }
            assertEquals(listOf("event-1", "event-2", "event-3"), results)
        }

        events.forEach { event ->
            val result = session.push(event)
            assertEquals(EventResult.of(isTruncated = true), result)
        }

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun eventSessionWithInvalidResultTest() = runTest {
        val app = launchSimpleApplication {
            install(EventContinuousSessionContext)
        }

        val context = app.plugins.find<EventContinuousSessionContext>()!!
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            multipleAwaitWith { _, value ->
                if (value.id.literal == "valid-event") {
                    valid(EventResult.empty())
                } else {
                    invalid(EventResult.invalid())
                }
            }
        }

        // Push invalid event
        println("push invalid event")
        val invalidResult = session.push(MockEvent("invalid-event"))
        assertEquals(EventResult.invalid(), invalidResult)

        // Push valid event
        println("push valid event")
        val validResult = session.push(MockEvent("valid-event"))
        assertEquals(EventResult.empty(), validResult)

        println("Should done here.")

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun eventSessionExceptionHandlingTest() = runTest {
        val app = launchSimpleApplication {
            install(EventContinuousSessionContext)
        }

        val context = app.plugins.find<EventContinuousSessionContext>()!!
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            await { event ->
                if (event.value.id.literal == "error-event") {
                    throw IllegalStateException("Test exception")
                } else {
                    EventResult.empty()
                }
            }
        }

        // Push error event - should get SessionAwaitOnFailureException
        val exception = assertFails {
            session.push(MockEvent("error-event"))
        }
        assertIs<SessionAwaitOnFailureException>(exception)
        assertIs<IllegalStateException>(exception.cause)
    }

    @Test
    fun eventSessionConcurrentPushTest() = runTest {
        val app = launchSimpleApplication {
            install(EventContinuousSessionContext)
        }

        val context = app.plugins.find<EventContinuousSessionContext>()!!
        val key = UnitContinuousSessionKey()

        val receivedEvents = mutableSetOf<String>()

        val session = context.session(key) {
            repeat(3) {
                await { event ->
                    receivedEvents.add(event.value.id.literal)
                    EventResult.of(isTruncated = true)
                }
            }
        }

        // Push events concurrently
        coroutineScope {
            launch { session.push(MockEvent("concurrent-1")) }
            launch { session.push(MockEvent("concurrent-2")) }
            launch { session.push(MockEvent("concurrent-3")) }
        }

        session.join()
        assertEquals(setOf("concurrent-1", "concurrent-2", "concurrent-3"), receivedEvents)
    }

    @Test
    fun eventSessionConfigurationTest() = runTest {
        val customDispatcher = Dispatchers.Default

        val app = launchSimpleApplication {
            install(EventContinuousSessionContext) {
                coroutineDispatcher = customDispatcher
                // Test setting dispatcher to null
                coroutineDispatcher = null
                // Set it back
                coroutineDispatcher = customDispatcher
            }
        }

        val context = app.plugins.find<EventContinuousSessionContext>()!!
        assertNotNull(context)

        // Test that the context works with a custom configuration
        val key = UnitContinuousSessionKey()
        val session = context.session(key) {
            await { EventResult.empty() }
        }

        assertEquals(EventResult.empty(), session.push(MockEvent("test")))
        session.join()
    }

    /**
     * Mock event implementation for testing
     */
    @OptIn(FuzzyEventTypeImplementation::class)
    private data class MockEvent(private val idInternal: String) : Event {
        override val id: ID get() = idInternal.ID

        @OptIn(ExperimentalSimbotAPI::class)
        override val time: Timestamp = Timestamp.now()
        override fun toString(): String = "MockEvent(id='$id')"
    }
}
