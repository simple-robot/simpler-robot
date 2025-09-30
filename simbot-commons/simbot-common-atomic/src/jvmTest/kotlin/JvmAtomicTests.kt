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
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Base class for Lincheck-based concurrency tests.
 * These tests are JVM-specific as they use the Lincheck library
 * for model checking and stress testing of concurrent operations.
 */
abstract class AtomicStressTest {
    @Test
    fun stressTest() = StressOptions().check(this::class)

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}

/**
 * Tests for conversion functions between simbot atomic types and Kotlin stdlib atomic types.
 * These tests are JVM-specific as the `toKotlinAtomic*()` functions return types
 * from `kotlin.concurrent.atomics` package which is only available on JVM.
 */
@OptIn(ExperimentalAtomicApi::class)
class AtomicConversionTests {
    @Test
    fun atomicLongToKotlinTest() {
        val atomic = atomic(100L)
        val kotlinAtomic = atomic.toKotlinAtomicLong()

        // Verify the value is preserved
        assertEquals(100L, kotlinAtomic.load())

        // Modify through Kotlin atomic
        kotlinAtomic.store(200L)

        // For internally created atomics, they should share state
        // For converted atomics, the conversion creates a new instance if needed
        assertTrue(kotlinAtomic.load() == 200L)

        // Test with operations
        val atomic2 = atomic(50L)
        atomic2.incrementAndGet()
        val kotlinAtomic2 = atomic2.toKotlinAtomicLong()
        assertEquals(51L, kotlinAtomic2.load())
    }

    @Test
    fun atomicIntToKotlinTest() {
        val atomic = atomic(42)
        val kotlinAtomic = atomic.toKotlinAtomicInt()

        assertEquals(42, kotlinAtomic.load())

        kotlinAtomic.store(100)
        assertTrue(kotlinAtomic.load() == 100)

        // Test with operations
        val atomic2 = atomic(10)
        atomic2.incrementAndGet()
        val kotlinAtomic2 = atomic2.toKotlinAtomicInt()
        assertEquals(11, kotlinAtomic2.load())
    }

    @Test
    fun atomicUIntToKotlinTest() {
        val atomic = atomic(42u)
        val kotlinAtomic = atomic.toKotlinAtomicInt()

        // UInt is stored as Int internally
        assertEquals(42, kotlinAtomic.load())

        val atomic2 = atomic(100u)
        atomic2.incrementAndGet()
        val kotlinAtomic2 = atomic2.toKotlinAtomicInt()
        assertEquals(101, kotlinAtomic2.load())
    }

    @Test
    fun atomicULongToKotlinTest() {
        val atomic = atomicUL(100u)
        val kotlinAtomic = atomic.toKotlinAtomicLong()

        // ULong is stored as Long internally
        assertEquals(100L, kotlinAtomic.load())

        val atomic2 = atomicUL(50u)
        atomic2.incrementAndGet()
        val kotlinAtomic2 = atomic2.toKotlinAtomicLong()
        assertEquals(51L, kotlinAtomic2.load())
    }

    @Test
    fun atomicBooleanToKotlinTest() {
        val atomic = atomic(true)
        val kotlinAtomic = atomic.toKotlinAtomicBoolean()

        assertEquals(true, kotlinAtomic.load())

        kotlinAtomic.store(false)
        assertTrue(kotlinAtomic.load() == false)

        val atomic2 = atomic(false)
        atomic2.value = true
        val kotlinAtomic2 = atomic2.toKotlinAtomicBoolean()
        assertEquals(true, kotlinAtomic2.load())
    }

    @Test
    fun atomicRefToKotlinTest() {
        val atomic = atomicRef("Hello")
        val kotlinAtomic = atomic.toKotlinAtomicReference()

        assertEquals("Hello", kotlinAtomic.load())

        kotlinAtomic.store("World")
        assertTrue(kotlinAtomic.load() == "World")

        data class TestData(val value: Int)

        val atomic2 = atomicRef(TestData(42))
        atomic2.value = TestData(100)
        val kotlinAtomic2 = atomic2.toKotlinAtomicReference()
        assertEquals(100, kotlinAtomic2.load().value)
    }

    @Test
    fun conversionRoundTripTest() {
        // Test that converting and then using maintains correctness
        val atomic = atomic(10)
        val kotlinAtomic = atomic.toKotlinAtomicInt()

        // Perform operations on Kotlin atomic
        kotlinAtomic.fetchAndIncrement()
        kotlinAtomic.fetchAndIncrement()

        assertEquals(12, kotlinAtomic.load())

        // Create new simbot atomic from the value
        val atomic2 = atomic(kotlinAtomic.load())
        assertEquals(12, atomic2.value)
    }

    @Test
    fun conversionPreservesAtomicity() {
        val atomic = atomic(0L)
        val kotlinAtomic = atomic.toKotlinAtomicLong()

        // Both should support atomic operations
        assertTrue(kotlinAtomic.compareAndSet(0L, 1L))
        assertEquals(1L, kotlinAtomic.load())

        assertTrue(atomic.compareAndSet(1L, 2L))
        assertEquals(2L, atomic.value)
    }
}

// =============================================================================
// JVM-Specific Lincheck Concurrency Tests
// =============================================================================
// These stress tests use Lincheck to verify thread-safety and linearizability
// of atomic operations under concurrent access. They test various operation
// combinations that go beyond the basic functional tests in commonTest.
// =============================================================================

/**
 * Concurrency test for AtomicBoolean operations including getAndSet, compareAndSet, and direct value access.
 */
@Suppress("unused")
class AtomicBooleanStressTest : AtomicStressTest() {
    private val atomic = atomic(false)

    @Operation
    fun getAndSet() = atomic.getAndSet(true)

    @Operation
    fun compareAndSet() = atomic.compareAndSet(true, false)

    @Operation
    fun get() = atomic.value

    @Operation
    fun set() {
        atomic.value = true
    }
}

/**
 * Concurrency test focusing on compareAndSet operations with cyclic state transitions (0→1→2→0).
 */
@Suppress("unused")
class AtomicIntCompareAndSetTest : AtomicStressTest() {
    private val atomic = atomic(0)

    @Operation
    fun compareAndSet1() = atomic.compareAndSet(0, 1)

    @Operation
    fun compareAndSet2() = atomic.compareAndSet(1, 2)

    @Operation
    fun compareAndSet3() = atomic.compareAndSet(2, 0)

    @Operation
    fun get() = atomic.value
}

/**
 * Concurrency test for compareAndExchange operations mixed with increment operations.
 */
@Suppress("unused")
class AtomicLongCompareAndExchangeTest : AtomicStressTest() {
    private val atomic = atomic(0L)

    @Operation
    fun compareAndExchange1() = atomic.compareAndExchange(0L, 100L)

    @Operation
    fun compareAndExchange2() = atomic.compareAndExchange(100L, 200L)

    @Operation
    fun increment() = atomic.incrementAndGet()

    @Operation
    fun get() = atomic.value
}

/**
 * Concurrency test for getAndSet operations mixed with increment operations.
 */
@Suppress("unused")
class AtomicIntGetAndSetTest : AtomicStressTest() {
    private val atomic = atomic(0)

    @Operation
    fun getAndSet1() = atomic.getAndSet(10)

    @Operation
    fun getAndSet2() = atomic.getAndSet(20)

    @Operation
    fun increment() = atomic.incrementAndGet()

    @Operation
    fun get() = atomic.value
}

/**
 * Comprehensive concurrency test for AtomicUInt including inc, dec, compareAndSet, and getAndSet.
 */
@Suppress("unused")
class AtomicUIntComprehensiveTest : AtomicStressTest() {
    private val atomic = atomic(0u)

    @Operation
    fun inc() = atomic.incrementAndGet()

    @Operation
    fun dec() = atomic.decrementAndGet()

    @Operation
    fun cas() = atomic.compareAndSet(5u, 10u)

    @Operation
    fun getAndSet() = atomic.getAndSet(0u)

    @Operation
    fun get() = atomic.value
}

/**
 * Comprehensive concurrency test for AtomicULong including compareAndSet and compareAndExchange.
 */
@Suppress("unused")
class AtomicULongComprehensiveTest : AtomicStressTest() {
    private val atomic = atomicUL(0u)

    @Operation
    fun inc() = atomic.incrementAndGet()

    @Operation
    fun dec() = atomic.decrementAndGet()

    @Operation
    fun cas() = atomic.compareAndSet(5u, 10u)

    @Operation
    fun cae() = atomic.compareAndExchange(10u, 0u)

    @Operation
    fun get() = atomic.value
}

/**
 * Concurrency test for AtomicRef with compareAndSet operations on reference values.
 */
@Suppress("unused")
class AtomicRefStressTest : AtomicStressTest() {
    data class Value(val x: Int)

    private val v1 = Value(1)
    private val v2 = Value(2)
    private val v3 = Value(3)

    private val atomic = atomicRef(v1)

    @Operation
    fun set1() = atomic.compareAndSet(v1, v2)

    @Operation
    fun set2() = atomic.compareAndSet(v2, v3)

    @Operation
    fun set3() = atomic.compareAndSet(v3, v1)

    @Operation
    fun get() = atomic.value
}

/**
 * Comprehensive concurrency test mixing all AtomicLong operations: increment, decrement, compareAndSet, and getAndSet.
 */
@Suppress("unused")
class AtomicLongMixedOperationsTest : AtomicStressTest() {
    private val atomic = atomic(0L)

    @Operation
    fun inc1() = atomic.getAndIncrement()

    @Operation
    fun inc2() = atomic.incrementAndGet()

    @Operation
    fun dec1() = atomic.getAndDecrement()

    @Operation
    fun dec2() = atomic.decrementAndGet()

    @Operation
    fun cas() = atomic.compareAndSet(5L, 10L)

    @Operation
    fun getAndSet() = atomic.getAndSet(0L)

    @Operation
    fun get() = atomic.value
}

/**
 * Concurrency test for operator overloading (+=, -=) mixed with standard increment operations.
 */
class AtomicIntOperatorTest : AtomicStressTest() {
    private val atomic = atomic(0)

    @Operation
    fun plusAssign() {
        atomic += 1
    }

    @Operation
    fun minusAssign() {
        atomic -= 1
    }

    @Operation
    @Suppress("unused")
    fun increment() = atomic.incrementAndGet()

    @Operation
    fun get() = atomic.value
}
