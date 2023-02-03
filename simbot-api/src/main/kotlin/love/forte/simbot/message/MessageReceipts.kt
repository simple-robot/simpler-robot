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

@file:JvmName("MessageReceipts")

package love.forte.simbot.message


/**
 * 将多个 [SingleMessageReceipt] 聚合为 [AggregatedMessageReceipt].
 *
 * @see SingleMessageReceipt
 * @see AggregatedMessageReceipt
 */
public fun List<SingleMessageReceipt>.aggregation(): AggregatedMessageReceipt {
    return when (size) {
        0 -> EmptyAggregatedMessageReceipt
        1 -> SingleElementAggregatedMessageReceipt(first())
        else -> ListAggregatedMessageReceipt(this)
    }
}

/**
 * 将多个 [SingleMessageReceipt] 聚合为 [AggregatedMessageReceipt].
 *
 * @see SingleMessageReceipt
 * @see AggregatedMessageReceipt
 */
public fun Collection<SingleMessageReceipt>.aggregation(): AggregatedMessageReceipt {
    if (this is List) {
        return aggregation()
    }
    
    return when (size) {
        0 -> EmptyAggregatedMessageReceipt
        1 -> SingleElementAggregatedMessageReceipt(first())
        else -> CollectionAggregatedMessageReceipt(this)
    }
}

/**
 * 将多个 [SingleMessageReceipt] 聚合为 [AggregatedMessageReceipt].
 *
 * @see SingleMessageReceipt
 * @see AggregatedMessageReceipt
 */
public fun Iterable<SingleMessageReceipt>.aggregation(): AggregatedMessageReceipt {
    if (this is List) {
        return aggregation()
    }
    
    if (this is Collection) {
        return aggregation()
    }
    
    return IterableAggregatedMessageReceipt(this)
}

/**
 * 没有元素的 [AggregatedMessageReceipt] 实现。
 */
private object EmptyAggregatedMessageReceipt : AggregatedMessageReceipt() {
    override fun iterator(): Iterator<SingleMessageReceipt> = EmptyIterator
    
    override val isSuccess: Boolean
        get() = false
    
    override val size: Int
        get() = 0
    
    override fun get(index: Int): SingleMessageReceipt {
        throw IndexOutOfBoundsException("Empty aggregated message receipt has no elements.")
    }
    
    private object EmptyIterator : Iterator<Nothing> {
        override fun hasNext(): Boolean = false
        override fun next(): Nothing = throw NoSuchElementException("Empty aggregated message receipt has no elements.")
    }
}

/**
 * 只有一个元素的 [AggregatedMessageReceipt] 实现。
 */
private class SingleElementAggregatedMessageReceipt(
    private val value: SingleMessageReceipt,
) : AggregatedMessageReceipt() {
    override fun iterator(): Iterator<SingleMessageReceipt> = SingleElementIterator(value)
    
    override val isSuccess: Boolean
        get() = value.isSuccess
    
    override val size: Int
        get() = 1
    
    override fun get(index: Int): SingleMessageReceipt {
        if (index != 0) {
            throw IndexOutOfBoundsException("Index $index of size 1")
        }
        
        return value
    }
    
    
    private class SingleElementIterator(value: SingleMessageReceipt) : Iterator<SingleMessageReceipt> {
        private var value: SingleMessageReceipt? = value
        
        override fun hasNext(): Boolean = value != null
        override fun next(): SingleMessageReceipt {
            return value?.also {
                value = null
            } ?: throw NoSuchElementException()
        }
    }
}

/**
 * 使用 [List] 作为 [SingleMessageReceipt] 集的载体。
 */
private class ListAggregatedMessageReceipt(
    private val list: List<SingleMessageReceipt>,
) : AggregatedMessageReceipt() {
    override fun iterator(): Iterator<SingleMessageReceipt> = list.iterator()
    
    override val isSuccess: Boolean
        get() = list.any { it.isSuccess }
    
    override val size: Int
        get() = list.size
    
    override fun get(index: Int): SingleMessageReceipt = list[index]
}


/**
 * 使用 [Collection] 作为 [SingleMessageReceipt] 集的载体。
 *
 * 与 [ListAggregatedMessageReceipt] 不同的是
 * [CollectionAggregatedMessageReceipt] 无法通过索引直接访问元素。
 */
private class CollectionAggregatedMessageReceipt(
    private val collection: Collection<SingleMessageReceipt>,
) : AggregatedMessageReceipt() {
    override fun iterator(): Iterator<SingleMessageReceipt> = collection.iterator()
    
    override val isSuccess: Boolean
        get() = collection.any { it.isSuccess }
    
    override val size: Int
        get() = collection.size
    
    override fun get(index: Int): SingleMessageReceipt {
        val size = collection.size
        if (index !in 0 until size) {
            throw IndexOutOfBoundsException("Index $index of size $size")
        }
        for ((currentIndex, receipt) in collection.withIndex()) {
            if (currentIndex == index) {
                return receipt
            }
        }
        
        throw IndexOutOfBoundsException("Index $index of size $size")
    }
}


/**
 * 使用 [Iterable] 作为 [SingleMessageReceipt] 集的载体。
 */
private class IterableAggregatedMessageReceipt(
    private val iterable: Iterable<SingleMessageReceipt>,
) : AggregatedMessageReceipt() {
    override fun iterator(): Iterator<SingleMessageReceipt> = iterable.iterator()
    
    override val isSuccess: Boolean
        get() = iterable.any { it.isSuccess }
    
    override val size: Int
        get() = iterable.count()
    
    override fun get(index: Int): SingleMessageReceipt {
        if (index < 0) {
            throw IndexOutOfBoundsException("Index $index of size $size")
        }
        for ((currentIndex, receipt) in iterable.withIndex()) {
            if (currentIndex == index) {
                return receipt
            }
        }
        
        throw IndexOutOfBoundsException("Index $index of size $size")
    }
}

