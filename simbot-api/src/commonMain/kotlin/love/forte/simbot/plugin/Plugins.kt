/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("PluginUtil")
@file:JvmMultifileClass

package love.forte.simbot.plugin

import love.forte.simbot.common.collection.toImmutable
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 用于表示一组 [Plugin]。
 */
public interface Plugins : Collection<Plugin>

/**
 * 根据类型寻找某个 [Plugin]。
 */
public inline fun <reified P : Plugin> Plugins.find(): P? = find { it is P } as P?

/**
 * 根据类型寻找某个 [Plugin]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified P : Plugin> Plugins.get(): P = find<P>() ?: throw NoSuchElementException(P::class.toString())

/**
 * 将一个 [Plugin] 的集合转化为 [Plugins]。
 */
public fun Collection<Plugin>.toPlugins(): Plugins = CollectionPlugins(toImmutable())

/**
 * @see Plugins
 */
private class CollectionPlugins(private val collections: Collection<Plugin>) : Plugins,
    Collection<Plugin> by collections {
    override fun toString(): String = "Plugins(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionPlugins) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }
}
