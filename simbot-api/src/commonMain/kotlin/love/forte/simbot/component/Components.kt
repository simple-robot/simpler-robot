/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

@file:JvmName("ComponentUtil")
@file:JvmMultifileClass

package love.forte.simbot.component

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import love.forte.simbot.common.collection.toImmutable
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

/**
 * 用于表示一组 [Component] 。
 */
public interface Components : Collection<Component> {
    /**
     * 根据 [id] 寻找第一个匹配的 [Component]。
     */
    public fun findById(id: String): Component? = find { it.id == id }

    /**
     * 当前所有的组件内 [Component.serializersModule] 的聚合产物。
     */
    public val serializersModule: SerializersModule
}

/**
 * 根据类型寻找某个 [Component]。
 */
public inline fun <reified C : Component> Components.find(): C? = find { it is C } as C?

/**
 * 根据类型寻找某个 [Component]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified C : Component> Components.get(): C =
    find<C>() ?: throw NoSuchElementException(C::class.toString())


/**
 * 将一个 [Component] 的集合转化为 [Components]。
 */
@JvmOverloads
public fun Collection<Component>.toComponents(
    parentSerializersModule: SerializersModule = EmptySerializersModule()
): Components =
    CollectionComponents(toImmutable(), parentSerializersModule)

/**
 * @see Components
 */
private class CollectionComponents(
    private val collections: Collection<Component>,
    parentSerializersModule: SerializersModule
) : Components,
    Collection<Component> by collections {
    override val serializersModule: SerializersModule =
        parentSerializersModule overwriteWith SerializersModule {
            collections.forEach { include(it.serializersModule) }
        }

    override fun toString(): String = "Components(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionComponents) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }


}
