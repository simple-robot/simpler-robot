/*
 *     Copyright (c) 2021-2024. ForteScarlet.
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

@file:JvmName("Attributes")

package love.forte.simbot.common.attribute

import kotlinx.serialization.Serializable
import love.forte.simbot.common.collection.computeValueIfAbsent
import love.forte.simbot.common.collection.computeValueIfPresent
import love.forte.simbot.common.collection.mergeValue
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 一个属性。
 *
 * 此类型通常使用在事件处理中的上下文使用。
 * [Attribute] 拥有一个 [属性名][name], 且**不会**真实保留 [T] 类型信息。
 *
 * [Attribute] 的 [Attribute.hashcode] 将会直接与 [name] 一致，因此可以直接将 [Attribute] 作为一个 [Map] 的 Key,
 * 并且在进行 [Attribute.equals] 比较的时候，会对 [name] 进行比较。
 *
 * **Note: 由于 [Attribute] 不保留任何类型信息，因此在使用 [Attribute] 进行类型转化的时候均为非检转化。你需要更严谨地使用此类型以避免出现类型转化异常。**
 *
 * @see AttributeMap
 *
 * @property name 属性的名称
 *
 */
@Serializable
public class Attribute<T : Any> private constructor(public val name: String) {
    private val hashcode: Int get() = name.hashCode()

    override fun hashCode(): Int = hashcode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute<*>) return false

        return name == other.name
    }

    override fun toString(): String = "Attribute(name=$name)"

    public companion object {
        /**
         * 构建一个 [Attribute] 实例。
         *
         * Kotlin:
         * ```kotlin
         * val attribute = Attribute.of<Foo>("foo")
         * ```
         *
         * Java:
         * ```java
         * final Attribute<Foo> foo = Attribute.of("foo");
         * final Attribute<Bar> bar = Attribute.of("bar", Bar.class);
         *  Attribute.<Tar>of("tar"); // anonymous
         * ```
         *
         */
        @JvmStatic
        public fun <T : Any> of(name: String): Attribute<T> = Attribute(name)
    }
}

/**
 * 构建一个 [Attribute] 实例。
 *
 * ```kotlin
 * val attribute = attribute<Foo>("foo")
 * ```
 */
public inline fun <reified T : Any> attribute(name: String): Attribute<T> = Attribute.of(name)

/**
 * 一个通过 [Attribute] 作为键值来界定其元素类型的映射表。
 *
 * [AttributeMap] 内部类似于以名称字符串为键的映射表，
 * 但是 [AttributeMap] 不需要通过泛型指定具体的值元素类型，
 * 而是通过作为键的 [Attribute] 来决定元素类型。
 *
 * [AttributeMap] 不允许存入null值（[Attribute] 的泛型约束）。
 *
 * [Attribute] 本身不记录类型信息，因此 [AttributeMap] 也无法校验类型信息。
 * 请避免置入两个名称相同但是元素值不同的键值对，这会导致置入或获取时会出现 [ClassCastException] 异常。
 *
 * example:
 * ```kotlin
 * class Foo
 *
 * fun test() {
 *      val fooAttr = attribute<Foo>("foo")
 *
 *      val map = attributeMutableMapOf()
 *      val foo = Foo()
 *      map[fooAttr] = foo
 *      val foo1 = map[fooAttr]!!
 *      val foo2 = map[attribute<Foo>("foo")]!! // by a new instance
 *
 *      println(foo1 === foo2) // true
 *  }
 * ```
 *
 * @see attributeMapOf
 * @see MutableAttributeMap
 */
public interface AttributeMap {

    /**
     * 通过 [attribute] 得到对应的数据。
     *
     * @throws ClassCastException 如果存在对应名称但是类型不匹配的键与值。
     */
    public operator fun <T : Any> get(attribute: Attribute<T>): T?

    /**
     * 通过 [attribute] 得到对应的数据，但是不进行类型转化。
     */
    public fun safeGet(attribute: Attribute<*>): Any?

    /**
     * 判断是否存在对应键名与类型的键。
     */
    public operator fun contains(attribute: Attribute<*>): Boolean

    /**
     * 数量
     */
    public fun size(): Int

    /**
     * 得到所有的键值对
     */
    public val entries: Set<Map.Entry<Attribute<*>, Any>>

    public companion object {
        /**
         * 构建一个 [AttributeMap]。
         */
        @JvmOverloads
        @JvmStatic
        public fun create(source: Map<Attribute<*>, Any> = emptyMap()): AttributeMap =
            attributeMapOf(source)
    }
}

/**
 * 构建一个 [AttributeMap]。
 */
public fun attributeMapOf(source: Map<Attribute<*>, Any> = emptyMap()): AttributeMap =
    if (source.isEmpty()) EmptyAttributeMap else AttributeMapImpl(source)


private object EmptyAttributeMap : AttributeMap {
    override fun <T : Any> get(attribute: Attribute<T>): T? = null
    override fun safeGet(attribute: Attribute<*>): Any? = null
    override fun contains(attribute: Attribute<*>): Boolean = false
    override fun size(): Int = 0
    override val entries: Set<Map.Entry<Attribute<*>, Any>>
        get() = emptySet()
}

/**
 * 一个包含 [AttributeMap] 的容器。
 */
public interface AttributeMapContainer {
    /**
     * An [AttributeMap].
     */
    public val attributeMap: AttributeMap
}

/**
 * [MutableAttributeMap] 是 [AttributeMap] 的子类型，代表一个允许变化的 [AttributeMap]。
 * 类似于 [Map] 与 [MutableMap] 之间的关系。
 *
 * @see AttributeMap
 * @see mutableAttributeMapOf
 */
public interface MutableAttributeMap : AttributeMap {
    /**
     * 得到所有的键值对
     */
    override val entries: MutableSet<MutableMap.MutableEntry<Attribute<*>, Any>>

    /**
     * 存入一个值。
     *
     * @throws ClassCastException 如果类型不匹配
     *
     * @return 返回被顶替的结果. 如果没有被顶替内容, 得到null。
     */
    public fun <T : Any> put(attribute: Attribute<T>, value: T): T?

    /**
     * 存入一个值，并得到被顶替的旧值。但是不对旧值进行类型转化。
     *
     * @return 返回被顶替的结果. 如果没有被顶替内容, 得到null。
     */
    public fun <T : Any> safePut(attribute: Attribute<T>, value: T): Any?

    /**
     * 存入值，当值已经存在的时候进行合并处理。
     *
     * @throws ClassCastException 如果类型不匹配
     */
    public fun <T : Any> merge(attribute: Attribute<T>, value: T, remapping: (T, T) -> T): T

    /**
     * 存入值，当值已经存在的时候进行合并处理。
     * 从 [MutableAttributeMap] 中检索得到的值与返回值均不进行类型转化。
     */
    public fun <T : Any> safeMerge(attribute: Attribute<T>, value: T, remapping: (Any, T) -> T): Any

    /**
     * 如果不存在，则计算并存入。
     *
     * @throws ClassCastException 如果类型不匹配
     */
    public fun <T : Any> computeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): T

    /**
     * 如果不存在，则计算并存入，且不对返回值进行类型转化。
     */
    public fun <T : Any> safeComputeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): Any

    /**
     * 如果存在，则计算。如果计算函数中返回null，则为移除对应结果。
     *
     * @throws ClassCastException 如果类型不匹配
     */
    public fun <T : Any> computeIfPresent(attribute: Attribute<T>, remappingFunction: (Attribute<T>, T) -> T?): T?

    /**
     * 如果存在，则计算。如果计算函数中返回null，则为移除对应结果，
     * 且不对从 [MutableAttributeMap] 中检索得到的结果和返回值进行类型转化。
     *
     * @throws ClassCastException 如果类型不匹配
     */
    public fun <T : Any> safeComputeIfPresent(attribute: Attribute<T>, remappingFunction: (Attribute<T>, Any) -> T?): Any?

    /**
     * 移除对应键名的值。
     *
     * @throws ClassCastException 如果类型不匹配
     */
    public fun <T : Any> remove(attribute: Attribute<T>): T?
    /**
     * 移除对应键名的值。不对返回值进行类型转化。
     */
    public fun safeRemove(attribute: Attribute<*>): Any?

    public companion object {

        @JvmStatic
        public fun create(sourceMap: MutableMap<Attribute<*>, Any> = mutableMapOf()): MutableAttributeMap =
            mutableAttributeMapOf(sourceMap)
    }
}

/**
 * 构建 [MutableAttributeMap]。
 */
public fun mutableAttributeMapOf(sourceMap: MutableMap<Attribute<*>, Any> = mutableMapOf()): MutableAttributeMap =
    MutableAttributeMapImpl(sourceMap)

/**
 * `set` operator function for [MutableAttributeMap]。
 */
public operator fun <T : Any> MutableAttributeMap.set(attribute: Attribute<T>, value: T) {
    put(attribute, value)
}


private open class AttributeMapImpl(protected open val map: Map<Attribute<*>, Any>) : AttributeMap {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(attribute: Attribute<T>): T? {
        val value = safeGet(attribute) ?: return null
        return value as T
    }

    override fun safeGet(attribute: Attribute<*>): Any? = map[attribute]

    override fun contains(attribute: Attribute<*>): Boolean = map.containsKey(attribute)

    override fun size(): Int = map.size

    override val entries: Set<Map.Entry<Attribute<*>, Any>>
        get() = map.entries

    override fun toString(): String = map.toString()
    override fun hashCode(): Int = map.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttributeMapImpl) return false

        if (map != other.map) return false

        return true
    }
}

private class MutableAttributeMapImpl(override val map: MutableMap<Attribute<*>, Any>) : AttributeMapImpl(map),
    MutableAttributeMap {
    override val entries: MutableSet<MutableMap.MutableEntry<Attribute<*>, Any>>
        get() = map.entries

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> put(attribute: Attribute<T>, value: T): T? {
        return safePut(attribute, value)?.let { it as T }
    }

    override fun <T : Any> safePut(attribute: Attribute<T>, value: T): Any? =
        map.put(attribute, value)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> merge(attribute: Attribute<T>, value: T, remapping: (T, T) -> T): T {
        val newValue = map.mergeValue(attribute, value) { old, now ->
            old as T; now as T
            remapping(old, now)
        }
        return newValue as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> safeMerge(attribute: Attribute<T>, value: T, remapping: (Any, T) -> T): Any {
        return map.mergeValue(attribute, value) { old, now ->
            now as T
            remapping(old, now)
        }!!
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> computeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): T {
        val value = map.computeValueIfAbsent(attribute) { k ->
            mappingFunction(k as Attribute<T>)
        }
        return value as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> safeComputeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): Any {
        return map.computeValueIfAbsent(attribute) { k ->
            mappingFunction(k as Attribute<T>)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> computeIfPresent(attribute: Attribute<T>, remappingFunction: (Attribute<T>, T) -> T?): T? {
        val value = map.computeValueIfPresent(attribute) { k, old ->
            k as Attribute<T>
            remappingFunction(k, old as T)
        }

        return value?.let { it as T }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> safeComputeIfPresent(
        attribute: Attribute<T>,
        remappingFunction: (Attribute<T>, Any) -> T?
    ): Any? {
        return map.computeValueIfPresent(attribute) { k, old ->
            k as Attribute<T>
            remappingFunction(k, old)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> remove(attribute: Attribute<T>): T? {
        return safeRemove(attribute)?.let { it as T }
    }

    override fun safeRemove(attribute: Attribute<*>): Any? {
        return map.remove(attribute)
    }
}
