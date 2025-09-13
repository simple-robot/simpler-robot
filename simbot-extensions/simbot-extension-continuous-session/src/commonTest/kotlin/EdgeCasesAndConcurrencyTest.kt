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

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.coroutines.IOOrDefault
import love.forte.simbot.extension.continuous.session.*
import kotlin.test.*

/**
 * Tests for edge cases, concurrent operations, and stress scenarios.
 *
 * @author ForteScarlet
 */
class EdgeCasesAndConcurrencyTest {

    @Test
    fun emptySessionTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        // Session that completes immediately without awaiting anything
        val session = context.session(key) {
            // Empty session - completes immediately
        }

        session.join()
        assertTrue(session.isCompleted)
        assertFalse(session.isCancelled)

        // Pushing to completed empty session should fail
        assertFails {
            session.push(1)
        }
    }

    @Test
    fun sessionWithMultipleAwaitsTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()
        val results = mutableListOf<String>()

        val session = context.session(key) {
            // Multiple awaits in sequence
            repeat(5) { index ->
                val (_, value) = await { (_, it) ->
                    results.add("await-$index-value-$it")
                    "result-$index-$it"
                }
                assertEquals(index + 1, value)
            }
        }

        // Push values sequentially
        repeat(5) { index ->
            assertEquals("result-$index-${index + 1}", session.push(index + 1))
        }

        session.join()
        assertEquals(
            listOf("await-0-value-1", "await-1-value-2", "await-2-value-3", "await-3-value-4", "await-4-value-5"),
            results
        )
    }

    @Test
    fun concurrentSessionCreationTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val keys = (1..10).map { UnitContinuousSessionKey() }

        // Create multiple sessions concurrently
        val sessions = coroutineScope {
            keys.map { key ->
                async {
                    context.session(key) {
                        await { (_, it) -> "session-${key.hashCode()}-$it" }
                    }
                }
            }.awaitAll()
        }

        // All sessions should be created successfully
        assertEquals(10, sessions.size)
        sessions.forEach { session ->
            assertTrue(session.isActive)
        }

        // Use all sessions concurrently
        coroutineScope {
            sessions.mapIndexed { index, session ->
                async {
                    session.push(index)
                }
            }.awaitAll()
        }

        // Wait for all sessions to complete
        sessions.forEach { it.join() }
        sessions.forEach { assertTrue(it.isCompleted) }
    }

    @Test
    fun concurrentPushToSameSessionTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()
        val receivedValues = mutableSetOf<Int>()

        val session = context.session(key) {
            repeat(10) {
                await { (_, it) ->
                    receivedValues.add(it)
                    "result-$it"
                }
            }
        }

        // Push values concurrently
        val results = coroutineScope {
            (1..10).map { value ->
                async {
                    session.push(value)
                }
            }.awaitAll()
        }

        session.join()

        // All values should be received
        assertEquals((1..10).toSet(), receivedValues)
        assertEquals(10, results.size)
        results.forEach { result ->
            assertTrue(result.startsWith("result-"))
        }
    }

    @Test
    fun sessionWithExceptionInMiddleTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            // First await succeeds
            await { (_, it) -> "first-$it" }

            // Second await throws exception
            await { (_, it) ->
                if (it == 2) {
                    throw IllegalStateException("Intentional exception")
                } else {
                    "second-$it"
                }
            }

            // This should never be reached
            fail("Should not reach here due to exception")
        }

        assertEquals("first-1", session.push(1))

        val exception = assertFails {
            session.push(2)
        }
        assertIs<SessionAwaitOnFailureException>(exception)
        assertIs<IllegalStateException>(exception.cause)

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun sessionWithLargeDataTest() = runTest {
        val context = ContinuousSessionContext<String, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()
        val largeString = "x".repeat(10000) // 10KB string

        val session = context.session(key) {
            val (_, received) = await { (_, it) -> it.uppercase() }
            assertEquals(largeString, received)
        }

        val result = session.push(largeString)
        assertEquals(largeString.uppercase(), result)

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun sessionKeyEqualityTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)

        // Test with different key instances that should be equal
        data class TestKey(val id: String) : ContinuousSessionKey<Unit>

        val key1 = TestKey("same-id")
        val key2 = TestKey("same-id")
        val key3 = TestKey("different-id")

        assertEquals(key1, key2)
        assertNotEquals(key1, key3)

        // Create session with first key
        val session1 = context.session(key1) {
            await { (_, it) -> "session1-$it" }
        }

        // Try to create session with equal key - should conflict
        assertFails {
            context.session(key2) {
                await { (_, it) -> "session2-$it" }
            }
        }

        // Create session with different key - should succeed
        val session3 = context.session(key3) {
            await { (_, it) -> "session3-$it" }
        }

        assertEquals("session1-1", session1.push(1))
        assertEquals("session3-2", session3.push(2))

        session1.join()
        session3.join()
    }

    @Test
    fun sessionContinuationResumeExceptionTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            val continuation = await()

            // Resume with exception
            continuation.resumeWithException(IllegalArgumentException("Test exception"))
        }

        val exception = assertFails {
            session.push(1)
        }
        assertIs<IllegalArgumentException>(exception)
        assertEquals("Test exception", exception.message)
    }

    @Test
    fun sessionStressTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val sessionCount = 50
        val messagesPerSession = 20

        val keys = (1..sessionCount).map { UnitContinuousSessionKey() }
        val sessions = keys.map { key ->
            context.session(key) {
                repeat(messagesPerSession) { index ->
                    await { (_, it) -> "session-${key.hashCode()}-msg-$index-value-$it" }
                }
            }
        }

        // Send messages to all sessions concurrently
        coroutineScope {
            sessions.flatMapIndexed { sessionIndex, session ->
                (1..messagesPerSession).map { messageIndex ->
                    async {
                        session.push(sessionIndex * 1000 + messageIndex)
                    }
                }
            }.awaitAll()
        }

        // Wait for all sessions to complete
        sessions.forEach { it.join() }
        sessions.forEach { assertTrue(it.isCompleted) }
    }

    @Test
    fun sessionWithNullValuesTest() = runTest {
        val context = ContinuousSessionContext<String?, String?>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            val (_, first) = await { (_, it) -> it?.uppercase() }
            val (_, second) = await { (_, it) -> it?.lowercase() ?: "null-value" }

            assertEquals("HELLO", first)
            assertEquals("null-value", second)
        }

        assertEquals("HELLO", session.push("hello"))
        assertEquals("null-value", session.push(null))

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun sessionTimeoutWithContinuationTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            val continuation = await()

            // Simulate timeout by not resuming immediately
            delay(50)
            continuation.resume("delayed-${continuation.value}")
        }

        // This should work despite the delay
        val result = session.push(42)
        assertEquals("delayed-42", result)

        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun sessionWithComplexDataTypesTest() = runTest {
        data class ComplexData(val id: Int, val name: String, val values: List<Double>)
        data class ComplexResult(val processed: Boolean, val data: ComplexData)

        val context = ContinuousSessionContext<ComplexData, ComplexResult>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val inputData = ComplexData(1, "test", listOf(1.0, 2.0, 3.0))

        val session = context.session(key) {
            val (_, received) = await { (_, data) ->
                ComplexResult(true, data.copy(name = data.name.uppercase()))
            }
            assertEquals(inputData, received)
        }

        val result = session.push(inputData)
        assertEquals(ComplexResult(true, inputData.copy(name = "TEST")), result)

        session.join()
        assertTrue(session.isCompleted)
    }
}
