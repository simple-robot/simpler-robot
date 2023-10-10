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

package love.forte.simbot.id

import kotlinx.serialization.Serializable
import love.forte.simbot.id.StringID.Companion.ID
import love.forte.simbot.id.UUID.Companion.UUID


/**
 * [ID] 是用于表示 _唯一标识_ 的不可变类值类型。
 *
 * [ID] 是一个密封类型，它提供的最终类型有：
 *
 * - [StringID]
 * - [UUID]
 * - [IntID]
 * - [LongID]
 * - [UIntID]
 * - [ULongID]
 *
 * 它们可以粗略的被归类为字符串类型（ [UUID] 的字面值表现为字符串）和数字类型。
 *
 * ### 序列化
 *
 * 所有**具体的**ID类型都是可序列化的，它们都会通过 Kotlinx serialization
 * 提供一个可作为**字面值**序列化的序列化器实现。
 *
 * 所有的ID类型都**不会**被序列化为结构体，例如 [UIntID] 会被直接序列化为一个数字:
 *
 * ```kotlin
 * @Serializable
 * data class Foo(val value: UIntID)
 * // 序列化结果: {"value": 123456}
 * ```
 *
 * ### 可排序的
 *
 * [ID] 实现 [Comparable] 并允许所有 [ID] 类型之间存在排序关系。
 * 具体的排序规则参考每个具体的 [ID] 类型的 [compareTo] 的文档说明。
 *
 * ### 字面值与 `toString`
 *
 * 一个 [ID] 所表示的字符串值即为其字面值，也就是 [ID.toString] 的输出结果。
 *
 * 对于 [StringID] 来说，字面值就是它内部的字符串的值。
 *
 * ```kotlin
 * val id = "abc".ID
 * // toString: abc
 * ```
 *
 * 对于 [UUID] 来说，字面值即为其内部 128 位数字通过一定算法计算而得到的具有规律且唯一的字符串值。
 *
 * ```kotlin
 * val id = UUID.random()
 * // toString: 817d2625-1c9b-4cc4-880e-5d6ba86a42b7
 * ```
 *
 * 对于各数字类型的ID [NumericalID] 来说，字面值即为数字转为字符串的值。
 *
 * ```kotlin
 * val iID: IntID = 1.ID     // toString: 1
 * val lID: LongID = 1L.ID   // toString: 1
 * val uiID: UIntID = 1u.ID  // toString: 1
 * val ul: ULong = 1u
 * val ulID: ULongID = ul.ID  // toString: 1
 * ```
 *
 * 在Java中需要尤其注意的是，一个相同的数值，
 * 使用无符号类型和有符号类型的ID在通过 `valueOf`
 * 构建的结果可能是不同的，获取到的 `value` 和字面值也可能是不同的。
 *
 * Java在操作无符号ID的时候需要注意使用相关的无符号API。
 * 以 `long` 为例：
 *
 * ```java
 * long value = -1;
 *
 * ongID longID = LongID.valueOf(value);
 * LongID uLongID = ULongID.valueOf(value);
 *
 * System.out.println(longID);  // -1
 * System.out.println(uLongID); // 18446744073709551615
 *
 * System.out.println(longID.getValue());  // -1
 * System.out.println(uLongID.getValue()); // -1
 * ```
 *
 * 如果希望得到一些符合预期的结果，你应该使用Java中的无符号相关API：
 *
 * ```java
 * long value = Long.parseUnsignedLong("18446744073709551615");
 * ULongID uLongID = ULongID.valueOf(value);
 * System.out.println(uLongID); // 18446744073709551615
 * System.out.println(Long.toUnsignedString(uLongID.getValue()));
 * // 18446744073709551615
 * ```
 *
 * ### `equals` 与 `hashCode`
 *
 * [ID] 下所有类型均允许互相通过 [ID.equals] 判断是否具有相同的 **字面值**。
 * [ID.equals] 实际上不会判断类型，因此如果两个不同类型的 [ID] 的字面值相同，
 * 例如值为 `"1"` 的 [StringID] 和值为 `1` 的 [IntID]，它们之间使用 [ID.equals]
 * 会得到 `true`。
 *
 * [ID] 作为一个"唯一标识"载体，大多数情况下，它的类型无关紧要。
 * 并且 [ID] 属性的提供者也应当以抽象类 [ID] 类型本身对外提供，
 * 而将具体的类型与构建隐藏在实现内部。
 *
 * ```kotlin
 * public interface Foo {
 *    val id: ID
 * }
 *
 * internal class FooImpl(override val id: ULongID) : Foo
 * ```
 *
 * 如果你希望严格匹配两个 [ID] 类型，而不是将它们视为统一的"唯一标识"，那么使用 [ID.equalsExact]
 * 来进行。[equalsExact] 会像传统数据类型的 `equals` 一样，同时判断类型与值。
 *
 * 由于 [ID] 类型之间通过 [equals] 会仅比较字面值，且对外应仅暴露 [ID] 本身，
 * 但是不同类型但字面值相同的 [ID] 的 [hashCode] 值可能并不相同，
 * 因此 [ID] 这个抽象类型本身 **不适合** 作为一种 hash Key, 例如作为 HashMap 的 Key:
 *
 * ```kotlin
 * // ❌Bad!
 * val map1 = hashMapOf<ID, String>("1".ID to "value 1", 1.ID to "also value 1")
 * // size: 2, values: {1=value 1, 1=also value 1}
 * // ❌Bad!
 * val uuid = UUID.random()
 * val strId = uuid.toString().ID
 * val map2 = hashMapOf<ID, String>(uuid to "UUID value", strId to "string ID value")
 * // size: 2, values: {2afb3d3e-d3f4-4c15-89ed-eec0e258d533=UUID value, 2afb3d3e-d3f4-4c15-89ed-eec0e258d533=string ID value}
 * ```
 *
 * 如果有必要，你应该使用一个**具体的**最终ID类型作为某种 hash key, 例如：
 *
 * ```kotlin
 * // ✔ OK.
 * val map = hashMapOf<IntID, String>(1.ID to "value 1", 1.ID to "also value 1")
 * // size: 1, values: {1=also value 1}
 * ```
 *
 * @author ForteScarlet
 */
@Serializable(with = AsStringIDSerializer::class)
public actual sealed class ID actual constructor() : Comparable<ID> {
    /**
     * ID 的字面值字符串。
     *
     * @return 字面值字符串
     */
    actual abstract override fun toString(): String

    /**
     * ID 的源值 hashcode，等于对应的源值的 hashcode。
     *
     * 不同类型但字面值相同的ID可能会有不同的 hashCode，例如字符串ID `"1"` 和数字ID `1`。
     */
    actual abstract override fun hashCode(): Int

    /**
     * 判断另外一个 [ID] 是否与当前 [ID] **字面值相同**。
     *
     * 任意类型的 ID 的 [equals] 应始终可以与其他任意类型的 [ID] 进行字面值比对。
     * 例如一个字面值为字符串 `"1"` 的 [ID] 与字面值是数字 `1` 的 [ID] 通过 [equals]
     * 进行比对，那么结果将会是 `true`。
     *
     * 如果希望在比对的时候连带类型进行比对，参考使用 [equalsExact]。
     *
     * @see equalsExact
     */
    actual abstract override fun equals(other: Any?): Boolean

    /**
     * 判断另外一个 [ID] 是否与当前 [ID] **字面值与类型均相同**。
     *
     * 会同时比对类型与字面值，[equalsExact] 更类似于传统的 `equals` 逻辑。
     */
    public actual abstract fun equalsExact(other: Any?): Boolean

    /**
     * 复制一个当前ID。
     */
    public actual abstract fun copy(): ID
}
