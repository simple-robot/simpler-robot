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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.coroutines.IOOrDefault
import love.forte.simbot.extension.continuous.session.ConflictSessionKeyException
import love.forte.simbot.extension.continuous.session.ContinuousSessionContext
import love.forte.simbot.extension.continuous.session.ContinuousSessionContext.ConflictStrategy.*
import love.forte.simbot.extension.continuous.session.UnitContinuousSessionKey
import love.forte.simbot.extension.continuous.session.push
import kotlin.test.*

/**
 * Tests for [ContinuousSessionContext.ConflictStrategy] behavior.
 *
 * @author ForteScarlet
 */
class ConflictStrategyTest {

    @Test
    fun conflictStrategyFailureTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        // Create first session
        val session1 = context.session(key, FAILURE) {
            await { (_, it) -> it.toString() }
        }

        // Try to create second session with same key - should fail
        val exception = assertFails {
            context.session(key, FAILURE) {
                await { (_, it) -> it.toString() }
            }
        }

        exception.printStackTrace()

        assertIs<ConflictSessionKeyException>(exception, "exception type not ConflictSessionKeyException: $exception")

        // First session should still work
        assertEquals("1", session1.push(1))
        session1.join()
        assertTrue(session1.isCompleted)
    }

    @Test
    fun conflictStrategyReplaceTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        // Create first session
        val session1 = context.session(key, REPLACE) {
            await { (_, it) -> "first-${it}" }
        }

        // Create second session with same key - should replace first
        val session2 = context.session(key, REPLACE) {
            await { (_, it) -> "second-${it}" }
        }

        // First session should be cancelled
        assertTrue(session1.isCancelled)

        // Second session should work
        assertEquals("second-1", session2.push(1))
        session2.join()
        assertTrue(session2.isCompleted)

        // Context should only contain the second session
        assertNull(context[key]?.takeIf { it.isActive })
    }

    @Test
    fun conflictStrategyExistingTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        // Create first session
        val session1 = context.session(key, EXISTING) {
            await { (_, it) -> "first-${it}" }
        }

        // Try to create second session with same key - should return existing
        val session2 = context.session(key, EXISTING) {
            await { (_, it) -> "second-${it}" }
        }

        // Should be the same session
        assertSame(session1, session2)

        // Session should work with first logic
        assertEquals("first-1", session1.push(1))
        session1.join()
        assertTrue(session1.isCompleted)
    }

    @Test
    fun conflictStrategyWithCompletedSessionTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        // Create and complete first session
        val session1 = context.session(key, FAILURE) {
            await { (_, it) -> it.toString() }
        }
        assertEquals("1", session1.push(1))
        session1.join()
        assertTrue(session1.isCompleted)

        // Should be able to create new session with same key after completion
        val session2 = context.session(key, FAILURE) {
            await { (_, it) -> "new-${it}" }
        }

        assertEquals("new-2", session2.push(2))
        session2.join()
        assertTrue(session2.isCompleted)
    }

    @Test
    fun conflictStrategyConcurrentTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.IOOrDefault)
        val key = UnitContinuousSessionKey()

        // Test concurrent session creation with different strategies
        coroutineScope {
            val session1 = async {
                context.session(key, REPLACE) {
                    delay(10) // Small delay to test concurrency
                    await { (_, it) -> "session1-${it}" }
                }
            }

            val session2 = async {
                delay(5) // Start slightly later
                context.session(key, REPLACE) {
                    await { (_, it) -> "session2-${it}" }
                }
            }

            val s1 = session1.await()
            val s2 = session2.await()

            // One should be cancelled, the other should work
            val activeSession = if (s1.isActive) s1 else s2
            val cancelledSession = if (s1.isActive) s2 else s1

            assertTrue(cancelledSession.isCancelled)
            assertEquals("session${if (s1.isActive) "1" else "2"}-1", activeSession.push(1))
        }
    }
}
