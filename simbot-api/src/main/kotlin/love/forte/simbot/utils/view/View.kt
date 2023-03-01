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

package love.forte.simbot.utils.view


/**
 * 一个“视图”。视图的主要应用是对使用者提供一个只读的集合类型，类似于 [Collection]。
 * 但是它不实现 [Collection] 或相关接口，而是提供一些较为基础的、与 [Collection] **相似**的方法。
 *
 * [View] 中的元素可能是变化的，线程安全性也与其内部实际表示的原集合相关。
 * 但是这些变化对外界不可见，它不论面向什么平台（比如JVM平台）都是一种更安全的只读集合。
 *
 *
 * @author ForteScarlet
 */
public interface View<out T> : Iterable<T> {
    /**
     * 得到当前视图的迭代器。
     */
    override fun iterator(): Iterator<T>
    
    /**
     * 获取当前视图中的元素数量。
     */
    public val size: Int
    
    /**
     * 判断当前视图是否为空。
     */
    public fun isEmpty(): Boolean
    
    /**
     * 判断当前视图中是否包含指定元素。
     */
    public operator fun contains(element: @UnsafeVariance T): Boolean
}

/**
 * 代表为一个可以通过索引值访问任意元素的 [View] 类型。
 */
public interface IndexAccessView<out T> : View<T> {
    
    /**
     * 获取指定索引上的元素。索引值以0为始。
     * @throws IndexOutOfBoundsException 当访问的索引超出界限时
     *
     */
    public operator fun get(index: Int): T
}

/**
 * 判断当前视图是否不为空。
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> View<T>.isNotEmpty(): Boolean = !isEmpty()


