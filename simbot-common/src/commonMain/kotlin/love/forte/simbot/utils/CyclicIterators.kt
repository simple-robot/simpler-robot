/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.utils

import love.forte.simbot.Simbot
import love.forte.simbot.SimbotIllegalArgumentException


/**
 * 无限循环的环形迭代器。[CyclicIterator] 将会永远拥有结果 ([CyclicIterator.hasNext]  始终为 `true`) 。
 *
 */
public abstract class CyclicIterator<T> : Iterator<T> {
    override fun hasNext(): Boolean = true
    abstract override fun next(): T
}

private class FunctionalCyclicIterator<T>(private val nextValue: () -> T) : CyclicIterator<T>() {
    override fun next(): T = nextValue()
}


private class ConstIterator<T>(private val value: T) : CyclicIterator<T>() {
    override fun next(): T = value
}

/**
 * 提供一个索引上限，并通过 [provider] 根据此索引来产生数据.
 *
 * 索引起始值为 `0`.
 *
 * @throws IllegalArgumentException if lastIndex <= 0
 */
public fun <T> indexedCycleIterator(lastIndex: Int, provider: (Int) -> T): Iterator<T> {
    Simbot.require(lastIndex > 0) { "lastIndex must > 0, but $lastIndex" }
    
    var i = 0
    fun getArrayValue(): T {
        return provider(i++).also {
            if (i > lastIndex) i = 0
        }
    }
    
    return FunctionalCyclicIterator(::getArrayValue)
}

/**
 * 提供一个索引区间，并通过 [provider] 根据此索引来产生数据。
 *
 * @throws IllegalArgumentException if [indexRange] is empty.
 */
public fun <T> rangedCycleIterator(indexRange: IntRange, provider: (Int) -> T): Iterator<T> {
    Simbot.require(!indexRange.isEmpty()) { "indexRange cannot be empty" }
    return IntTransformCyclicIterator(indexRange::iterator, provider)
}

/**
 * 提供一个索引区间，并通过 [provider] 根据此索引来产生数据。
 *
 * @throws IllegalArgumentException if [indexRange] is empty.
 */
public fun <T> rangedCycleIterator(indexRange: LongRange, provider: (Long) -> T): Iterator<T> {
    Simbot.require(!indexRange.isEmpty()) { "indexRange cannot be empty" }
    return LongTransformCyclicIterator(indexRange::iterator, provider)
}

/**
 * 将一个 [Array] 转化为 [CyclicIterator].
 *
 * @throws IllegalArgumentException if array is empty.
 */
public fun <T> Array<T>.asCycleIterator(): Iterator<T> {
    return when {
        isEmpty() -> throw SimbotIllegalArgumentException("array cannot be empty")
        size == 1 -> constIterator(first())
        else -> indexedCycleIterator(lastIndex, ::get)
    }
}

/**
 * 将一个 [List] 转化为 [CyclicIterator].
 *
 * @throws IllegalArgumentException if list is empty.
 */
public fun <T> List<T>.asCycleIterator(): Iterator<T> {
    return when {
        isEmpty() -> throw SimbotIllegalArgumentException("list cannot be empty")
        size == 1 -> constIterator(first())
        else -> indexedCycleIterator(lastIndex, toList()::get)
    }
}

/**
 * 将一个 [Iterable] 转化为 [CyclicIterator].
 *
 * 每次产生新的迭代器时都会进行检测，如果新的迭代器为空，则会抛出 [IllegalArgumentException].
 *
 * @throws IllegalArgumentException if iterator is empty.
 */
public fun <T> Iterable<T>.asCycleIterator(): Iterator<T> {
    fun newIterator(): Iterator<T> = iterator().also {
        Simbot.require(it.hasNext()) { "Initially iterator was empty." }
    }
    
    var iter = newIterator()
    
    fun getValue(): T {
        if (!iter.hasNext()) iter = newIterator()
        return iter.next()
    }
    
    return FunctionalCyclicIterator(::getValue)
}


public fun <T> constIterator(value: T): Iterator<T> = ConstIterator(value)


private class IntTransformCyclicIterator<T>(
    private val creator: () -> IntIterator,
    private val transformer: (Int) -> T,
) : CyclicIterator<T>() {
    private var iter = creator()
    override fun hasNext(): Boolean = true
    
    private fun next0(): Int {
        if (!iter.hasNext()) {
            iter = creator()
        }
        return iter.next()
    }
    
    override fun next(): T = transformer(next0())
}


private class LongTransformCyclicIterator<T>(
    private val creator: () -> LongIterator,
    private val transformer: (Long) -> T,
) : CyclicIterator<T>() {
    private var iter = creator()
    override fun hasNext(): Boolean = true
    
    private fun next0(): Long {
        if (!iter.hasNext()) {
            iter = creator()
        }
        return iter.next()
    }
    
    override fun next(): T = transformer(next0())
}

