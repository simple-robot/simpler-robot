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

@file:JvmName("Atomics")
@file:JvmMultifileClass

package love.forte.simbot.common.atomic

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


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

/**
 * Create an instance of [AtomicLong]
 */
public expect fun atomic(value: Long): AtomicLong

/**
 * Create an instance of [AtomicInt]
 */
public expect fun atomic(value: Int): AtomicInt

/**
 * Create an instance of [AtomicInt]
 */
public expect fun atomic(value: UInt): AtomicUInt

/**
 * Create an instance of [AtomicULong]
 */
public expect fun atomic(value: ULong): AtomicULong

/**
 * Create an instance of [AtomicBoolean]
 */
public expect fun atomic(value: Boolean): AtomicBoolean

/**
 * Create an instance of [AtomicRef]<[T]>
 */
public expect fun <T> atomicRef(value: T): AtomicRef<T>

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
