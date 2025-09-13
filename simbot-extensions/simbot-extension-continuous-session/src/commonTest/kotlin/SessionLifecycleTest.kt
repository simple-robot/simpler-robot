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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.coroutines.IOOrDefault
import love.forte.simbot.extension.continuous.session.ContinuousSessionContext
import love.forte.simbot.extension.continuous.session.UnitContinuousSessionKey
import love.forte.simbot.extension.continuous.session.push
import kotlin.test.*

/**
 * Tests for session lifecycle management, cancellation, and resource cleanup.
 *
 * @author ForteScarlet
 */
class SessionLifecycleTest {

    @Test
    fun sessionCancellationTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            // This should never complete due to cancellation
            await { (_, it) -> it.toString() }
            fail("Session should have been cancelled")
        }

        assertTrue(session.isActive, "session.isActive should be true")
        assertFalse(session.isCompleted, "session.isCompleted should be false")
        assertFalse(session.isCancelled, "session.isCancelled should be false")

        // Cancel the session
        session.cancel()

        assertFalse(session.isActive, "session.isActive should be false")
        assertTrue(session.isCompleted, "session.isCompleted should be true")
        assertTrue(session.isCancelled, "session.isCancelled should be true")

        // Pushing to cancelled session should fail
        assertFails {
            session.push(1)
        }

        // Session should be removed from context
        assertNull(context[key]?.takeIf { it.isActive })
    }

    @Test
    fun sessionCancellationWithCauseTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()
        val cancellationCause = IllegalStateException("Test cancellation")

        val session = context.session(key) {
            await { (_, it) -> it.toString() }
        }

        session.cancel(cancellationCause)
        delay(10)

        assertTrue(session.isCancelled)
        assertFalse(session.isActive)
    }

    @Test
    fun sessionCompletionCallbackTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()
        var completionCalled = false
        var completionCause: Throwable? = null

        val session = context.session(key) {
            await { (_, it) -> it.toString() }
        }

        val onCompletionJob = Job()

        session.onCompletion { cause ->
            completionCalled = true
            completionCause = cause
            onCompletionJob.complete()
        }

        assertEquals("1", session.push(1))
        session.join()
        onCompletionJob.join()

        assertTrue(completionCalled, "completionCalled should be true")
        assertNull(completionCause, "completionCause should be null") // Normal completion
        assertTrue(session.isCompleted, "session.isCompleted should be true")
    }

    @Test
    fun sessionCompletionCallbackWithCancellationTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()
        var completionCalled = false
        var completionCause: Throwable? = null

        val session = context.session(key) {
            delay(100) // Long enough to be cancelled
            await { (_, it) -> it.toString() }
        }

        session.onCompletion { cause ->
            completionCalled = true
            completionCause = cause
        }

        session.cancel()
        delay(10)

        assertTrue(completionCalled)
        assertNotNull(completionCause)
        assertIs<CancellationException>(completionCause)
        assertTrue(session.isCancelled)
    }

    @Test
    fun sessionRemovalTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            await { (_, it) -> it.toString() }
        }

        // Session should be in context
        assertSame(session, context[key])
        assertTrue(key in context)

        // Remove session manually
        val removedSession = context.remove(key)
        assertSame(session, removedSession)

        // Session should no longer be in context
        assertNull(context[key], "context[$key] should be null")
        assertFalse(key in context)

        // Session should still be active (removal doesn't cancel)
        assertTrue(session.isActive)

        // Can still use the session
        assertEquals("1", session.push(1))
        session.join()
    }

    @Test
    fun sessionContextCleanupTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val keys = (1..5).map { UnitContinuousSessionKey() }

        // Create multiple sessions
        val sessions = keys.map { key ->
            context.session(key) {
                await { (_, it) -> it.toString() }
            }
        }

        // All sessions should be in context
        keys.forEach { key ->
            assertTrue(key in context)
            assertNotNull(context[key])
        }

        // Complete some sessions
        sessions.take(3).forEachIndexed { index, session ->
            assertEquals("$index", session.push(index))
            session.join()
        }

        // Cancel remaining sessions
        sessions.drop(3).forEach { it.cancel() }

        delay(10) // Allow cleanup

        // Completed and cancelled sessions should be cleaned up
        keys.forEach { key ->
            assertNull(context[key]?.takeIf { it.isActive })
        }
    }

    @Test
    fun sessionParentJobCancellationTest() = runTest {
        val parentJob = Job()
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault + parentJob)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            delay(100) // Long enough to be cancelled
            await { (_, it) -> it.toString() }
        }

        assertTrue(session.isActive)

        // Cancel parent job
        parentJob.cancel()
        delay(10)

        // Session should be cancelled too
        assertTrue(session.isCancelled)
        assertFalse(session.isActive)
    }

    @Test
    fun sessionJoinAfterCompletionTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            await { (_, it) -> it.toString() }
        }

        assertEquals("1", session.push(1))
        session.join()
        assertTrue(session.isCompleted)

        // Joining again should complete immediately
        session.join()
        assertTrue(session.isCompleted)
    }

    @Test
    fun sessionJoinAfterCancellationTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            delay(100)
            await { (_, it) -> it.toString() }
        }

        session.cancel()
        session.join() // Should complete immediately
        assertTrue(session.isCancelled)
    }

    @Test
    fun sessionPushAfterInactiveTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            await { (_, it) -> it.toString() }
        }

        assertEquals("1", session.push(1))
        session.join()

        // Pushing after completion should fail
        assertFails {
            session.push(2)
        }
    }

    @Test
    fun sessionResourceCleanupOnExceptionTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        val session = context.session(key) {
            await { (_, it) ->
                if (it == 1) {
                    it.toString()
                } else {
                    throw IllegalArgumentException("Invalid value: $it")
                }
            }
        }

        assertEquals("1", session.push(1))

        // Second push should cause exception and session cleanup
        assertFails {
            session.push(2)
        }

        session.join()
        assertTrue(session.isCompleted)

        // Session should be cleaned up from context
        assertNull(context[key]?.takeIf { it.isActive })
    }
}
