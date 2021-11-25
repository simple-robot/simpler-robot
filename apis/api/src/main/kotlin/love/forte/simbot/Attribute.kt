/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("Attributes")

package love.forte.simbot

import kotlinx.serialization.Serializable
import love.forte.simbot.SimbotComponent.name
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast


/**
 * 一个属性。
 *
 * 此类型通常使用在 [Component.properties].
 *
 * 是api模块下为数不多使用了反射的地方。
 *
 *
 */

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

@Serializable
public class Attribute<T : Any> private constructor(
    public val name: String,
    public val type: KClass<T>
) {
    private val hashcode: Int = (name.hashCode() * 31) + type.hashCode()

    override fun hashCode(): Int = hashcode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute<*>) return false

        return if (name != other.name) return false
        else type == other.type
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
 * TODO 是否允许同名不同类型？
 *
 */
public interface AttributeMap {

    /**
     * 通过 [attribute] 得到对应的数据。
     *
     * @throws ClassCastException 如果存在对应名称但是类型不匹配的键与值。
     */
    public operator fun <T : Any> get(attribute: Attribute<T>): T?

    /**
     * 判断是否存在对应的键名
     */
    public fun contains(attributeName: String): Boolean


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
        override fun contains(attributeName: String): Boolean = false
        override fun <T : Any> contains(attribute: Attribute<T>): Boolean = false
        override fun size(): Int = 0
    }
}

public interface MutableAttributeMap : AttributeMap {

    /**
     * 存入一个值。
     *
     * @throws IllegalStateException 如果已经存在重名但是类型不同的键
     */
    public fun <T : Any> put(attribute: Attribute<T>, value: T): T?


    /**
     * 移除对应键名的值
     */
    public fun remove(attributeName: String): Any?


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


public class AttributeHashMap : MutableAttributeMap {
    private val names = mutableMapOf<String, Attribute<*>>()
    private val values = mutableMapOf<Attribute<*>, Any>()

    public val entries: MutableSet<MutableMap.MutableEntry<Attribute<*>, Any>>
        get() = values.entries

    override fun size(): Int = values.size

    override fun <T : Any> get(attribute: Attribute<T>): T? {
        val got = values[attribute] ?: return null
        return attribute.type.cast(got)
    }

    override fun <T : Any> put(attribute: Attribute<T>, value: T): T? {
        val key = names[attribute.name]
        println("key = $key")
        println("$key == $attribute : ${key == attribute}")
        if (key != null && key != attribute) {
            val nowValue = values[key]
            if (nowValue != null) {
                attribute.type.safeCast(nowValue)
                    ?: throw IllegalStateException("Type conflict: expected to be ${key.type}, but is ${attribute.type}")
            } else {
                names[attribute.name] = attribute
            }

            values[attribute] = value
            return null
        }

        return values.put(attribute, value).let {
            names[attribute.name] = attribute
            if (it != null) {
                attribute.type.cast(it)
            } else null
        }
    }

    override fun contains(attributeName: String): Boolean = attributeName in names

    override fun <T : Any> contains(attribute: Attribute<T>): Boolean {
        return names[attribute.name]?.equals(attribute) ?: false
    }

    override fun remove(attributeName: String): Any? {
        val removedKey = names.remove(attributeName) ?: return null
        return values.remove(removedKey)
    }

    override fun <T : Any> remove(attribute: Attribute<T>): T? {
        val removedKey = names.remove(attribute.name) ?: return null
        return values.remove(removedKey)?.let { attribute.type.cast(it) }
    }

    override fun toString(): String = values.toString()
}