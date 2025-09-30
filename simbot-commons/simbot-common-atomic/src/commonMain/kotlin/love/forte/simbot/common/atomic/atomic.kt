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

@file:JvmName("Atomics")
@file:JvmMultifileClass

package love.forte.simbot.common.atomic

import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndUpdate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.concurrent.atomics.AtomicBoolean as KotlinAtomicBoolean
import kotlin.concurrent.atomics.AtomicInt as KotlinAtomicInt
import kotlin.concurrent.atomics.AtomicLong as KotlinAtomicLong
import kotlin.concurrent.atomics.AtomicReference as KotlinAtomicReference


/**
 * Atomic [Long].
 *
 * @author ForteScarlet
 */
public interface AtomicLong {
    public var value: Long
    public fun getAndSet(value: Long): Long
    public fun incrementAndGet(delta: Long = 1L): Long
    public fun decrementAndGet(delta: Long = 1L): Long
    public fun getAndIncrement(delta: Long = 1L): Long
    public fun getAndDecrement(delta: Long = 1L): Long
    public fun compareAndSet(expect: Long, value: Long): Boolean
    public fun compareAndExchange(expect: Long, value: Long): Long
}

/**
 * Atomic [ULong]
 * @author ForteScarlet
 */
public interface AtomicULong {
    public var value: ULong
    public fun getAndSet(value: ULong): ULong
    public fun incrementAndGet(delta: ULong = 1u): ULong
    public fun decrementAndGet(delta: ULong = 1u): ULong
    public fun getAndIncrement(delta: ULong = 1u): ULong
    public fun getAndDecrement(delta: ULong = 1u): ULong
    public fun compareAndSet(expect: ULong, value: ULong): Boolean
    public fun compareAndExchange(expect: ULong, value: ULong): ULong
}

/**
 * Atomic [Int].
 * @author ForteScarlet
 */
public interface AtomicInt {
    public var value: Int
    public fun getAndSet(value: Int): Int
    public fun incrementAndGet(delta: Int = 1): Int
    public fun decrementAndGet(delta: Int = 1): Int
    public fun getAndIncrement(delta: Int = 1): Int
    public fun getAndDecrement(delta: Int = 1): Int
    public fun compareAndSet(expect: Int, value: Int): Boolean
    public fun compareAndExchange(expect: Int, value: Int): Int
}

/**
 * Atomic [UInt]
 * @author ForteScarlet
 */
public interface AtomicUInt {
    public var value: UInt
    public fun getAndSet(value: UInt): UInt
    public fun incrementAndGet(delta: UInt = 1u): UInt
    public fun decrementAndGet(delta: UInt = 1u): UInt
    public fun getAndIncrement(delta: UInt = 1u): UInt
    public fun getAndDecrement(delta: UInt = 1u): UInt
    public fun compareAndSet(expect: UInt, value: UInt): Boolean
    public fun compareAndExchange(expect: UInt, value: UInt): UInt
}

/**
 * Atomic [Boolean]
 * @author ForteScarlet
 */
public interface AtomicBoolean {
    public var value: Boolean
    public fun getAndSet(value: Boolean): Boolean
    public fun compareAndSet(expect: Boolean, value: Boolean): Boolean
    public fun compareAndExchange(expect: Boolean, value: Boolean): Boolean
}

/**
 * Atomic reference
 */
public interface AtomicRef<T> {
    public var value: T
    public fun getAndSet(value: T): T
    public fun compareAndSet(expect: T, value: T): Boolean
    public fun compareAndExchange(expect: T, value: T): T
}

@OptIn(ExperimentalAtomicApi::class)
private class AtomicLongImpl(val kotlinAtomic: KotlinAtomicLong) : AtomicLong {
    override var value: Long
        get() = kotlinAtomic.load()
        set(value) {
            kotlinAtomic.store(newValue = value)
        }

    override fun compareAndExchange(expect: Long, value: Long): Long =
        kotlinAtomic.compareAndExchange(expectedValue = expect, newValue = value)

    override fun getAndSet(value: Long): Long =
        kotlinAtomic.fetchAndUpdate { value }

    override fun incrementAndGet(delta: Long): Long =
        kotlinAtomic.addAndFetch(delta)

    override fun decrementAndGet(delta: Long): Long =
        kotlinAtomic.addAndFetch(-delta)

    override fun getAndIncrement(delta: Long): Long =
        kotlinAtomic.fetchAndAdd(delta)

    override fun getAndDecrement(delta: Long): Long =
        kotlinAtomic.fetchAndAdd(-delta)

    override fun compareAndSet(expect: Long, value: Long): Boolean =
        kotlinAtomic.compareAndSet(expectedValue = expect, newValue = value)

    override fun toString(): String = kotlinAtomic.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicLongImpl) return false

        if (kotlinAtomic != other.kotlinAtomic) return false

        return true
    }

    override fun hashCode(): Int {
        return kotlinAtomic.hashCode()
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class AtomicULongImpl(val kotlinAtomic: KotlinAtomicLong) : AtomicULong {
    override var value: ULong
        get() = kotlinAtomic.load().toULong()
        set(value) {
            kotlinAtomic.store(newValue = value.toLong())
        }

    override fun compareAndExchange(expect: ULong, value: ULong): ULong =
        kotlinAtomic.compareAndExchange(expectedValue = expect.toLong(), newValue = value.toLong()).toULong()

    override fun getAndSet(value: ULong): ULong =
        kotlinAtomic.fetchAndUpdate { value.toLong() }.toULong()

    override fun incrementAndGet(delta: ULong): ULong =
        kotlinAtomic.addAndFetch(delta.toLong()).toULong()

    override fun decrementAndGet(delta: ULong): ULong =
        kotlinAtomic.addAndFetch(-delta.toLong()).toULong()

    override fun getAndIncrement(delta: ULong): ULong =
        kotlinAtomic.fetchAndAdd(delta.toLong()).toULong()

    override fun getAndDecrement(delta: ULong): ULong =
        kotlinAtomic.fetchAndAdd(-delta.toLong()).toULong()

    override fun compareAndSet(expect: ULong, value: ULong): Boolean =
        kotlinAtomic.compareAndSet(expectedValue = expect.toLong(), newValue = value.toLong())

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicULongImpl) return false

        if (kotlinAtomic != other.kotlinAtomic) return false

        return true
    }

    override fun hashCode(): Int {
        return kotlinAtomic.hashCode()
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class AtomicIntImpl(val kotlinAtomic: KotlinAtomicInt) : AtomicInt {
    override var value: Int
        get() = kotlinAtomic.load()
        set(value) {
            kotlinAtomic.store(value)
        }

    override fun compareAndExchange(expect: Int, value: Int): Int =
        kotlinAtomic.compareAndExchange(expectedValue = expect, newValue = value)

    override fun getAndSet(value: Int): Int =
        kotlinAtomic.fetchAndUpdate { value }

    override fun incrementAndGet(delta: Int): Int =
        kotlinAtomic.addAndFetch(delta)

    override fun decrementAndGet(delta: Int): Int =
        kotlinAtomic.addAndFetch(-delta)

    override fun getAndIncrement(delta: Int): Int =
        kotlinAtomic.fetchAndAdd(delta)

    override fun getAndDecrement(delta: Int): Int =
        kotlinAtomic.fetchAndAdd(-delta)

    override fun compareAndSet(expect: Int, value: Int): Boolean =
        kotlinAtomic.compareAndSet(expectedValue = expect, newValue = value)

    override fun toString(): String = kotlinAtomic.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicIntImpl) return false

        if (kotlinAtomic != other.kotlinAtomic) return false

        return true
    }

    override fun hashCode(): Int {
        return kotlinAtomic.hashCode()
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class AtomicUIntImpl(val kotlinAtomic: KotlinAtomicInt) : AtomicUInt {
    override var value: UInt
        get() = kotlinAtomic.load().toUInt()
        set(value) {
            kotlinAtomic.store(value.toInt())
        }

    override fun compareAndExchange(expect: UInt, value: UInt): UInt =
        kotlinAtomic.compareAndExchange(expectedValue = expect.toInt(), newValue = value.toInt()).toUInt()

    override fun getAndSet(value: UInt): UInt =
        kotlinAtomic.fetchAndUpdate { value.toInt() }.toUInt()

    override fun incrementAndGet(delta: UInt): UInt =
        kotlinAtomic.addAndFetch(delta.toInt()).toUInt()

    override fun decrementAndGet(delta: UInt): UInt =
        kotlinAtomic.addAndFetch(-delta.toInt()).toUInt()

    override fun getAndIncrement(delta: UInt): UInt =
        kotlinAtomic.fetchAndAdd(delta.toInt()).toUInt()

    override fun getAndDecrement(delta: UInt): UInt =
        kotlinAtomic.fetchAndAdd(-delta.toInt()).toUInt()

    override fun compareAndSet(expect: UInt, value: UInt): Boolean =
        kotlinAtomic.compareAndSet(expectedValue = expect.toInt(), newValue = value.toInt())

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicUIntImpl) return false

        if (kotlinAtomic != other.kotlinAtomic) return false

        return true
    }

    override fun hashCode(): Int {
        return kotlinAtomic.hashCode()
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class AtomicBooleanImpl(val kotlinAtomic: KotlinAtomicBoolean) : AtomicBoolean {
    override var value: Boolean
        get() = kotlinAtomic.load()
        set(value) {
            kotlinAtomic.store(value)
        }

    override fun compareAndExchange(expect: Boolean, value: Boolean): Boolean =
        kotlinAtomic.compareAndExchange(expectedValue = expect, newValue = value)

    override fun getAndSet(value: Boolean): Boolean =
        kotlinAtomic.exchange(value)

    override fun compareAndSet(expect: Boolean, value: Boolean): Boolean =
        kotlinAtomic.compareAndSet(expect, value)

    override fun toString(): String = kotlinAtomic.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicBooleanImpl) return false

        if (kotlinAtomic != other.kotlinAtomic) return false

        return true
    }

    override fun hashCode(): Int {
        return kotlinAtomic.hashCode()
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class AtomicRefImpl<T>(val kotlinAtomic: KotlinAtomicReference<T>) : AtomicRef<T> {
    override var value: T
        get() = kotlinAtomic.load()
        set(value) {
            kotlinAtomic.store(newValue = value)
        }

    override fun compareAndExchange(expect: T, value: T): T =
        kotlinAtomic.compareAndExchange(expectedValue = expect, newValue = value)

    override fun getAndSet(value: T): T =
        kotlinAtomic.fetchAndUpdate { value }

    override fun compareAndSet(expect: T, value: T): Boolean =
        kotlinAtomic.compareAndSet(expectedValue = expect, newValue = value)

    override fun toString(): String = kotlinAtomic.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicRefImpl<*>) return false

        if (kotlinAtomic != other.kotlinAtomic) return false

        return true
    }

    override fun hashCode(): Int {
        return kotlinAtomic.hashCode()
    }
}

/**
 * Create an instance of [AtomicLong]
 */
@OptIn(ExperimentalAtomicApi::class)
public fun atomic(value: Long): AtomicLong = AtomicLongImpl(KotlinAtomicLong(value))

/**
 * Create an instance of [AtomicInt]
 */
@OptIn(ExperimentalAtomicApi::class)
public fun atomic(value: Int): AtomicInt = AtomicIntImpl(KotlinAtomicInt(value))

/**
 * Create an instance of [AtomicInt]
 */
@OptIn(ExperimentalAtomicApi::class)
public fun atomic(value: UInt): AtomicUInt = AtomicUIntImpl(KotlinAtomicInt(value.toInt()))

/**
 * Create an instance of [AtomicULong]
 */
@OptIn(ExperimentalAtomicApi::class)
public fun atomic(value: ULong): AtomicULong = AtomicULongImpl(KotlinAtomicLong(value.toLong()))

/**
 * Create an instance of [AtomicBoolean]
 */
@OptIn(ExperimentalAtomicApi::class)
public fun atomic(value: Boolean): AtomicBoolean = AtomicBooleanImpl(KotlinAtomicBoolean(value))

/**
 * Create an instance of [AtomicRef]<[T]>
 */
@OptIn(ExperimentalAtomicApi::class)
public fun <T> atomicRef(value: T): AtomicRef<T> = AtomicRefImpl(KotlinAtomicReference(value))

/**
 * Create an instance of [AtomicULong]
 */
public fun atomicUL(value: ULong): AtomicULong = atomic(value)

//region update

/**
 * Update value by [AtomicLong.compareAndSet] and then return the old value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     if (compareAndSet(current, block(current))) {
 *         return current
 *     }
 * }
 * ```
 *
 * @return The old value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicLong.update(block: (Long) -> Long): Long {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        if (compareAndSet(current, block(current))) {
            return current
        }
    }
}

/**
 * Update value by [AtomicInt.compareAndSet] and then return the old value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     if (compareAndSet(current, block(current))) {
 *         return current
 *     }
 * }
 * ```
 * @return The old value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicInt.update(block: (Int) -> Int): Int {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        if (compareAndSet(current, block(current))) {
            return current
        }
    }
}

/**
 * Update value by [AtomicLong.compareAndSet] and then return the old value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     if (compareAndSet(current, block(current))) {
 *         return current
 *     }
 * }
 * ```
 *
 * @return The old value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicULong.update(block: (ULong) -> ULong): ULong {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        if (compareAndSet(current, block(current))) {
            return current
        }
    }
}

/**
 * Update value by [AtomicInt.compareAndSet] and then return the old value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     if (compareAndSet(current, block(current))) {
 *         return current
 *     }
 * }
 * ```
 *
 * @return The old value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicUInt.update(block: (UInt) -> UInt): UInt {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        if (compareAndSet(current, block(current))) {
            return current
        }
    }
}

/**
 * Update value by [AtomicRef.compareAndSet] and then return the old value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     if (compareAndSet(current, block(current))) {
 *         return current
 *     }
 * }
 * ```
 *
 * @return The old value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> AtomicRef<T>.update(block: (T) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        if (compareAndSet(current, block(current))) {
            return current
        }
    }
}
//endregion

//region updateAndGet

/**
 * Update value by [AtomicLong.compareAndSet] and then return the new value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     val new = block(current)
 *     if (compareAndSet(current, new)) {
 *         return new
 *     }
 * }
 * ```
 *
 * @return The new value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicLong.updateAndGet(block: (Long) -> Long): Long {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        val new = block(current)
        if (compareAndSet(current, new)) {
            return new
        }
    }
}

/**
 * Update value by [AtomicInt.compareAndSet] and then return the new value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     val new = block(current)
 *     if (compareAndSet(current, new)) {
 *         return new
 *     }
 * }
 * ```
 *
 * @return The new value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicInt.updateAndGet(block: (Int) -> Int): Int {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        val new = block(current)
        if (compareAndSet(current, new)) {
            return new
        }
    }
}

/**
 * Update value by [AtomicULong.compareAndSet] and then return the new value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     val new = block(current)
 *     if (compareAndSet(current, new)) {
 *         return new
 *     }
 * }
 * ```
 *
 * @return The new value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicULong.updateAndGet(block: (ULong) -> ULong): ULong {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        val new = block(current)
        if (compareAndSet(current, new)) {
            return new
        }
    }
}

/**
 * Update value by [AtomicUInt.compareAndSet] and then return the new value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     val new = block(current)
 *     if (compareAndSet(current, new)) {
 *         return new
 *     }
 * }
 * ```
 *
 * @return The new value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun AtomicUInt.updateAndGet(block: (UInt) -> UInt): UInt {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        val new = block(current)
        if (compareAndSet(current, new)) {
            return new
        }
    }
}

/**
 * Update value by [AtomicRef.compareAndSet] and then return the new value.
 *
 * ```kotlin
 * while (true) {
 *     val current = value
 *     val new = block(current)
 *     if (compareAndSet(current, new)) {
 *         return new
 *     }
 * }
 * ```
 *
 * @return The new value that been exchanged.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> AtomicRef<T>.updateAndGet(block: (T) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        val current = value
        val new = block(current)
        if (compareAndSet(current, new)) {
            return new
        }
    }
}
//endregion

//region Operators

/**
 * Operator `+=` for [AtomicInt].
 *
 * e.g.
 * ```kotlin
 * atomic += 1
 * ```
 */
public operator fun AtomicInt.plusAssign(delta: Int) {
    incrementAndGet(delta)
}

/**
 * Operator `+=` for [AtomicUInt].
 *
 * e.g.
 * ```kotlin
 * atomic += 1u
 * ```
 */
public operator fun AtomicUInt.plusAssign(delta: UInt) {
    incrementAndGet(delta)
}

/**
 * Operator `+=` for [AtomicLong].
 *
 * e.g.
 * ```kotlin
 * atomic += 1L
 * ```
 */
public operator fun AtomicLong.plusAssign(delta: Long) {
    incrementAndGet(delta)
}

/**
 * Operator `+=` for [AtomicULong].
 *
 * e.g.
 * ```kotlin
 * atomic += 1u
 * ```
 */
public operator fun AtomicULong.plusAssign(delta: ULong) {
    incrementAndGet(delta)
}

/**
 * Operator `-=` for [AtomicInt].
 *
 * e.g.
 * ```kotlin
 * atomic -= 1
 * ```
 */
public operator fun AtomicInt.minusAssign(delta: Int) {
    decrementAndGet(delta)
}

/**
 * Operator `-=` for [AtomicUInt].
 *
 * e.g.
 * ```kotlin
 * atomic -= 1u
 * ```
 */
public operator fun AtomicUInt.minusAssign(delta: UInt) {
    decrementAndGet(delta)
}

/**
 * Operator `-=` for [AtomicLong].
 *
 * e.g.
 * ```kotlin
 * atomic -= 1L
 * ```
 */
public operator fun AtomicLong.minusAssign(delta: Long) {
    decrementAndGet(delta)
}

/**
 * Operator `-=` for [AtomicULong].
 *
 * e.g.
 * ```kotlin
 * atomic -= 1u
 * ```
 */
public operator fun AtomicULong.minusAssign(delta: ULong) {
    decrementAndGet(delta)
}
//endregion

//region Kotlin atomic

/**
 * Converts [AtomicLong] to [KotlinAtomicLong].
 * @since 4.14.1
 */
@OptIn(ExperimentalAtomicApi::class)
public fun AtomicLong.toKotlinAtomicLong(): KotlinAtomicLong = (this as? AtomicLongImpl)?.kotlinAtomic
    ?: KotlinAtomicLong(this.value)

/**
 * Converts [AtomicULong] to [kotlin.concurrent.atomics.AtomicLong].
 * @since 4.14.1
 */
@OptIn(ExperimentalAtomicApi::class)
public fun AtomicULong.toKotlinAtomicLong(): KotlinAtomicLong = (this as? AtomicULongImpl)?.kotlinAtomic
    ?: KotlinAtomicLong(this.value.toLong())

/**
 * Converts [AtomicInt] to [KotlinAtomicInt].
 * @since 4.14.1
 */
@OptIn(ExperimentalAtomicApi::class)
public fun AtomicInt.toKotlinAtomicInt(): KotlinAtomicInt = (this as? AtomicIntImpl)?.kotlinAtomic
    ?: KotlinAtomicInt(this.value)

/**
 * Converts [AtomicUInt] to [KotlinAtomicInt].
 * @since 4.14.1
 */
@OptIn(ExperimentalAtomicApi::class)
public fun AtomicUInt.toKotlinAtomicInt(): KotlinAtomicInt = (this as? AtomicUIntImpl)?.kotlinAtomic
    ?: KotlinAtomicInt(this.value.toInt())

/**
 * Converts [AtomicBoolean] to [KotlinAtomicBoolean].
 * @since 4.14.1
 */
@OptIn(ExperimentalAtomicApi::class)
public fun AtomicBoolean.toKotlinAtomicBoolean(): KotlinAtomicBoolean = (this as? AtomicBooleanImpl)?.kotlinAtomic
    ?: KotlinAtomicBoolean(this.value)

/**
 * Converts [AtomicRef] to [KotlinAtomicReference].
 * @since 4.14.1
 */
@OptIn(ExperimentalAtomicApi::class)
public fun <T> AtomicRef<T>.toKotlinAtomicReference(): KotlinAtomicReference<T> =
    (this as? AtomicRefImpl<T>)?.kotlinAtomic
        ?: KotlinAtomicReference(this.value)

//endregion
