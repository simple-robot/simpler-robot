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

    @Test
    fun atomicDeltaTest() {
        // AtomicInt with delta
        with(atomic(10)) {
            assertEquals(15, incrementAndGet(5))
            assertEquals(12, decrementAndGet(3))
            assertEquals(12, getAndIncrement(10))
            assertEquals(22, value)
            assertEquals(22, getAndDecrement(7))
            assertEquals(15, value)
        }

        // AtomicLong with delta
        with(atomic(100L)) {
            assertEquals(125L, incrementAndGet(25L))
            assertEquals(105L, decrementAndGet(20L))
            assertEquals(105L, getAndIncrement(50L))
            assertEquals(155L, value)
            assertEquals(155L, getAndDecrement(30L))
            assertEquals(125L, value)
        }

        // AtomicUInt with delta
        with(atomic(50u)) {
            assertEquals(60u, incrementAndGet(10u))
            assertEquals(55u, decrementAndGet(5u))
            assertEquals(55u, getAndIncrement(15u))
            assertEquals(70u, value)
            assertEquals(70u, getAndDecrement(20u))
            assertEquals(50u, value)
        }

        // AtomicULong with delta
        with(atomicUL(1000u)) {
            assertEquals(1500u, incrementAndGet(500u))
            assertEquals(1300u, decrementAndGet(200u))
            assertEquals(1300u, getAndIncrement(700u))
            assertEquals(2000u, value)
            assertEquals(2000u, getAndDecrement(400u))
            assertEquals(1600u, value)
        }
    }

    @Test
    fun atomicOperatorTest() {
        // AtomicInt operators
        with(atomic(10)) {
            this += 5
            assertEquals(15, value)
            this -= 3
            assertEquals(12, value)
            this += 1
            assertEquals(13, value)
            this -= 1
            assertEquals(12, value)
        }

        // AtomicLong operators
        with(atomic(100L)) {
            this += 25L
            assertEquals(125L, value)
            this -= 20L
            assertEquals(105L, value)
            this += 1L
            assertEquals(106L, value)
            this -= 1L
            assertEquals(105L, value)
        }

        // AtomicUInt operators
        with(atomic(50u)) {
            this += 10u
            assertEquals(60u, value)
            this -= 5u
            assertEquals(55u, value)
            this += 1u
            assertEquals(56u, value)
            this -= 1u
            assertEquals(55u, value)
        }

        // AtomicULong operators
        with(atomicUL(1000u)) {
            this += 500u
            assertEquals(1500u, value)
            this -= 200u
            assertEquals(1300u, value)
            this += 1u
            assertEquals(1301u, value)
            this -= 1u
            assertEquals(1300u, value)
        }
    }

    @Test
    fun atomicCompareAndExchangeReturnValueTest() {
        // AtomicInt
        with(atomic(10)) {
            // Failed exchange returns current value
            val result1 = compareAndExchange(expect = 5, value = 20)
            assertEquals(10, result1) // Should return current value (10), not expected (5)
            assertEquals(10, value) // Value should not change

            // Successful exchange returns expected value
            val result2 = compareAndExchange(expect = 10, value = 30)
            assertEquals(10, result2) // Should return the value that was replaced
            assertEquals(30, value) // Value should be updated
        }

        // AtomicLong
        with(atomic(100L)) {
            assertEquals(100L, compareAndExchange(50L, 200L))
            assertEquals(100L, value)
            assertEquals(100L, compareAndExchange(100L, 300L))
            assertEquals(300L, value)
        }

        // AtomicUInt
        with(atomic(10u)) {
            assertEquals(10u, compareAndExchange(5u, 20u))
            assertEquals(10u, value)
            assertEquals(10u, compareAndExchange(10u, 30u))
            assertEquals(30u, value)
        }

        // AtomicULong
        with(atomicUL(100u)) {
            assertEquals(100u, compareAndExchange(50u, 200u))
            assertEquals(100u, value)
            assertEquals(100u, compareAndExchange(100u, 300u))
            assertEquals(300u, value)
        }

        // AtomicBoolean
        with(atomic(true)) {
            assertEquals(true, compareAndExchange(expect = false, value = false))
            assertEquals(true, value)
            assertEquals(true, compareAndExchange(expect = true, value = false))
            assertEquals(false, value)
        }
    }

    @Test
    fun atomicUpdateRetryBehaviorTest() {
        // Test that update returns the old value
        with(atomic(0)) {
            var callCount = 0
            val oldValue = update {
                callCount++
                it + 10
            }
            assertEquals(0, oldValue)
            assertEquals(10, value)
            assertTrue(callCount >= 1) // Should be called at least once
        }

        // Test that updateAndGet returns the new value
        with(atomic(5)) {
            var callCount = 0
            val newValue = updateAndGet {
                callCount++
                it * 2
            }
            assertEquals(10, newValue)
            assertEquals(10, value)
            assertTrue(callCount >= 1)
        }

        // Test with AtomicLong
        with(atomic(100L)) {
            val oldValue = update { it + 50L }
            assertEquals(100L, oldValue)
            assertEquals(150L, value)

            val newValue = updateAndGet { it - 25L }
            assertEquals(125L, newValue)
            assertEquals(125L, value)
        }

        // Test with AtomicUInt
        with(atomic(10u)) {
            val oldValue = update { it * 2u }
            assertEquals(10u, oldValue)
            assertEquals(20u, value)
        }

        // Test with AtomicULong
        with(atomicUL(50u)) {
            val newValue = updateAndGet { it + 50u }
            assertEquals(100u, newValue)
            assertEquals(100u, value)
        }

        // Test with AtomicRef
        data class Counter(val count: Int)
        with(atomicRef(Counter(5))) {
            val oldValue = update { Counter(it.count + 1) }
            assertEquals(5, oldValue.count)
            assertEquals(6, value.count)

            val newValue = updateAndGet { Counter(it.count * 2) }
            assertEquals(12, newValue.count)
        }
    }

    @Test
    fun atomicRefNullableTest() {
        // Test with nullable reference
        val atomic = atomicRef<String?>(null)
        assertNull(atomic.value)

        atomic.value = "Hello"
        assertEquals("Hello", atomic.value)

        assertTrue(atomic.compareAndSet("Hello", null))
        assertNull(atomic.value)

        assertFalse(atomic.compareAndSet("World", "Test"))
        assertNull(atomic.value)

        assertTrue(atomic.compareAndSet(null, "NewValue"))
        assertEquals("NewValue", atomic.value)

        // Test compareAndExchange with null
        assertEquals("NewValue", atomic.compareAndExchange("Wrong", null))
        assertEquals("NewValue", atomic.value)

        assertEquals("NewValue", atomic.compareAndExchange("NewValue", null))
        assertNull(atomic.value)

        // Test update with null
        val oldValue = atomic.update { "Updated" }
        assertNull(oldValue)
        assertEquals("Updated", atomic.value)

        val newValue = atomic.updateAndGet { null }
        assertNull(newValue)
        assertNull(atomic.value)

        // Test getAndSet with null
        atomic.value = "Test"
        assertEquals("Test", atomic.getAndSet(null))
        assertNull(atomic.value)
    }

    @Test
    fun atomicExtendedBoundaryTest() {
        // Test Int boundaries
        with(atomic(Int.MAX_VALUE - 1)) {
            assertEquals(Int.MAX_VALUE, incrementAndGet())
            assertEquals(Int.MIN_VALUE, incrementAndGet()) // Overflow
        }

        with(atomic(Int.MIN_VALUE + 1)) {
            assertEquals(Int.MIN_VALUE, decrementAndGet())
            assertEquals(Int.MAX_VALUE, decrementAndGet()) // Underflow
        }

        // Test Long boundaries
        with(atomic(Long.MAX_VALUE - 1L)) {
            assertEquals(Long.MAX_VALUE, incrementAndGet())
            assertEquals(Long.MIN_VALUE, incrementAndGet()) // Overflow
        }

        with(atomic(Long.MIN_VALUE + 1L)) {
            assertEquals(Long.MIN_VALUE, decrementAndGet())
            assertEquals(Long.MAX_VALUE, decrementAndGet()) // Underflow
        }

        // Test UInt boundaries
        with(atomic(UInt.MAX_VALUE - 1u)) {
            assertEquals(UInt.MAX_VALUE, incrementAndGet())
            assertEquals(0u, incrementAndGet()) // Wrap to 0
            assertEquals(1u, incrementAndGet())
        }

        with(atomic(1u)) {
            assertEquals(0u, decrementAndGet())
            assertEquals(UInt.MAX_VALUE, decrementAndGet()) // Wrap to MAX
        }

        // Test ULong boundaries
        with(atomicUL(ULong.MAX_VALUE - 1u)) {
            assertEquals(ULong.MAX_VALUE, incrementAndGet())
            assertEquals(0u, incrementAndGet()) // Wrap to 0
        }

        with(atomicUL(1u)) {
            assertEquals(0u, decrementAndGet())
            assertEquals(ULong.MAX_VALUE, decrementAndGet()) // Wrap to MAX
        }

        // Test negative deltas
        with(atomic(10)) {
            assertEquals(5, incrementAndGet(-5))
            assertEquals(10, decrementAndGet(-5))
        }

        with(atomic(100L)) {
            assertEquals(75L, incrementAndGet(-25L))
            assertEquals(100L, decrementAndGet(-25L))
        }
    }

    @Test
    fun atomicToStringConsistencyTest() {
        // AtomicInt
        with(atomic(42)) {
            assertEquals("42", toString())
            incrementAndGet()
            assertEquals("43", toString())
        }

        // AtomicLong
        with(atomic(12345L)) {
            assertEquals("12345", toString())
        }

        // AtomicUInt
        with(atomic(999u)) {
            assertEquals("999", toString())
        }

        // AtomicULong
        with(atomicUL(88888u)) {
            assertEquals("88888", toString())
        }

        // AtomicBoolean
        with(atomic(true)) {
            assertEquals("true", toString())
            value = false
            assertEquals("false", toString())
        }

        // AtomicRef
        with(atomicRef("Test")) {
            assertEquals("Test", toString())
        }

        with(atomicRef(listOf(1, 2, 3))) {
            assertEquals("[1, 2, 3]", toString())
        }
    }

    @Test
    fun atomicZeroDeltaTest() {
        // Test zero delta operations
        with(atomic(10)) {
            assertEquals(10, incrementAndGet(0))
            assertEquals(10, decrementAndGet(0))
            assertEquals(10, getAndIncrement(0))
            assertEquals(10, getAndDecrement(0))
        }

        with(atomic(100L)) {
            assertEquals(100L, incrementAndGet(0L))
            assertEquals(100L, decrementAndGet(0L))
        }

        with(atomic(50u)) {
            assertEquals(50u, incrementAndGet(0u))
            assertEquals(50u, decrementAndGet(0u))
        }

        with(atomicUL(200u)) {
            assertEquals(200u, incrementAndGet(0u))
            assertEquals(200u, decrementAndGet(0u))
        }
    }

    @Test
    fun atomicChainedOperationsTest() {
        // Test chained compareAndSet operations
        with(atomic(0)) {
            assertTrue(compareAndSet(0, 1))
            assertTrue(compareAndSet(1, 2))
            assertTrue(compareAndSet(2, 3))
            assertFalse(compareAndSet(0, 4))
            assertEquals(3, value)
        }

        // Test chained compareAndExchange operations
        with(atomic(0L)) {
            assertEquals(0L, compareAndExchange(0L, 10L))
            assertEquals(10L, compareAndExchange(10L, 20L))
            assertEquals(20L, compareAndExchange(20L, 30L))
            assertEquals(30L, compareAndExchange(999L, 40L)) // Should fail and return current
            assertEquals(30L, value)
        }

        // Test mixed operations
        with(atomic(10)) {
            incrementAndGet()
            assertEquals(11, getAndSet(20))
            assertTrue(compareAndSet(20, 30))
            assertEquals(35, incrementAndGet(5))
            assertEquals(35, getAndDecrement(10))
            assertEquals(25, value)
        }
    }

    @Test
    fun atomicRefStructuralEqualityTest() {
        data class Person(val name: String, val age: Int)

        val person1 = Person("Alice", 30)
        val person2 = Person("Alice", 30) // Structurally equal to person1
        val person3 = Person("Bob", 25)

        val atomic = atomicRef(person1)

        // compareAndSet uses referential equality
        assertFalse(atomic.compareAndSet(person2, person3)) // Should fail even though structurally equal
        assertEquals(person1, atomic.value)

        assertTrue(atomic.compareAndSet(person1, person3)) // Should succeed with same reference
        assertEquals(person3, atomic.value)

        // compareAndExchange also uses referential equality
        assertEquals(person3, atomic.compareAndExchange(person2, person1))
        assertEquals(person3, atomic.value) // Value unchanged

        assertEquals(person3, atomic.compareAndExchange(person3, person1))
        assertEquals(person1, atomic.value) // Value changed
    }

    @Test
    fun atomicRefUpdateComplexObjectTest() {
        data class Counter(val count: Int, val name: String)

        val atomic = atomicRef(Counter(0, "Initial"))

        // Test multiple updates
        atomic.update { Counter(it.count + 1, it.name) }
        assertEquals(1, atomic.value.count)

        atomic.updateAndGet { Counter(it.count + 5, "Updated") }
        assertEquals(6, atomic.value.count)
        assertEquals("Updated", atomic.value.name)

        // Test with lambda that creates new instances
        repeat(10) {
            atomic.update { c -> Counter(c.count + 1, c.name) }
        }
        assertEquals(16, atomic.value.count)
    }

}

