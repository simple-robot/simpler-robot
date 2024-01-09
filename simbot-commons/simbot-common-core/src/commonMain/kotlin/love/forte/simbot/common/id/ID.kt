/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.common.id

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UUID.Companion.UUID
import kotlin.concurrent.Volatile
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.random.Random


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
 * LongID longID = LongID.valueOf(value);
 * ULongID uLongID = ULongID.valueOf(value);
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
public sealed class ID : Comparable<ID> {

    /**
     * ID 的 **字面值** 字符串。
     *
     * @return 字面值字符串
     */
    abstract override fun toString(): String

    /**
     * ID 的源值 hashcode，等于对应的源值的 hashcode。
     *
     * 不同类型但字面值相同的ID可能会有不同的 hashCode，例如字符串ID `"1"` 和数字ID `1`。
     *
     */
    abstract override fun hashCode(): Int

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
    abstract override fun equals(other: Any?): Boolean

    /**
     * 判断另外一个 [ID] 是否与当前 [ID] **字面值与类型均相同**。
     *
     * 会同时比对类型与字面值，[equalsExact] 更类似于传统的 `equals` 逻辑。
     */
    public abstract fun equalsExact(other: Any?): Boolean

    /**
     * 复制一个当前ID。
     */
    public abstract fun copy(): ID
}


/**
 * 将ID视为字符串进行序列化/反序列化的序列化器。
 *
 * 会将任何 [ID] 序列化为 [String]；
 * 会将任何字符串反序列化为 [StringID]。
 *
 * 可以作为任意 [ID] 的通用序列化器。
 *
 */
public object AsStringIDSerializer : KSerializer<ID> {
    override fun deserialize(decoder: Decoder): StringID = decoder.decodeString().ID
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AsStringID", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ID) {
        encoder.encodeString(value.toString())
    }
}


/**
 * 一个使用字符串 [String] 作为ID值的 [ID] 实现。
 *
 * 任何 [ID] 类型都可以作为一个字符串类型进行序列化。如果希望通用化 [ID] ，无视它的具体类型而作为字符串进行序列化，
 * 参考序列化器 [AsStringIDSerializer]。
 *
 * @property value 字符串ID的源值
 */
@Serializable(with = StringID.Serializer::class)
public class StringID private constructor(public val value: String) : ID() {
    override fun toString(): String = value
    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean {
        return idCommonEq(other) {
            if (it is StringID) return value == it.value

            value == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean {
        return idExactEq(other) {
            value == it.value
        }
    }

    override fun copy(): StringID = value.ID

    /**
     * 使 [StringID] 与某任意类型的 [ID] 排序。
     *
     * 如果目标类型是 [StringID] 或 [UUID]，则通过字面值排序。
     * 否则（是 [NumericalID]）始终得到 `-1`。
     *
     */
    override fun compareTo(other: ID): Int {
        if (other is StringID) {
            return value.compareTo(other.value)
        }
        if (other is UUID) {
            return value.compareTo(other.toString())
        }

        return -1
    }

    public companion object {
        private val EMPTY = StringID("")

        /**
         * 通过 [String] 构建一个 [StringID].
         *
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        @get:JsName("valueOfString")
        public val String.ID: StringID get() = if (isEmpty()) EMPTY else StringID(this)

        /**
         * 通过 [CharSequence.toString] 构建一个 [StringID].
         *
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        @get:JsName("valueOfCharSequence")
        public val CharSequence.ID: StringID get() = if (isEmpty()) EMPTY else StringID(toString())
    }


    /**
     * 将ID视为字符串进行序列化/反序列化的序列化器。
     *
     * 会将任何 [ID] 序列化为 [String]；
     * 会将任何字符串反序列化为 [StringID]。
     *
     * 可以作为任意 [ID] 的通用序列化器。
     *
     */
    public object Serializer : KSerializer<StringID> {
        override fun deserialize(decoder: Decoder): StringID = decoder.decodeString().ID
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StringID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: StringID) {
            encoder.encodeString(value.toString())
        }

    }
}

/**
 * 一个不可变的 "universally unique identifier" (UUID) 。
 *
 * [UUID] 是一个128位的不可变唯一标识，由两个分别代表高低位的 [Long] 组成。
 *
 * > [UUID] 的实现逻辑大多数参考自 Java 的 `java.util.UUID`，在多平台化实现中进行了部分调整。
 *
 * see also: [RFC 4122: A Universally Unique IDentifier (UUID) URN Namespace](https://www.ietf.org/rfc/rfc4122.txt)
 *
 * @see java.util.UUID
 */
@Suppress("KDocUnresolvedReference")
@Serializable(with = UUID.Serializer::class)
public class UUID private constructor(
    public val mostSignificantBits: Long,
    public val leastSignificantBits: Long,
    string: String? = null
) : ID() {

    @Volatile
    private lateinit var s: String

    init {
        if (string != null) {
            s = string
        }
    }

    /**
     * 获取缓存的 toString 值。如果 [s] 尚未初始化，计算UUID的string值后初始化 [s]。
     * 初始化结果前后必然相同，因此计算并初始化的过程不会加锁，可能会导致多次计算。
     *
     */
    private val stringValue: String
        get() = if (::s.isInitialized) s else uuidString(mostSignificantBits, leastSignificantBits).also { s = it }

    /**
     * 与另一个 [ID] 进行比较。
     *
     * - 如果它同样是 [UUID]，则会通过 [mostSignificantBits] 和 [leastSignificantBits] 进行对比；
     * - 如果是一个 [NumericalID]，则 [UUID] 始终得到 `1` —— 128位数字的威力就是如此。
     * - 如果是一个 [StringID]，则会通过 [toString] 的值进行比较。
     * - 其他情况，始终得到 `-1`。
     *
     */
    override fun compareTo(other: ID): Int {
        return when (other) {
            is UUID -> {
                val c = mostSignificantBits.compareTo(other.mostSignificantBits)
                if (c != 0) return c

                leastSignificantBits.compareTo(other.leastSignificantBits)
            }

            is NumericalID -> 1
            is StringID -> stringValue.compareTo(other.value)
        }
    }

    /**
     * 得到 UUID 的字符串字面值。
     *
     * [UUID] 的字符串值在计算后会被缓存，后续获取将会直接得到结果。
     *
     * 但是不能保证计算次数，如果同时有多个线程访问，依然可能产生多次字符串计算，
     * 但是最终结果是相同的。
     *
     */
    override fun toString(): String = stringValue

    override fun hashCode(): Int {
        val hilo: Long = mostSignificantBits xor leastSignificantBits
        return (hilo shr 32).toInt() xor hilo.toInt()
    }

    /**
     * 判断与另一个目标是否为 [ID] 且字面值相同。
     *
     * - 如果同样是 [UUID]，没什么可说的，正常判断
     * - 如果是 [NumericalID]，始终得到 `false`
     * - 其他情况，通过 [toString] 进行判断。
     *
     */
    override fun equals(other: Any?): Boolean = idCommonEq(other) {
        when (it) {
            is NumericalID -> false
            is UUID -> mostSignificantBits == it.mostSignificantBits && leastSignificantBits == it.leastSignificantBits
            else -> stringValue == it.toString()
        }
    }

    /**
     * 判断另一个目标是否是 [UUID]，且值相同。
     */
    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        mostSignificantBits == it.mostSignificantBits && leastSignificantBits == it.leastSignificantBits
    }

    override fun copy(): ID = UUID(mostSignificantBits, leastSignificantBits, if (::s.isInitialized) s else null)

    public companion object {
        /**
         * 通过长度为 `16` 的随机字节数组生成 [UUID]。
         *
         * @throws IllegalArgumentException 长度不符合条件时
         */
        public fun fromData(data: ByteArray): UUID {
            require(data.size == 16) { "data must be 16 bytes in length" }
            var msb = 0L
            var lsb = 0L
            for (i in 0..<8) {
                msb = (msb shl 8) or (data[i].toLong() and 0xff)
            }
            for (i in 8..<16) {
                lsb = (lsb shl 8) or (data[i].toLong() and 0xff)
            }

            return from(msb, lsb)
        }

        /**
         * 通过高低64位数字构建 [UUID]
         */
        @JvmStatic
        @JvmName("valueOf")
        public fun from(mostSignificantBits: Long, leastSignificantBits: Long): UUID =
            UUID(mostSignificantBits, leastSignificantBits)

        /**
         * 通过随机数生成器得到一个随机的 [UUID]。
         */
        @JvmStatic
        @JvmOverloads
        public fun random(random: Random = Random): UUID = doRandom(random.nextBytes(16))


        private fun doRandom(data: ByteArray): UUID {
            data[6] = data[6] and 0x0f                    // clear version
            data[6] = data[6] or 0x40                     // set to version 4
            data[8] = data[8] and 0x3f                    // clear variant
            data[8] = (data[8].toInt() or 0x80).toByte()  // set to IETF variant
            return fromData(data)
        }

        /**
         * 通过一个UUID字符串解析为一个 [UUID]。
         *
         * @throws IllegalArgumentException 如果 receiver 不能作为 [UUID] 被解析
         *
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        public val String.UUID: UUID
            get() = toUUIDSigs(::UUID)
    }

    /**
     *
     * [UUID] 的序列化器，会将其作为字符串处理。
     *
     * @see UUID
     */
    public object Serializer : KSerializer<UUID> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUIDString", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): UUID {
            return decoder.decodeString().UUID
        }

        override fun serialize(encoder: Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }
    }

}

/**
 * 通过一个数字作为ID值的 [ID] 实现。主要有2个子抽象类型：
 *
 * - [SignedNumericID] 有符号数字
 * - [UnsignedNumericID] 无符号数字
 *
 * 并由此扩展为4个具体的 ID 类型，按照最大可表示的数值按顺序为：
 *
 * - [IntID] 32位有符号整型
 * - [UIntID] 32位无符号整型
 * - [LongID] 64位有符号整型
 * - [ULongID] 64位无符号整型
 *
 * 如果想要作为ID的数字已经超过64位无符号 ([ULongID]) 锁能表示的最大数字，
 * 那么建议使用其他类型来表示，例如字符串ID [StringID] 或 UUID ([UUID]，也可以算是一个128位数字的ID)
 *
 *
 * @see SignedNumericID
 * @see UnsignedNumericID
 * @see IntID
 * @see UIntID
 * @see LongID
 * @see ULongID
 */
public sealed class NumericalID : ID() {

    /**
     * 将数字值转化为 [Double]。类似于 [Number.toDouble]
     */
    public abstract fun toDouble(): Double

    /**
     * 将数字值转化为 [Float]。类似于 [Number.toFloat]
     */
    public abstract fun toFloat(): Float

    /**
     * 将数字值转化为 [Long]。类似于 [Number.toLong]
     */
    public abstract fun toLong(): Long

    /**
     * 将数字值转化为 [Int]。类似于 [Number.toInt]
     */
    public abstract fun toInt(): Int

    /**
     * 将数字值转化为 [Short]。类似于 [Number.toShort]
     */
    public abstract fun toShort(): Short

    /**
     * 将数字值转化为 [Byte]。类似于 [Number.toByte]
     */
    public abstract fun toByte(): Byte

    final override fun compareTo(other: ID): Int {
        return when (other) {
            is StringID -> 1
            is UUID -> -1
            is NumericalID -> compareNumber(other)
        }
    }

    protected abstract fun compareNumber(target: NumericalID): Int

    public companion object {
        internal const val INT_MAX_ON_UINT: UInt = 2147483647u // Int.MAX_VALUE.toUInt()
        internal const val INT_MAX_ON_ULONG: ULong = 2147483647u // Int.MAX_VALUE
        internal const val LONG_MAX_ON_ULONG: ULong = 9223372036854775807u // Long.MAX_VALUE.toULong()
    }
}

/**
 * 通过一个**有符号**数字作为ID值的 [NumericalID] 实现。
 *
 * - [IntID] 32位有符号整型
 * - [LongID] 64位有符号整型
 *
 * 如果想要作为ID的数字已经超过64位有符号 ([LongID]) 锁能表示的最大数字，
 * 那么建议使用其他类型来表示，例如 [ULongID] 或 [StringID]
 *
 *
 * @see IntID
 * @see LongID
 */
public sealed class SignedNumericID : NumericalID()

/**
 * 通过一个**无符号**数字作为ID值的 [NumericalID] 实现。
 *
 * - [UIntID] 32位无符号整型
 * - [ULongID] 64位无符号整型
 *
 * 如果想要作为ID的数字已经超过64位无符号 ([ULongID]) 锁能表示的最大数字，
 * 那么建议使用其他类型来表示，例如 [StringID]
 *
 * 在 Java 中对无符号数字的操作需要有些注意的地方。
 * 具体描述请参考 [ID] 的文档注释中的相关说明。
 *
 * @see UIntID
 * @see ULongID
 */
public sealed class UnsignedNumericID : NumericalID()


/**
 * 一个通过 [32位整型 (Int)][Int] 作为ID值的 [NumericalID] 实现。
 *
 * @property value 源值
 */
@Serializable(with = IntID.Serializer::class)
public class IntID private constructor(public val value: Int) : SignedNumericID() {
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toLong(): Long = value.toLong()
    override fun toInt(): Int = value
    override fun toShort(): Short = value.toShort()
    override fun toByte(): Byte = value.toByte()
    override fun toString(): String = value.toString()
    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = idCommonEq(other) {
        when (it) {
            is UUID -> false
            is NumericalID -> compareNumber(it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

    override fun compareNumber(target: NumericalID): Int = compareIntID(target)

    override fun copy(): IntID = IntID(value)

    public companion object {
        /**
         * 将一个 [Int] 转化为 [IntID]。
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        public val Int.ID: IntID
            get() = IntID(this)


    }

    /**
     * [IntID] 的序列化器。不出意外地将 [IntID] 作为 [Int] 进行序列化。
     */
    internal object Serializer : KSerializer<IntID> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntID", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): IntID = IntID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: IntID) {
            encoder.encodeInt(value.value)
        }

    }
}

/**
 * 一个通过 [32位无符号整型 (unsigned int)][UInt] 作为ID值的 [NumericalID] 实现。
 *
 * @property value 源值。
 * 对于不支持直接操作无符号类型的目标来说，可能需要使用额外手段操作。
 *
 * 例如 Java 中，需要借助 `java.lang.Integer` 中与无符号相关的API进行操作，
 * 比如 `java.lang.Integer.toUnsignedString`。
 */
@Serializable(with = UIntID.Serializer::class)
public class UIntID private constructor(@get:JvmName("getValue") public val value: UInt) : UnsignedNumericID() {
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toLong(): Long = value.toLong()
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toShort()
    override fun toByte(): Byte = value.toByte()
    override fun toString(): String = value.toString()
    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = idCommonEq(other) {
        when (it) {
            is UUID -> false
            is NumericalID -> compareNumber(it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

    override fun compareNumber(target: NumericalID): Int = compareUIntID(target)

    override fun copy(): UIntID = UIntID(value)

    public companion object {
        /**
         * 将一个 [UInt] 转化为 [UIntID]。
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        public val UInt.ID: UIntID
            get() = UIntID(this)

    }

    /**
     * [UIntID] 的序列化器。将 [UIntID] 作为 [UInt] 进行序列化。
     */
    internal object Serializer : KSerializer<UIntID> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UIntID", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): UIntID = UInt.serializer().deserialize(decoder).ID
        override fun serialize(encoder: Encoder, value: UIntID) {
            UInt.serializer().serialize(encoder, value.value)
        }
    }
}

/**
 * 一个通过 [64位整型 (Long)][Long] 作为ID值的 [NumericalID] 实现。
 *
 *  @property value 源值。
 */
@Serializable(with = LongID.Serializer::class)
public class LongID private constructor(public val value: Long) : SignedNumericID() {
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toLong(): Long = value
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toShort()
    override fun toByte(): Byte = value.toByte()
    override fun toString(): String = value.toString()
    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = idCommonEq(other) {
        when (it) {
            is UUID -> false
            is NumericalID -> compareNumber(it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

    override fun compareNumber(target: NumericalID): Int = compareLongID(target)

    override fun copy(): LongID = LongID(value)

    public companion object {
        /**
         * 将一个 [Long] 转化为 [LongID]。
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        public val Long.ID: LongID
            get() = LongID(this)

    }

    /**
     * [LongID] 的序列化器。不出意外地将 [LongID] 作为 [Long] 进行序列化。
     */
    public object Serializer : KSerializer<LongID> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LongID", PrimitiveKind.LONG)
        override fun deserialize(decoder: Decoder): LongID = decoder.decodeLong().ID
        override fun serialize(encoder: Encoder, value: LongID) {
            encoder.encodeLong(value.value)
        }
    }
}

/**
 * 一个通过 [64位无符号整型 (unsigned long)][ULong] 作为ID值的 [NumericalID] 实现。
 *
 * @property value 源值。
 * 对于不支持直接操作无符号类型的目标来说，可能需要使用额外手段操作。
 *
 * 例如 Java 中，需要借助 `java.lang.Long` 中与无符号相关的API进行操作，
 * 比如 `java.lang.Long.toUnsignedString`。
 */
@Serializable(with = ULongID.Serializer::class)
public class ULongID private constructor(@get:JvmName("getValue") public val value: ULong) : UnsignedNumericID() {
    override fun toDouble(): Double = value.toDouble()
    override fun toFloat(): Float = value.toFloat()
    override fun toLong(): Long = value.toLong()
    override fun toInt(): Int = value.toInt()
    override fun toShort(): Short = value.toShort()
    override fun toByte(): Byte = value.toByte()
    override fun toString(): String = value.toString()
    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = idCommonEq(other) {
        when (it) {
            is UUID -> false
            is NumericalID -> compareNumber(it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

    override fun compareNumber(target: NumericalID): Int = compareULongID(target)

    override fun copy(): ULongID = ULongID(value)

    public companion object {
        /**
         * 将一个 [ULong] 转化为 [ULongID]。
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        public val ULong.ID: ULongID
            get() = ULongID(this)
    }

    internal object Serializer : KSerializer<ULongID> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ULongID", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): ULongID = ULong.serializer().deserialize(decoder).ID

        override fun serialize(encoder: Encoder, value: ULongID) {
            ULong.serializer().serialize(encoder, value.value)
        }
    }
}

private fun IntID.compareIntID(target: NumericalID): Int = when (target) {
    is IntID -> this.value.compareTo(target.value)
    is LongID -> this.value.compareTo(target.value)
    is UIntID -> when {
        this.value < 0 || target.value > NumericalID.INT_MAX_ON_UINT -> -1
        else -> this.value.compareTo(target.value.toInt())
    }

    is ULongID -> when {
        this.value < 0 || target.value > NumericalID.INT_MAX_ON_ULONG -> -1
        else -> this.value.compareTo(target.toInt())
    }
}

private fun LongID.compareLongID(target: NumericalID): Int = when (target) {
    is IntID -> this.value.compareTo(target.value)
    is LongID -> this.value.compareTo(target.value)
    is UIntID -> this.value.compareTo(target.value.toLong())
    is ULongID -> when {
        this.value < 0L || target.value > NumericalID.LONG_MAX_ON_ULONG -> -1
        else -> this.value.compareTo(target.value.toLong())
    }
}

private fun UIntID.compareUIntID(target: NumericalID): Int = when (target) {
    is IntID -> when {
        target.value < 0 || this.value > NumericalID.INT_MAX_ON_UINT -> 1
        else -> this.value.toInt().compareTo(target.value)
    }

    is LongID -> this.value.toLong().compareTo(target.value)
    is UIntID -> this.value.compareTo(target.value)
    is ULongID -> this.value.compareTo(target.value)
}

private fun ULongID.compareULongID(target: NumericalID): Int = when (target) {
    is IntID -> when {
        target.value < 0 || this.value > NumericalID.INT_MAX_ON_ULONG -> 1
        else -> this.value.toInt().compareTo(target.value)
    }

    is LongID -> when {
        target.value < 0 || this.value > NumericalID.LONG_MAX_ON_ULONG -> 1
        else -> this.value.toLong().compareTo(target.value)
    }

    is UIntID -> this.value.compareTo(target.value)
    is ULongID -> this.value.compareTo(target.value)
}

private inline fun <reified T : ID> T.idCommonEq(other: Any?, orElse: T.(ID) -> Boolean): Boolean {
    if (other == null) return false
    if (other === this) return true
    if (other !is ID) return false

    return orElse(other)
}

private inline fun <reified T : ID> T.idExactEq(other: Any?, orElse: T.(T) -> Boolean): Boolean {
    if (other == null) return false
    if (other === this) return true
    if (other !is T) return false

    return orElse(other)
}

/**
 * 同 [ID.toString], 得到 [ID] 的字面值。
 *
 */
public inline val ID.literal: String
    get() = toString()
