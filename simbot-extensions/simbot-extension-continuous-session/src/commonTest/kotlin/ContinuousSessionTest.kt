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
import love.forte.simbot.extension.continuous.session.*
import love.forte.simbot.extension.continuous.session.ContinuousSessionContext.ConflictStrategy.EXISTING
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
            val context = ContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)
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
            assertNull(context[key])
        }

        assertTrue(parentJob.isActive)
    }

    @Test
    fun sessionPushAndThrowTest() = runTest {
        val parentJob = Job()
        coroutineScope {
            val key = Any()
            val context = ContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)
            val session = context.session(key) {
                assertEquals(1, await { it.toString() })// .also { println("await: $it") }
                val ex = assertFails {
                    await { throw IllegalStateException("error on $it") }// .also { println("await: $it") }
                }

                assertIs<IllegalStateException>(ex)
            }

            // launch {
            assertEquals("1", session.push(1)) // .also { println("push 1 result: $it") }

            val ex = assertFails {
                session.push(2)//.also { println("push 2 result: $it") }
            }
            assertIs<SessionAwaitOnFailureException>(ex)
            session.join()
            assertTrue(session.isCompleted)
            assertFalse(session.isCancelled)
            assertNull(context[key])
        }

        assertTrue(parentJob.isActive)
    }

    @Test
    fun sessionAwaitTimeoutTest() = runTest {
        val parentJob = Job()
        val context = ContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)

        val firstTimeoutJob = Job()

        coroutineScope {
            val key = Any()
            val session = context.session(key) {
                val v1 = withTimeoutOrNull(50.milliseconds) {
                    await { it.toString() }
                }

                assertNull(v1, "Expected timeout value to be null, but was: $v1")
                firstTimeoutJob.complete()
                assertEquals(1, await { it.toString() })
            }

            firstTimeoutJob.join()

            assertEquals("1", session.push(1))

            session.join()

            assertTrue(session.isCompleted)
            assertFalse(session.isCancelled)
            //println("before context: $context")
            // Expected value to be null, but was: <love.forte.simbot.extension.continuous.session.SimpleSessionImpl@653702a0>.
            // 但是加上 println 就好了
            // map 改成使用同步锁实现后就行了
            //println(context)
            val gotSession = context[key]
            assertNull(context[key], "Expect context[$key] to be null, but was: $gotSession")
        }

        assertTrue(parentJob.isActive)
    }

    @Test
    fun sessionGetTest() = runTest {
        val parentJob = Job()
        val context = ContinuousSessionContext<Int, String>(Dispatchers.Default + parentJob)
        val key = Any()


        val inSession = InSession {
            suspend fun awaitValue(): Int = await { it.toString() }
            assertEquals(1, awaitValue())
            assertEquals(2, awaitValue())
            assertEquals(3, awaitValue())
        }

        fun s() = context.session(key, EXISTING, inSession)

        assertEquals("1", s().push(1))
        assertEquals("2", s().push(2))
        assertEquals("3", s().push(3))
    }


    @Test
    fun sessionContinuationTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.Default)
        val key = Any()
        val inSession = InSession {
            val c = await()
            val value = c.value
            assertEquals(1, value)
            val job1 = launch {
                c.resume(value.toString())
            }

            val c2 = await()
            assertEquals(2, c2.value)
            val job2 = launch {
                c2.resume(c2.value.toString())
            }

            job1.join()
            job2.join()
        }

        val provider = context.session(key, inSession)

        assertEquals("1", provider.push(1))
        assertEquals("2", provider.push(2))
    }

    @Test
    fun sessionContinuationWithoutResumeTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.Default)
        val key = Any()
        val inSession = InSession {
            val c = await()
            val value = c.value
            assertEquals(1, value)
            val job = launch {
                c.resume(value.toString())
            }

            val c2 = await()
            assertEquals(2, c2.value)
            job.join()
        }

        val provider = context.session(key, inSession)

        assertEquals("1", provider.push(1))
        val ex = assertFails {
            provider.push(2)
        }
        assertIs<SessionCompletedWithoutResumeException>(ex)
    }

    @Test
    fun sessionContinuationWithoutResumeMultiTest() = runTest {
        val context = ContinuousSessionContext<Int, String>(Dispatchers.Default)
        val key = Any()
        val inSession = InSession {
            val c = await()
            val value = c.value
            assertEquals(1, value)
            val job = launch {
                c.resume(value.toString())
            }

            val c2 = await()
            val c3 = await()
            val c4 = await()
            val set = setOf(c2.value, c3.value, c4.value)
            assertContains(set, 2)
            assertContains(set, 3)
            assertContains(set, 4)
            job.join()
        }

        val provider = context.session(key, inSession)

        assertEquals("1", provider.push(1))

        coroutineScope {
            withContext(Dispatchers.Default) {
                launch {
                    assertIs<SessionCompletedWithoutResumeException>(assertFails {
                        provider.push(2)
                    })
                }
                launch {
                    assertIs<SessionCompletedWithoutResumeException>(assertFails {
                        provider.push(3)
                    })
                }
                launch {
                    assertIs<SessionCompletedWithoutResumeException>(assertFails {
                        provider.push(4)
                    })
                }
            }
        }
    }

}
