/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */

@file:JvmName("Attributes")

package love.forte.simbot

import kotlinx.serialization.Serializable
import love.forte.simbot.SimbotComponent.name
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.reflect.full.safeCast


/*
 * 参考自 gradle-7.1 代码 org.gradle.api.attributes.Attribute, 下述原文LICENSE
 *
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 一个属性。
 *
 * 此类型通常使用在 [Component.attributes], 或者作为事件处理中的上下文使用。
 *
 * [Attribute] 拥有一个 [属性名][name] 和此属性对应的 [类型][type], 其中，
 *
 * 对于一个 [Attribute], 它的 [Attribute.hashcode] 将会直接与 [name] 一致，因此可以直接将 [Attribute] 作为一个 [Map] 的 Key.
 *
 * 但是在进行 [Attribute.equals] 比较的时候，会同时对 [name] 和 [type] 进行比较。
 *
 * @see AttributeMap
 *
 * @property name 属性的名称
 * @property type 属性对应的元素类型
 *
 */
@Serializable
public class Attribute<T : Any> private constructor(
    public val name: String,
    public val type: KClass<T>
) {
    private val hashcode: Int get() = name.hashCode()

    override fun hashCode(): Int = hashcode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute<*>) return false

        if (name != other.name) return false
        return type == other.type
    }

    override fun toString(): String = name

    public companion object {
        @Api4J
        @JvmStatic
        public fun <T : Any> of(name: String, type: Class<T>): Attribute<T> = of(name, type.kotlin)

        /**
         * @see attribute
         */
        @JvmSynthetic
        public fun <T : Any> of(name: String, type: KClass<T>): Attribute<T> = Attribute(name, type)
    }


}

@JvmSynthetic
public fun <T : Any> attribute(name: String, type: KClass<T>): Attribute<T> = Attribute.of(name, type)
public inline fun <reified T : Any> attribute(name: String): Attribute<T> = attribute(name, T::class)
public inline fun <reified T : Any> attribute(): Attribute<T> =
    with(T::class) { attribute(qualifiedName ?: name, this) }


/**
 * 一个通过 [Attribute] 作为键值来界定其元素类型的映射表。
 *
 * [AttributeMap]与名称字符串为键的映射表相比，其没有明确的值类型，取而代之的是通过 [Attribute] 来规定元素类型的。
 *
 *
 * [AttributeMap] 中的Key即为 [Attribute], 并且以类型作为 [equals] 条件之一，
 * 因此在使用 [AttributeMap] 的时候，应当避免出现相同 [Attribute.name] 但是 [Attribute.type] 不同的情况。
 *
 *
 * [AttributeMap] 不允许存入null值。
 *
 *
 * example:
 * ```kotlin
 * class Foo
 *
 * fun test() {
 *      val fooAttr = attribute<Foo>("foo")
 *
 *      val map = AttributeHashMap()
 *      val foo = Foo()
 *      map[fooAttr] = foo
 *      val foo1 = map[fooAttr]!!
 *      val foo2 = map[attribute<Foo>("foo")]!! // by a new instance
 *
 *      println(foo1 === foo2) // true
 *  }
 * ```
 *
 * @see AttributeMutableMap
 */
public interface AttributeMap : AttributeContainer {

    /**
     * 通过 [attribute] 得到对应的数据。
     *
     * @throws ClassCastException 如果存在对应名称但是类型不匹配的键与值。
     */
    public operator fun <T : Any> get(attribute: Attribute<T>): T?


    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? = get(attribute)

    /**
     * 判断是否存在对应键名与类型的键。
     */
    public fun <T : Any> contains(attribute: Attribute<T>): Boolean

    /**
     * 数量
     */
    public fun size(): Int

    public object Empty : AttributeMap {
        override fun <T : Any> get(attribute: Attribute<T>): T? = null
        override fun <T : Any> contains(attribute: Attribute<T>): Boolean = false
        override fun size(): Int = 0
    }
}


/**
 * [MutableAttributeMap] 是 [AttributeMap] 的子类型，代表一个允许变化的 [AttributeMap], 类似于 [Map] 与 [MutableMap] 之间的关系。
 */
public interface MutableAttributeMap : AttributeMap {

    /**
     * 存入一个值。
     *
     * @throws IllegalStateException 如果已经存在重名但是类型不同的键
     * @throws ClassCastException 如果类型不匹配
     *
     * @return 返回被顶替的结果. 如果没有被顶替内容, 得到null。
     */
    public fun <T : Any> put(attribute: Attribute<T>, value: T): T?


    /**
     * 存入值，当值已经存在的时候进行合并处理。
     *
     */
    public fun <T : Any> merge(attribute: Attribute<T>, value: T, remapping: (T, T) -> T): T


    /**
     * 如果不存在，则计算并存入。
     */
    public fun <T : Any> computeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): T

    /**
     * 如果存在，则计算。如果计算函数中返回null，则为移除对应结果。
     */
    public fun <T : Any> computeIfPresent(attribute: Attribute<T>, remappingFunction: (Attribute<T>, T) -> T?): T?

    /**
     * 移除对应键名的值。
     *
     * @throws ClassCastException 如果类型不匹配
     */
    public fun <T : Any> remove(attribute: Attribute<T>): T?
}


public operator fun <T : Any> MutableAttributeMap.set(attribute: Attribute<T>, value: T) {
    put(attribute, value)
}


public class AttributeMutableMap(private val values: MutableMap<Attribute<*>, Any> = mutableMapOf()) :
    MutableAttributeMap {


    public val entries: MutableSet<MutableMap.MutableEntry<Attribute<*>, Any>>
        get() = values.entries

    override fun size(): Int = values.size

    override fun <T : Any> get(attribute: Attribute<T>): T? {
        val got = values[attribute] ?: return null
        return attribute.type.cast(got)
    }

    override fun <T : Any> put(attribute: Attribute<T>, value: T): T? {
        val type = attribute.type
        return values.put(attribute, type.cast(value))?.let { type.safeCast(it) }
    }

    override fun <T : Any> merge(attribute: Attribute<T>, value: T, remapping: (T, T) -> T): T {
        val type = attribute.type
        val newValue = values.merge(attribute, value) { old, now ->
            val oldValue = type.cast(old)
            val nowValue = type.cast(now)
            remapping(oldValue, nowValue)
        }
        return type.cast(newValue)
    }

    override fun <T : Any> computeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): T {
        val type = attribute.type
        val value = values.computeIfAbsent(attribute) { k ->
            @Suppress("UNCHECKED_CAST")
            mappingFunction(k as Attribute<T>)
        }
        return type.cast(value)
    }

    override fun <T : Any> computeIfPresent(attribute: Attribute<T>, remappingFunction: (Attribute<T>, T) -> T?): T? {
        val type = attribute.type
        val value = values.computeIfPresent(attribute) { k, old ->
            @Suppress("UNCHECKED_CAST")
            k as Attribute<T>
            remappingFunction(k , k.type.cast(old))
        }
        return value?.let { type.cast(it) }
    }

    override fun <T : Any> contains(attribute: Attribute<T>): Boolean {
        return attribute in values
    }


    override fun <T : Any> remove(attribute: Attribute<T>): T? {
        val removedValue = values.remove(attribute) ?: return null
        return removedValue.let { attribute.type.safeCast(it) }
    }

    override fun toString(): String = values.toString()
}


/**
 * 一个 [Attribute] 容器，标记其允许获取属性。
 */
public interface AttributeContainer {

    /**
     * 通过 [attribute] 尝试获取指定属性。
     */
    public fun <T : Any> getAttribute(attribute: Attribute<T>): T?

}
