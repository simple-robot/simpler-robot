/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("CollectionViewUtil")

package love.forte.simbot.utils

import org.jetbrains.annotations.UnmodifiableView

/**
 * 将目标列表实例转化为一个 [列表视图][ListView].
 */
public fun <T> List<T>.view(): @UnmodifiableView ListView<T> = ListView(this)



/**
 * 列表视图，对一个目标列表进行代理包装并作为一个 **只读** 视图使用。
 * 此视图不可修改，但是内部元素可能会随着原始的 [代理目标][delegate] 的变化而变化。
 *
 */
public class ListView<T>(private val delegate: List<T>) : List<T> by delegate {
    override fun toString(): String = delegate.toString()
    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other is ListView<*>) delegate == other.delegate
        else delegate == other
    }
    
}
