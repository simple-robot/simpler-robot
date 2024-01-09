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

package love.forte.simbot.common.atomic

import kotlin.concurrent.AtomicReference

/**
 * Create an instance of [AtomicLong]
 */
public actual fun atomic(value: Long): AtomicLong = AtomicLongImpl(value)

private class AtomicLongImpl(value: Long) : AtomicLong {
    private val atomic = kotlin.concurrent.AtomicLong(value)

    override var value: Long by atomic::value

    override fun getAndSet(value: Long): Long = atomic.getAndSet(value)

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
}

/**
 * Create an instance of [AtomicInt]
 */
public actual fun atomic(value: Int): AtomicInt = AtomicIntImpl(value)

private class AtomicIntImpl(value: Int) : AtomicInt {
    private val atomic = kotlin.concurrent.AtomicInt(value)

    override var value: Int by atomic::value

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
}

/**
 * Create an instance of [AtomicInt]
 */
public actual fun atomic(value: UInt): AtomicUInt = AtomicUIntImpl(value)

private class AtomicUIntImpl(value: UInt) : AtomicUInt {
    private val atomic = kotlin.concurrent.AtomicInt(value.toInt())

    override var value: UInt
        get() = atomic.value.toUInt()
        set(value) {
            atomic.value = value.toInt()
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

    override fun toString(): String = atomic.value.toUInt().toString()
}

/**
 * Create an instance of [AtomicULong]
 */
public actual fun atomic(value: ULong): AtomicULong = AtomicULongImpl(value)

private class AtomicULongImpl(value: ULong) : AtomicULong {
    private val atomic = kotlin.concurrent.AtomicLong(value.toLong())

    override var value: ULong
        get() = atomic.value.toULong()
        set(value) {
            atomic.value = value.toLong()
        }

    override fun getAndSet(value: ULong): ULong = atomic.getAndSet(value.toLong()).toULong()

    override fun incrementAndGet(delta: ULong): ULong =
        if (delta == ONE) atomic.incrementAndGet().toULong() else atomic.addAndGet(delta.toLong()).toULong()

    override fun decrementAndGet(delta: ULong): ULong =
        if (delta == ONE) atomic.decrementAndGet().toULong() else atomic.addAndGet(-delta.toLong()).toULong()

    override fun getAndIncrement(delta: ULong): ULong =
        if (delta == ONE) atomic.getAndIncrement().toULong() else atomic.getAndAdd(delta.toLong()).toULong()

    override fun getAndDecrement(delta: ULong): ULong =
        if (delta == ONE) atomic.getAndDecrement().toULong() else atomic.getAndAdd(-delta.toLong()).toULong()

    override fun compareAndSet(expect: ULong, value: ULong): Boolean =
        atomic.compareAndSet(expect.toLong(), value.toLong())

    override fun compareAndExchange(expect: ULong, value: ULong): ULong =
        atomic.compareAndExchange(expect.toLong(), value.toLong()).toULong()

    override fun toString(): String = atomic.value.toULong().toString()

    companion object {
        private const val ONE: ULong = 1u
    }
}

/**
 * Create an instance of [AtomicBoolean]
 */
public actual fun atomic(value: Boolean): AtomicBoolean = AtomicBooleanImpl(value)

private class AtomicBooleanImpl(value: Boolean) : AtomicBoolean {
    private val atomic = kotlin.concurrent.AtomicInt(value.toBInt())
    override var value: Boolean
        get() = atomic.value.toBool()
        set(value) {
            atomic.value = value.toBInt()
        }

    override fun getAndSet(value: Boolean): Boolean = atomic.getAndSet(value.toBInt()).toBool()

    override fun compareAndSet(expect: Boolean, value: Boolean): Boolean =
        atomic.compareAndSet(expect.toBInt(), value.toBInt())

    override fun compareAndExchange(expect: Boolean, value: Boolean): Boolean =
        atomic.compareAndExchange(expect.toBInt(), value.toBInt()).toBool()

    override fun toString(): String = atomic.value.toBool().toString()

    companion object {
        const val FALSE = 0
        const val TRUE = 1
        fun Boolean.toBInt() = if (this) TRUE else FALSE
        fun Int.toBool() = this == TRUE
    }
}

/**
 * Create an instance of [AtomicRef]<[T]>
 */
public actual fun <T> atomicRef(value: T): AtomicRef<T> = AtomicRefImpl(value)

private class AtomicRefImpl<T>(value: T) : AtomicRef<T> {
    private val atomic = AtomicReference(value)
    override var value: T by atomic::value

    override fun getAndSet(value: T): T = atomic.getAndSet(value)

    override fun compareAndSet(expect: T, value: T): Boolean = atomic.compareAndSet(expect, value)

    override fun compareAndExchange(expect: T, value: T): T = atomic.compareAndExchange(expect, value)

    override fun toString(): String = atomic.value.toString()
}
