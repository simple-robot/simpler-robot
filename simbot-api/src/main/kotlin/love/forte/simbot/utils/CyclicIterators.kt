/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

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

private class FunctionalCyclicIterator<T>(private val nextValue: () -> T) : CyclicIterator<T>() {
    override fun next(): T = nextValue()
}


private class ConstIterator<T>(private val value: T) : CyclicIterator<T>() {
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

