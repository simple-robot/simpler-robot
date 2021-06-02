/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:Suppress("unused")

package love.forte.simbot.utils

import love.forte.simbot.utils.StoneArray.Companion.EMPTY


/**
 * 一个数组的视图，但是不允许对数组进行任何修改。
 * @author ForteScarlet
 */
public class StoneArray<out T> internal constructor(private val view: Array<T>) : Iterable<T> {

    /**
     * 获取元素。
     */
    operator fun get(index: Int): T = view[index]

    /**
     * 数组长度.
     */
    public val size: Int get() = view.size

    /**
     * 拷贝一个数组的副本。
     */
    public fun copyArray(): Array<out T> = view.copyOf()


    override fun iterator(): Iterator<T> = view.iterator()

    internal companion object {
        internal val EMPTY = StoneArray<Any?>(emptyArray())
    }
}

public fun <T> StoneArray<T>.isEmpty() = size == 0
public fun <T> StoneArray<T>.isNotEmpty() = !isEmpty()


@Suppress("UNCHECKED_CAST")
public fun <T> stoneArrayOf(vararg elements: T): StoneArray<T> = if (elements.isEmpty()) EMPTY as StoneArray<T> else StoneArray(elements)

@Suppress("UNCHECKED_CAST")
public fun <T> Array<T>.asStoneArray(): StoneArray<T> = if (this.isEmpty()) EMPTY as StoneArray<T> else StoneArray(this)