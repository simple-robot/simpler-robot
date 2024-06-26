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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import love.forte.simbot.common.atomic.*
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class AtomicTests {

    @Test
    fun atomicIncrDecrGetTest() {
        with(atomic(0)) {
            assertEquals(1, incrementAndGet())
            assertEquals(2, incrementAndGet())
            assertEquals(3, incrementAndGet())
            assertEquals(2, decrementAndGet())
            assertEquals(1, decrementAndGet())
            assertEquals(0, decrementAndGet())
            assertEquals(-1, decrementAndGet())
        }

        with(atomic(0L)) {
            assertEquals(1L, incrementAndGet())
            assertEquals(2L, incrementAndGet())
            assertEquals(3L, incrementAndGet())
            assertEquals(2L, decrementAndGet())
            assertEquals(1L, decrementAndGet())
            assertEquals(0L, decrementAndGet())
            assertEquals(-1L, decrementAndGet())
        }

        with(atomic(0u)) {
            assertEquals(1u, incrementAndGet())
            assertEquals(2u, incrementAndGet())
            assertEquals(3u, incrementAndGet())
            assertEquals(2u, decrementAndGet())
            assertEquals(1u, decrementAndGet())
            assertEquals(0u, decrementAndGet())
            assertEquals(UInt.MAX_VALUE, decrementAndGet())
        }

        with(atomicUL(0u)) {
            assertEquals(1u, incrementAndGet())
            assertEquals(2u, incrementAndGet())
            assertEquals(3u, incrementAndGet())
            assertEquals(2u, decrementAndGet())
            assertEquals(1u, decrementAndGet())
            assertEquals(0u, decrementAndGet())
            assertEquals(ULong.MAX_VALUE, decrementAndGet())
        }

        with(atomic(UInt.MAX_VALUE)) {
            assertEquals(0u, incrementAndGet())
        }

        with(atomic(ULong.MAX_VALUE)) {
            assertEquals(0u, incrementAndGet())
        }

        assertEquals(UInt.MAX_VALUE.toString(), atomic(UInt.MAX_VALUE).toString())
        assertEquals(ULong.MAX_VALUE.toString(), atomic(ULong.MAX_VALUE).toString())
    }

    @Test
    fun atomicGetIncrDecrTest() {
        with(atomic(0)) {
            assertEquals(0, getAndIncrement())
            assertEquals(1, getAndIncrement())
            assertEquals(2, getAndIncrement())
            assertEquals(3, getAndDecrement())
            assertEquals(2, getAndDecrement())
            assertEquals(1, getAndDecrement())
            assertEquals(0, getAndDecrement())
            assertEquals(-1, getAndDecrement())
        }

        with(atomic(0L)) {
            assertEquals(0L, getAndIncrement())
            assertEquals(1L, getAndIncrement())
            assertEquals(2L, getAndIncrement())
            assertEquals(3L, getAndDecrement())
            assertEquals(2L, getAndDecrement())
            assertEquals(1L, getAndDecrement())
            assertEquals(0L, getAndDecrement())
            assertEquals(-1L, getAndDecrement())
        }

        with(atomic(0u)) {
            assertEquals(0u, getAndIncrement())
            assertEquals(1u, getAndIncrement())
            assertEquals(2u, getAndIncrement())
            assertEquals(3u, getAndDecrement())
            assertEquals(2u, getAndDecrement())
            assertEquals(1u, getAndDecrement())
            assertEquals(0u, getAndDecrement())
            assertEquals(UInt.MAX_VALUE, getAndDecrement())
        }

        with(atomicUL(0u)) {
            assertEquals(0u, getAndIncrement())
            assertEquals(1u, getAndIncrement())
            assertEquals(2u, getAndIncrement())
            assertEquals(3u, getAndDecrement())
            assertEquals(2u, getAndDecrement())
            assertEquals(1u, getAndDecrement())
            assertEquals(0u, getAndDecrement())
            assertEquals(ULong.MAX_VALUE, getAndDecrement())
        }
    }

    @Test
    fun compareAsyncTest() = runTest {
        val times = 1000
        withContext(Dispatchers.Default) {
            coroutineScope {
                launch(Dispatchers.Default) { checkAtomicInt(times) }
                launch(Dispatchers.Default) { checkAtomicLong(times) }
                launch(Dispatchers.Default) { checkAtomicUInt(times) }
                launch(Dispatchers.Default) { checkAtomicULong(times) }
                launch(Dispatchers.Default) { checkAtomicRef(times) }
            }
        }
    }

    private suspend fun checkAtomicInt(times: Int) {
        val atomic = atomic(0)

        withContext(Dispatchers.Default) {
            launch {
                repeat(times) {
                    launch {
                        atomic += 1
                    }
                }
            }
            launch {
                repeat(times) {
                    launch {
                        atomic.update { it + 1 }
                    }
                }
            }
        }

        assertEquals(times * 2, atomic.value)
    }

    private suspend fun checkAtomicLong(times: Int) {
        val atomic = atomic(0L)

        withContext(Dispatchers.Default) {
            launch {
                repeat(times) {
                    launch {
                        atomic += 1L
                    }
                }
            }
            launch {
                repeat(times) {
                    launch {
                        atomic.update { it + 1L }
                    }
                }
            }
        }

        assertEquals((times * 2).toLong(), atomic.value)
    }

    private suspend fun checkAtomicUInt(times: Int) {
        val atomic = atomic(0u)

        withContext(Dispatchers.Default) {
            launch {
                repeat(times) {
                    launch {
                        atomic += 1u
                    }
                }
            }
            launch {
                repeat(times) {
                    launch {
                        atomic.update { it + 1u }
                    }
                }
            }
        }

        assertEquals((times * 2).toUInt(), atomic.value)
    }

    private suspend fun checkAtomicULong(times: Int) {
        val atomic = atomicUL(0u)

        withContext(Dispatchers.Default) {
            launch {
                repeat(times) {
                    launch {
                        atomic += 1u
                    }
                }
            }
            launch {
                repeat(times) {
                    launch {
                        atomic.update { it + 1u }
                    }
                }
            }
        }

        assertEquals((times * 2).toULong(), atomic.value)
    }

    private suspend fun checkAtomicRef(times: Int) {
        data class Value(val value: Int)

        val atomic = atomicRef(Value(0))

        withContext(Dispatchers.Default) {
            launch {
                repeat(times) {
                    launch {
                        atomic.update { it.copy(value = it.value + 1) }
                    }
                }
            }
        }

        assertEquals(Value(times), atomic.value)
    }

    @Test
    fun atomicEqualsTest() {
        assertNotEquals(atomic(0), atomic(0))
        assertNotEquals(atomic(0L), atomic(0L))
        assertNotEquals(atomic(0u), atomic(0u))
        assertNotEquals(atomicUL(0u), atomicUL(0u))
        assertNotEquals(atomic(false), atomic(false))
        val any = Any()
        assertNotEquals(atomicRef(any), atomicRef(any))
    }

    @Test
    fun atomicSetTest() {
        with(atomic(1)) {
            value = 0
            assertEquals(0, value)
            assertFalse(compareAndSet(expect = 1, value = 2))
            assertTrue(compareAndSet(expect = 0, value = 1))
            assertEquals(1, getAndSet(2))
            assertEquals(2, compareAndExchange(3, 4))
            assertEquals(2, compareAndExchange(2, 4))
            assertEquals(4, value)
        }

        with(atomic(0L)) {
            value = 0L
            assertEquals(0L, value)
            assertFalse(compareAndSet(expect = 1L, value = 2L))
            assertTrue(compareAndSet(expect = 0L, value = 1L))
            assertEquals(1L, getAndSet(2L))
            assertEquals(2L, compareAndExchange(3L, 4L))
            assertEquals(2L, compareAndExchange(2L, 4L))
            assertEquals(4L, value)
        }

        with(atomic(0u)) {
            value = 0u
            assertEquals(0u, value)
            assertFalse(compareAndSet(expect = 1u, value = 2u))
            assertTrue(compareAndSet(expect = 0u, value = 1u))
            assertEquals(1u, getAndSet(2u))
            assertEquals(2u, compareAndExchange(3u, 4u))
            assertEquals(2u, compareAndExchange(2u, 4u))
            assertEquals(4u, value)
        }

        with(atomicUL(0u)) {
            value = 0u
            assertEquals(0u, value)
            assertFalse(compareAndSet(expect = 1u, value = 2u))
            assertTrue(compareAndSet(expect = 0u, value = 1u))
            assertEquals(1u, getAndSet(2u))
            assertEquals(2u, compareAndExchange(3u, 4u))
            assertEquals(2u, compareAndExchange(2u, 4u))
            assertEquals(4u, value)
        }

        with(atomic(true)) {
            value = false
            assertFalse(value)
            assertFalse(compareAndSet(expect = true, value = false))
            assertTrue(compareAndSet(expect = false, value = true))
            assertTrue(getAndSet(false))
            assertFalse(getAndSet(true))
            assertTrue(compareAndExchange(expect = false, value = true))
            assertTrue(compareAndExchange(expect = true, value = false))
            assertFalse(compareAndExchange(expect = true, value = false))
            assertEquals(value.toString(), this.toString())
        }
    }

    @Test
    fun atomicUpdateTest() {
        with(atomic(0)) {
            assertEquals(0, update { 2 })
            assertEquals(10, updateAndGet { 10 })
        }
        with(atomic(0L)) {
            assertEquals(0L, update { 2L })
            assertEquals(10L, updateAndGet { 10L })
        }
        with(atomic(0u)) {
            assertEquals(0u, update { 2u })
            assertEquals(10u, updateAndGet { 10u })
        }
        with(atomicUL(0u)) {
            assertEquals(0u, update { 2u })
            assertEquals(10u, updateAndGet { 10u })
        }

    }

    @Test
    fun atomicRefTest() {
        data class Value(val value: Int)

        val v0 = Value(0)
        val v1 = Value(1)
        val v2 = Value(2)
        val v3 = Value(3)
        val v4 = Value(4)
        val v5 = Value(5)

        val atomic = atomicRef(v0)
        atomic.value = v1
        assertEquals(v1, atomic.value)

        assertFalse(atomic.compareAndSet(v2, v3))
        assertTrue(atomic.compareAndSet(v1, v3))
        assertEquals(v3, atomic.compareAndExchange(v4, v5))
        assertEquals(v3, atomic.compareAndExchange(v3, v5))
        assertEquals(v5.toString(), atomic.toString())

        assertEquals(v5, atomic.update { Value(6) })
        assertEquals(Value(7), atomic.updateAndGet { Value(7) })
    }

}

