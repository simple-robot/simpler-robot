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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.id.StringID.Companion.ID
import love.forte.simbot.id.UUID.Companion.UUID
import love.forte.simbot.utils.Cloneable
import love.forte.simbot.utils.toUUIDSigs
import love.forte.simbot.utils.uuidString0
import kotlin.concurrent.Volatile
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.random.Random


/**
 *
 *
 *
 * @author ForteScarlet
 */
@Serializable(with = AsStringIDSerializer::class)
public expect sealed class ID() : Comparable<ID>, Cloneable {

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
 *
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

    override fun compareTo(other: ID): Int {
        if (other !is StringID) {
            return -1
        }

        return value.compareTo(other.value)
    }

    public companion object {
        private val EMPTY = StringID("")

        /**
         * 通过 [String] 构建一个 [StringID].
         *
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
        public val String.ID: StringID get() = if (isEmpty()) EMPTY else StringID(this)

        /**
         * 通过 [CharSequence] 构建一个 [StringID].
         *
         */
        @get:JvmStatic
        @get:JvmName("valueOf")
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
 * [UUID] 的实现逻辑大多数参考自 `java.util.UUID`，
 * 在多平台化实现中进行了部分调整。
 *
 * @see java.util.UUID
 * @see [RFC 4122: A Universally Unique IDentifier (UUID) URN Namespace](https://www.ietf.org/rfc/rfc4122.txt)
 */
@Suppress("KDocUnresolvedReference")
@Serializable(with = UUID.UUIDSerializer::class)
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
        get() = if (::s.isInitialized) s else uuidString0(mostSignificantBits, leastSignificantBits).also { s = it }

    /**
     * 与另一个 [ID] 进行比较。
     *
     * - 如果它同样是 [UUID]，则会通过 [mostSignificantBits] 和 [leastSignificantBits] 进行对比；
     * - 如果是一个 [NumericalID]，则 [UUID] 始终得到 `1` —— 128位数字的威力就是如此；
     * - 如果是一个 [StringID]，则会通过 [toString] 的值进行比较；
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
            else -> -1
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
        @get:JvmName("fromString")
        public val String.UUID: UUID
            get() = toUUIDSigs(::UUID)
    }


    public object UUIDSerializer : KSerializer<UUID> {
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
 * 通过一个数字作为ID值的 [ID] 实现。主要有4个子类型，按照最大可表示的数值按顺序为：
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
            is NumericalID -> compareNumber(this, other)
            else -> -1
        }
    }

    public companion object {
        internal const val INT_MAX_ON_UINT: UInt = 2147483647u // Int.MAX_VALUE.toUInt()
        internal const val INT_MAX_ON_LONG: Long = 2147483647L // Int.MAX_VALUE
        internal const val INT_MAX_ON_ULONG: ULong = 2147483647u // Int.MAX_VALUE

        internal const val LONG_MAX_ON_ULONG: ULong = 9223372036854775807u // Long.MAX_VALUE.toULong()

    }
}

/**
 * 一个通过 [32位整型 (Int)][Int] 作为ID值的 [NumericalID] 实现。
 *
 * @property value 源值
 */
public class IntID private constructor(public val value: Int) : NumericalID() {
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
            is NumericalID -> compareNumber(this, it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

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

    public object IntIDSerializer : KSerializer<IntID> {
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
public class UIntID private constructor(
    @get:JvmName("getValue") public val value: UInt
) : NumericalID() {
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
            is NumericalID -> compareNumber(this, it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

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
}

/**
 * 一个通过 [64位整型 (Long)][Long] 作为ID值的 [NumericalID] 实现。
 *
 *  @property value 源值。
 */
public class LongID private constructor(public val value: Long) : NumericalID() {
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
            is NumericalID -> compareNumber(this, it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

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
public class ULongID private constructor(@get:JvmName("getValue") public val value: ULong) : NumericalID() {
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
            is NumericalID -> compareNumber(this, it) == 0
            else -> value.toString() == it.toString()
        }
    }

    override fun equalsExact(other: Any?): Boolean = idExactEq(other) {
        value == it.value
    }

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
}


private fun compareNumber(source: NumericalID, target: NumericalID): Int {
    return when (source) {
        is IntID -> when (target) {
            is IntID -> source.value.compareTo(target.value)
            is UIntID -> when {
                source.value < 0 || target.value > NumericalID.INT_MAX_ON_UINT -> -1
                else -> source.value.compareTo(target.value.toInt())
            }

            else -> TODO()
        }

        else -> TODO()
    }
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
