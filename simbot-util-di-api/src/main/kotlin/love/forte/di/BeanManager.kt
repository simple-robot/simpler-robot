/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.di

import kotlin.reflect.KClass
import kotlin.reflect.cast


/**
 * [Bean] 的注册器，
 * 用于通过对 [Bean] 的 [描述][BeanDescription] 向一个Bean管理器中注册一个 [Bean].
 */
public interface BeanRegistrar {

    /**
     * 注册一个 [Bean]。
     *
     * @throws BeanDefineAlreadyExistException [name] 已经存在的时候
     */
    public fun register(name: String, bean: Bean<*>)
}


/**
 * [Bean] 的容器，可通过此容器并根据名称或类型寻找一个或多个 [Bean].
 */
public interface BeanContainer {

    /**
     * 根据 [Bean] 的唯一名称得到其结果。
     *
     * @throws NoSuchBeanException 当bean不存在时
     */
    public operator fun get(name: String): Any = getOrNull(name) ?: noSuchBeanDefine { name }

    /**
     * 检测是否存在某个bean name。
     */
    public operator fun contains(name: String): Boolean

    /**
     * 根据的唯一限定0名称得到其结果, 或者得到null。
     */
    public fun getOrNull(name: String): Any?

    /**
     * 根据 [Bean] 的唯一名称得到其结果。
     *
     * @throws NoSuchBeanException 当bean不存在时
     * @throws ClassCastException 类型不匹配时
     */
    public operator fun <T : Any> get(name: String, type: KClass<T>): T = type.cast(this[name])

    /**
     * 根据的唯一限定0名称得到其结果, 或者得到null。
     *
     * @throws ClassCastException 类型不匹配时
     */
    public fun <T : Any> getOrNull(name: String, type: KClass<T>): T? = getOrNull(name)?.let(type::cast)


    @Api4J
    public operator fun <T : Any> get(name: String, type: Class<T>): T = type.cast(this[name])

    @Api4J
    public fun <T : Any> getOrNull(name: String, type: Class<T>): T? = getOrNull(name)?.let(type::cast)


    /**
     * 根据类型获取一个 [Bean]。类型会寻找所有相同类型以及其自类型的所有 [Bean].
     * [Bean] 根据 `name` 作为唯一限定，因此不能保证同一个类型下只存在一个实例，
     * 更何况类型下可能还存在子类型。
     *
     * @throws NoSuchBeanException 当此类型的bean不存在时
     * @throws MultiSameTypeBeanException 当此类型的bean存在多个时
     */
    public operator fun <T : Any> get(type: KClass<T>): T =
        getOrNull(type) ?: throw NoSuchBeanException("type of $type")


    /**
     * 根据类型获取一个 [Bean]。类型会寻找所有相同类型以及其自类型的所有 [Bean].
     * [Bean] 根据 `name` 作为唯一限定，因此不能保证同一个类型下只存在一个实例，
     * 更何况类型下可能还存在子类型。
     *
     * @throws MultiSameTypeBeanException 当此类型的bean存在多个时
     */
    public fun <T : Any> getOrNull(type: KClass<T>): T?


    /**
     * 根据类型获取此类型下的所有 [Bean] 的名称. 当无法找到任何结果的时候，会返回一个空列表。
     *
     * 如果 [type] == null, 则代表获取当前容器中全部内容。
     *
     */
    public fun <T : Any> getAll(type: KClass<T>? = null): List<String>


    @Api4J
    public operator fun <T : Any> get(type: Class<T>): T = this[type.kotlin]

    @Api4J
    public fun <T : Any> getOrNull(type: Class<T>): T? = getOrNull(type.kotlin)

    @Api4J
    public fun <T : Any> getAll(type: Class<T>? = null): List<String> = getAll(type?.kotlin)


    /**
     * 得到此名称对应Bean的类型。
     */
    public fun getType(name: String): KClass<*> = getTypeOrNull(name) ?: noSuchBeanDefine { name }

    /**
     * 根据一个Bean的唯一限定名称获取其对应的bean类型，如果没有此bean则得到null。
     */
    public fun getTypeOrNull(name: String): KClass<*>?

    /**
     * 得到此名称对应Bean的类型。
     */
    @Api4J
    public fun getTypeClass(name: String): Class<*> = getTypeClassOrNull(name) ?: noSuchBeanDefine { name }

    /**
     * 根据一个Bean的唯一限定名称获取其对应的bean类型，如果没有此bean则得到null。
     */
    @Api4J
    public fun getTypeClassOrNull(name: String): Class<*>? = getTypeOrNull(name)?.java


    public companion object Empty : BeanContainer {
        public override fun contains(name: String): Boolean = false
        public override fun getOrNull(name: String): Any? = null
        public override fun <T : Any> getOrNull(type: KClass<T>): T? = null
        public override fun <T : Any> getAll(type: KClass<T>?): List<String> = emptyList()
        override fun getTypeOrNull(name: String): KClass<*>? = null
    }
}


public inline fun <reified T : Any> BeanContainer.all(): List<String> = getAll(T::class)
public inline fun <reified T : Any> BeanContainer.allInstance(): List<T> =
    T::class.let { type -> getAll(type).map { name -> get(name, type) } }

@PublishedApi
internal inline val NULL_TYPE: KClass<*>?
    get() = null

public inline val BeanContainer.all: List<String> get() = this.getAll(NULL_TYPE)
public inline val BeanContainer.allInstance: List<Any> get() = all.map(::get)


/**
 * 存在嵌套关系的 [BeanContainer], 可能存在一个 [上层容器][parentContainer].
 */
public interface HierarchicalBeanContainer : BeanContainer {

    /**
     * 父级容器. 如果父级容器不为null，则所有的其他获取函数理论上都应优先检测此父级容器。
     */
    public val parentContainer: BeanContainer?


    /**
     * 检测在**当前**容器中是否存在指定名称的bean。
     */
    public fun containsLocal(name: String): Boolean


    public companion object Empty : HierarchicalBeanContainer, BeanContainer by BeanContainer {
        override val parentContainer: BeanContainer? get() = null
        override fun containsLocal(name: String): Boolean = false
    }
}


/**
 * 一个Bean管理器，用于管理、获取各种 bean.
 *
 * @author ForteScarlet
 */
public interface BeanManager : BeanRegistrar, BeanContainer


//

