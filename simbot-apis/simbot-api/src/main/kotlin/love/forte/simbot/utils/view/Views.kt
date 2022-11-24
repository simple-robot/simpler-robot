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