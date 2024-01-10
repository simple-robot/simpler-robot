/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Create an instance of [AtomicLong]
 */
public actual fun atomic(value: Long): AtomicLong =
    AtomicLongImpl(java.util.concurrent.atomic.AtomicLong(value))

private class AtomicLongImpl(private val atomic: java.util.concurrent.atomic.AtomicLong) : AtomicLong {
    override var value: Long
        get() = atomic.get()
        set(value) {
            atomic.set(value)
        }

    override fun getAndSet(value: Long): Long =
        atomic.getAndSet(value)

    override fun incrementAndGet(delta: Long): Long =
        if (delta == 1L) atomic.incrementAndGet() else atomic.addAndGet(delta)

    override fun decrementAndGet(delta: Long): Long =
        if (delta == 1L) atomic.decrementAndGet() else atomic.addAndGet(-delta)

    override fun getAndIncrement(delta: Long): Long =
        if (delta == 1L) atomic.getAndIncrement() else atomic.getAndAdd(delta)

    override fun getAndDecrement(delta: Long): Long =
        if (delta == 1L) atomic.getAndDecrement() else atomic.getAndAdd(-delta)

    override fun compareAndSet(expect: Long, value: Long): Boolean = atomic.compareAndSet(expect, value)

    override fun compareAndExchange(expect: Long, value: Long): Long = atomic.compareAndExchange(expect, value)

    override fun toString(): String = atomic.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicLongImpl) return false

        if (atomic != other.atomic) return false

        return true
    }

    override fun hashCode(): Int = atomic.hashCode()
}

/**
 * Create an instance of [AtomicInt]
 */
public actual fun atomic(value: Int): AtomicInt =
    AtomicIntImpl(AtomicInteger(value))


private class AtomicIntImpl(private val atomic: AtomicInteger) : AtomicInt {
    override var value: Int
        get() = atomic.get()
        set(value) {
            atomic.set(value)
        }

    override fun getAndSet(value: Int): Int =
        atomic.getAndSet(value)

    override fun incrementAndGet(delta: Int): Int =
        if (delta == 1) atomic.incrementAndGet() else atomic.addAndGet(delta)

    override fun decrementAndGet(delta: Int): Int =
        if (delta == 1) atomic.decrementAndGet() else atomic.addAndGet(-delta)

    override fun getAndIncrement(delta: Int): Int =
        if (delta == 1) atomic.getAndIncrement() else atomic.getAndAdd(delta)

    override fun getAndDecrement(delta: Int): Int =
        if (delta == 1) atomic.getAndDecrement() else atomic.getAndAdd(-delta)

    override fun compareAndSet(expect: Int, value: Int): Boolean = atomic.compareAndSet(expect, value)

    override fun compareAndExchange(expect: Int, value: Int): Int = atomic.compareAndExchange(expect, value)

    override fun toString(): String = atomic.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicIntImpl) return false

        if (atomic != other.atomic) return false

        return true
    }

    override fun hashCode(): Int = atomic.hashCode()
}

/**
 * Create an instance of [AtomicInt]
 */
public actual fun atomic(value: UInt): AtomicUInt =
    AtomicUIntImpl(AtomicInteger(value.toInt()))

private class AtomicUIntImpl(private val atomic: AtomicInteger) : AtomicUInt {
    override var value: UInt
        get() = atomic.get().toUInt()
        set(value) {
            atomic.set(value.toInt())
        }

    override fun getAndSet(value: UInt): UInt =
        atomic.getAndSet(value.toInt()).toUInt()

    override fun incrementAndGet(delta: UInt): UInt =
        if (delta == 1u) atomic.incrementAndGet().toUInt() else atomic.addAndGet(delta.toInt()).toUInt()

    override fun decrementAndGet(delta: UInt): UInt =
        if (delta == 1u) atomic.decrementAndGet().toUInt() else atomic.addAndGet(-delta.toInt()).toUInt()

    override fun getAndIncrement(delta: UInt): UInt =
        if (delta == 1u) atomic.getAndIncrement().toUInt() else atomic.getAndAdd(delta.toInt()).toUInt()

    override fun getAndDecrement(delta: UInt): UInt =
        if (delta == 1u) atomic.getAndDecrement().toUInt() else atomic.getAndAdd(-delta.toInt()).toUInt()

    override fun compareAndSet(expect: UInt, value: UInt): Boolean = atomic.compareAndSet(expect.toInt(), value.toInt())

    override fun compareAndExchange(expect: UInt, value: UInt): UInt =
        atomic.compareAndExchange(expect.toInt(), value.toInt()).toUInt()

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicUIntImpl) return false

        if (atomic != other.atomic) return false

        return true
    }

    override fun hashCode(): Int = atomic.hashCode()
}

/**
 * Create an instance of [AtomicULong]
 */
public actual fun atomic(value: ULong): AtomicULong =
    AtomicULongImpl(java.util.concurrent.atomic.AtomicLong(value.toLong()))

private class AtomicULongImpl(private val atomic: java.util.concurrent.atomic.AtomicLong) : AtomicULong {
    override var value: ULong
        get() = atomic.get().toULong()
        set(value) {
            atomic.set(value.toLong())
        }

    override fun getAndSet(value: ULong): ULong = atomic.getAndSet(value.toLong()).toULong()

    override fun incrementAndGet(delta: ULong): ULong =
        (if (delta == ONE) atomic.incrementAndGet() else atomic.addAndGet(delta.toLong())).toULong()

    override fun decrementAndGet(delta: ULong): ULong =
        (if (delta == ONE) atomic.decrementAndGet() else atomic.addAndGet(-delta.toLong())).toULong()

    override fun getAndIncrement(delta: ULong): ULong =
        (if (delta == ONE) atomic.getAndIncrement() else atomic.getAndAdd(delta.toLong())).toULong()

    override fun getAndDecrement(delta: ULong): ULong =
        (if (delta == ONE) atomic.getAndDecrement() else atomic.getAndAdd(-delta.toLong())).toULong()

    override fun compareAndSet(expect: ULong, value: ULong): Boolean =
        atomic.compareAndSet(expect.toLong(), value.toLong())

    override fun compareAndExchange(expect: ULong, value: ULong): ULong =
        atomic.compareAndExchange(expect.toLong(), value.toLong()).toULong()

    override fun toString(): String = value.toString()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicULongImpl) return false

        if (atomic != other.atomic) return false

        return true
    }

    override fun hashCode(): Int = atomic.hashCode()


    companion object {
        private const val ONE: ULong = 1u
    }
}

/**
 * Create an instance of [AtomicBoolean]
 */
public actual fun atomic(value: Boolean): AtomicBoolean =
    AtomicBooleanImpl(java.util.concurrent.atomic.AtomicBoolean(value))

private class AtomicBooleanImpl(private val atomic: java.util.concurrent.atomic.AtomicBoolean) : AtomicBoolean {
    override var value: Boolean
        get() = atomic.get()
        set(value) {
            atomic.set(value)
        }

    override fun getAndSet(value: Boolean) = atomic.getAndSet(value)

    override fun compareAndSet(expect: Boolean, value: Boolean): Boolean = atomic.compareAndSet(expect, value)

    override fun compareAndExchange(expect: Boolean, value: Boolean): Boolean =
        atomic.compareAndExchange(expect, value)

    override fun toString(): String = atomic.toString()
}

/**
 * Create an instance of [AtomicRef]<[T]>
 */
public actual fun <T> atomicRef(value: T): AtomicRef<T> =
    AtomicRefImpl(AtomicReference(value))

private class AtomicRefImpl<T>(private val atomic: AtomicReference<T>) : AtomicRef<T> {
    override var value: T
        get() = atomic.get()
        set(value) {
            atomic.set(value)
        }

    override fun getAndSet(value: T): T = atomic.getAndSet(value)

    override fun compareAndSet(expect: T, value: T): Boolean = atomic.compareAndSet(expect, value)

    override fun compareAndExchange(expect: T, value: T): T = atomic.compareAndExchange(expect, value)

    override fun toString(): String = atomic.toString()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AtomicRefImpl<*>) return false

        if (atomic != other.atomic) return false

        return true
    }

    override fun hashCode(): Int {
        return atomic.hashCode()
    }


}
