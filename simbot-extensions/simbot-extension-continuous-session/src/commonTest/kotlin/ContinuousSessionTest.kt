/*
 *     Copyright (c) 2024. ForteScarlet.
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
import love.forte.simbot.extension.continuous.session.SessionAwaitOnFailureException
import love.forte.simbot.extension.continuous.session.SessionPushOnFailureException
import love.forte.simbot.extension.continuous.session.SimpleContinuousSessionContext
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

/*
 *     Copyright (c) 2024. ForteScarlet.
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

class ContinuousSessionTest {

    @Test
    fun sessionPushAndCompletedTest() = runTest {
        val parentJob = Job()
        coroutineScope {
            val key = Any()
            val context = SimpleContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)
            val session = context.session(key) {
                assertEquals(1, await { it.toString() }.also { println("await: $it") })
                assertEquals(2, await { it.toString() }.also { println("await: $it") })
                assertEquals(3, await { it.toString() }.also { println("await: $it") })
                // done
            }

            assertEquals("1", session.push(1).also { println("push 1 result: $it") })
            assertEquals("2", session.push(2).also { println("push 2 result: $it") })
            assertEquals("3", session.push(3).also { println("push 3 result: $it") })

            val ex = assertFails {
                session.push(4).also { println("push 4 result: $it") }
            }
            ex.printStackTrace()
            assertIs<SessionPushOnFailureException>(ex)
            session.join()
            assertTrue(session.isCompleted)
            assertFalse(session.isCancelled)
            // TODO error!
            // assertNull(context[key])
        }

        assertTrue(parentJob.isActive)
    }

    @Test
    fun sessionPushAndThrowTest() = runTest {
        val parentJob = Job()
        coroutineScope {
            val key = Any()
            val context = SimpleContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)
            val session = context.session(key) {
                assertEquals(1, await { it.toString() })// .also { println("await: $it") }
                val ex = assertFails {
                    await { throw IllegalStateException("error on $it") }// .also { println("await: $it") }
                }

                //println("await ex = $ex (@${ex.hashCode()})")
                assertIs<IllegalStateException>(ex)
                //println("Session done")
            }

            // launch {
            assertEquals("1", session.push(1)) // .also { println("push 1 result: $it") }

            val ex = assertFails {
                session.push(2)//.also { println("push 2 result: $it") }
            }
            //println("push ex = $ex (@${ex.hashCode()})")
            assertIs<SessionAwaitOnFailureException>(ex)
            session.join()
            assertTrue(session.isCompleted)
            assertFalse(session.isCancelled)
            // TODO error!
            // assertNull(context[key])
        }

        assertTrue(parentJob.isActive)
    }

    @Test
    fun sessionAwaitTimeoutTest() = runTest {
        val parentJob = Job()
        val context = SimpleContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)

        coroutineScope {
            val key = Any()
            val session = context.session(key) {
                val v1 = withContext(Dispatchers.Default) {
                    withTimeoutOrNull(50.milliseconds) {
                        await { it.toString() }
                    }
                }
                assertNull(v1, "Expected timeout value to be null, but was: $v1")
                assertEquals(1, await { it.toString() })
            }

            withContext(Dispatchers.Default) {
                delay(100.milliseconds)
            }

            assertEquals("1", session.push(1))
            session.join()
            assertTrue(session.isCompleted)
            assertFalse(session.isCancelled)
            //println("before context: $context")
            // Expected value to be null, but was: <love.forte.simbot.extension.continuous.session.SimpleSessionImpl@653702a0>.
            // 但是加上 println 就好了
            //println(context)
            assertNull(context[key])

            // val newSession = context.session(key, strategy = ContinuousSessionContext.ConflictStrategy.OLD) {}
            // println("oldSession: $session")
            // println("newSession: $newSession")
            // assertNotEquals(session, newSession)
        }
        withContext(Dispatchers.Default) {
        }

        assertTrue(parentJob.isActive)
    }


}
