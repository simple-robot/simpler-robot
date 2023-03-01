/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("Views")

package love.forte.simbot.utils.view


/**
 * 构建一个当前 [List] 对应的 [View]。
 */
public fun <T> List<T>.asView(): IndexAccessView<T> {
    if (this === emptyList<T>()) {
        return EmptyView
    }
    
    return ListView(this)
}


private class ListView<out T>(val list: List<T>) : IndexAccessView<T>, RandomAccess {
    override fun iterator(): Iterator<T> = list.iterator()
    
    override val size: Int
        get() = list.size
    
    override fun isEmpty(): Boolean = list.isEmpty()
    
    override fun contains(element: @UnsafeVariance T): Boolean = element in list
    
    override fun get(index: Int): T = list[index]
}

/**
 * 构建一个当前 [Collection] 对应的 [View]。
 * 如果当前集合类型为 [List], 则相当于 [List.asView]。
 */
public fun <T> Collection<T>.asView(): View<T> {
    return if (this is List) asView() else CollectionView(this)
}


private class CollectionView<out T>(val collection: Collection<T>) : View<T> {
    override fun iterator(): Iterator<T> = collection.iterator()
    
    override val size: Int
        get() = collection.size
    
    override fun isEmpty(): Boolean = collection.isEmpty()
    
    override fun contains(element: @UnsafeVariance T): Boolean = element in collection
}

/**
 * 构建一个当前 [Iterable] 对应的 [View]。
 * 如果当前类型为 [Collection], 则相当于 [Collection.asView]。
 *
 * 如果当前 [Iterable] 不属于集合类型或列表类型，
 * 那么得到的 [View] 中大多数操作都可能是直接依托于 [Iterable.iterator] 方法的，
 * 例如 [View.size], 每次获取都会进行一次遍历与计算。
 *
 */
public fun <T> Iterable<T>.asView(): View<T> {
    return when (this) {
        is List -> this.asView()
        is Collection -> this.asView()
        else -> IterableView(this)
    }
}

/**
 * 将 [View] 转化为 [List].
 */
public fun <T> View<T>.toList(): List<T> {
    return when (this) {
        is ListView -> list.toList()
        is CollectionView -> collection.toList()
        else -> (this as Iterable<T>).toList()
    }
}


private class IterableView<out T>(private val iterable: Iterable<T>) : View<T> {
    override fun iterator(): Iterator<T> = iterable.iterator()
    
    override val size: Int
        get() = iterable.count()
    
    override fun isEmpty(): Boolean = iterable.iterator().hasNext()
    
    override fun contains(element: @UnsafeVariance T): Boolean = iterable.any { it == element }
}

/**
 * 得到一个永远不会有内容的 [View]。
 */
public fun <T> emptyView(): IndexAccessView<T> = EmptyView

/**
 * 得到一个永远不会存在内容的 [View] 实现。
 */
private object EmptyView : IndexAccessView<Nothing>, RandomAccess {
    override fun iterator(): Iterator<Nothing> = EmptyIterator
    
    private object EmptyIterator : Iterator<Nothing> {
        override fun hasNext(): Boolean = false
        override fun next(): Nothing = throw NoSuchElementException()
    }
    
    override val size: Int
        get() = 0
    
    override fun isEmpty(): Boolean = true
    
    override fun contains(element: Nothing): Boolean = false
    
    override fun get(index: Int): Nothing =
        throw IndexOutOfBoundsException("Empty view doesn't contain element at index $index.")
}
