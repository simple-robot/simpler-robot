package love.forte.simbot.utils

import love.forte.simbot.Simbot
import love.forte.simbot.SimbotIllegalArgumentException




/**
 * 无限循环的环形迭代器。[CyclicIterator] 将会永远拥有结果。
 *
 */
public abstract class CyclicIterator<T> : Iterator<T> {
    override fun hasNext(): Boolean = true
    abstract override fun next(): T
}

private class FunctionalCyclicIterator<T>(private val nextValue: () -> T) : Iterator<T> {
    override fun hasNext(): Boolean = true
    override fun next(): T = nextValue()
}


private class ConstIterator<T>(private val value: T) : Iterator<T> {
    override fun hasNext(): Boolean = true
    override fun next(): T = value
}


public fun <T> indexedCycleIterator(lastIndex: Int, valueGet: (Int) -> T): Iterator<T> {
    var i = 0
    fun getArrayValue(): T {
        return valueGet(i++).also {
            if (i > lastIndex) i = 0
        }
    }
    return FunctionalCyclicIterator(::getArrayValue)
}

public fun <T> Array<T>.asCycleIterator(): Iterator<T> {
    return when {
        isEmpty() -> throw SimbotIllegalArgumentException("array cannot be empty")
        size == 1 -> constIterator(first())
        else -> indexedCycleIterator(lastIndex, ::get)
    }
}

public fun <T> List<T>.asCycleIterator(): Iterator<T> {
    return when {
        isEmpty() -> throw SimbotIllegalArgumentException("list cannot be empty")
        size == 1 -> constIterator(first())
        else -> indexedCycleIterator(lastIndex, ::get)
    }
}


public fun <T> Iterable<T>.asCycleIterator(): Iterator<T> {
    fun newIterator(): Iterator<T> = iterator().also {
        Simbot.require(it.hasNext()) { "Initially iterator was empty." }
    }

    var iter = newIterator()
    Simbot.require(iter.hasNext()) { "Initially iterator was empty." }

    fun getValue(): T {
        if (!iter.hasNext()) iter = newIterator()
        return iter.next()
    }

    return FunctionalCyclicIterator(::getValue)
}


public fun <T> constIterator(value: T): Iterator<T> = ConstIterator(value)

