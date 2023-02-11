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


