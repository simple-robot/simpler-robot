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
 */

@file:JvmName("Identifies")

package love.forte.simbot

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.utils.RandomIDUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAccumulator
import java.util.concurrent.atomic.LongAdder


/**
 * 唯一标识 [ID].
 *
 * 一个 [ID]，其代表了两种数据：ID的类型，以及ID具体的值。
 *
 * 假若一个 [ID] 中实际存储的值仅有一个，则它的 [toString] 应当就是它的字面值。
 * 通常情况下 [ID] 之间的相等判断即通过字面值进行。
 *
 * [ID] 是 [可排序的][Comparable]。
 *
 *
 * ## 序列化
 *
 * [ID] 应当支持序列化, 且 [ID] 的序列化器应当都是一个 `primitive` 序列化器。
 * 所有的 [ID] 序列化后都不应是结构体, 而是一个原始类型值。
 *
 *
 * 例如：
 * ```kotlin
 *
 * @Serializable
 * data class User(val id: LongID, val name: String)
 *
 * val json = Json.encodeToString(User(213L.ID, "ForteScarlet"))
 * // json: {"id": 213, "name": "ForteScarlet"}
 *
 * ```
 *
 * 鉴于 [ID] 的最终序列化结果为原始类型，并且是非封闭性的，
 * 因此在序列化是时使用 [ID]，需要使用一个具体的最终类型。
 *
 * ```kotlin
 * // 直接使用具体的ID类型，比如LongID
 * @Serializable
 * data class User(val id: LongID)
 *
 * ```
 *
 * 对于一个可序列化类型，作为属性的 [ID] 必须是一个具体的可字面量序列化类型，而不能是一个抽象类型。
 *
 * 如果对于一个ID字段，你希望能够保证它能够完全的正反序列化，并且你只关心它的字面量而不关系其他方面，
 * 那么你可以考虑将此字段的序列化器标记为 [ID.AsCharSequenceIDSerializer].
 *
 * ```kotlin
 * @Serializable
 * data class Example(
 *      @Serializable(ID.AsCharSequenceIDSerializer::class)
 *      val id: ID,
 *      val name: String
 * )
 * ```
 *
 * 此序列化其会永远将ID视为其字面值字符串作为序列化目标。
 *

 *
 * @see CharSequenceID
 * @see NumericalID
 *
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName("ID")
public sealed class ID : Comparable<ID>, Cloneable {
    /**
     * [ID] 的 [toString] 结果必须是当前ID所对应的字面值。
     */
    abstract override fun toString(): String

    /**
     * ID之间应当是可以排序的。
     */
    abstract override fun compareTo(other: ID): Int

    final override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ID) return false
        if (doEquals(other)) return true
        return literal == other.literal
    }

    protected abstract fun doEquals(other: ID): Boolean
    abstract override fun hashCode(): Int

    @Suppress("FunctionName")
    public companion object {
        @Bonus @JvmStatic
        public fun `$`(value: Int): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: Char): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: Long): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: Double): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: Float): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: CharSequence): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: UUID): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: AtomicInteger): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: LongAdder): ID = value.ID
        @Bonus @JvmStatic
        public fun `$`(value: LongAccumulator): ID = value.ID
    }

    /**
     * 将一个 [ID] 视为一个 [CharSequenceID] 进行序列化。
     */
    public object AsCharSequenceIDSerializer : KSerializer<ID> {
        override fun deserialize(decoder: Decoder): ID = decoder.decodeString().ID
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AsCharSequenceId", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ID) {
            encoder.encodeString(value.toString())
        }


    }
}

/**
 * [ID]的字面值。等同于 [ID.toString].
 */
public inline val ID.literal: String get() = toString()

/**
 * 将一个 [Int] 作为 [ID][IntID].
 */
@get:JvmName("ID")
public val Int.ID: IntID
    get() = IntID(this)

/**
 * 将一个 [Char] 作为 [ID][IntID].
 */
@get:JvmName("ID")
public val Char.ID: IntID
    get() = IntID(this)

/**
 * 将一个 [Long] 作为 [ID][LongID].
 */
@get:JvmName("ID")
public val Long.ID: LongID
    get() = LongID(this)

/**
 * 将一个 [Double] 作为 [ID][DoubleID].
 */
@get:JvmName("ID")
public val Double.ID: DoubleID
    get() = DoubleID(this)

/**
 * 将一个 [Float] 作为 [ID][FloatID].
 */
@get:JvmName("ID")
public val Float.ID: FloatID
    get() = FloatID(this)

/**
 * 注意，尽可能避免将 [StringBuilder] 等可变序列作为参数提供, 除非你明确的知道你在做什么。
 * [CharSequenceID] 的 [value][CharSequenceID.value] 目前将会直接使用其引用作为参数。
 */
@get:JvmName("ID")
public val CharSequence.ID: CharSequenceID
    get() = if (isEmpty()) CharSequenceID.EMPTY else CharSequenceID(this)


/**
 * 将一个UUID转化为字符串后作为 [CharSequenceID] 。
 */
public val UUID.ID: CharSequenceID
    get() = toString().ID

/**
 * 取得一个随机ID。
 */
public fun randomID(): ID = RandomIDUtil.randomID().ID


/**
 * 将一个 [AtomicInteger] 当前的**瞬时值**作为ID。
 */
@Suppress("FunctionName")
@get:JvmName("ID")
public val AtomicInteger.ID: IntID
    get() = this.get().ID

/**
 * 将一个 [LongAdder] 当前的**瞬时值**作为ID。
 */
@Suppress("FunctionName")
@get:JvmName("ID")
public val LongAdder.ID: LongID
    get() = this.sum().ID

/**
 * 将一个 [LongAccumulator] 当前的**瞬时值**作为ID。
 */
@Suppress("FunctionName")
@get:JvmName("ID")
public val LongAccumulator.ID: LongID
    get() = this.get().ID


/**
 * 根据当前时间戳作为 [LongID].
 */
public fun currentTimeMillisID(): LongID = System.currentTimeMillis().ID

/**
 * 以一个 [数字][Number] 作为字面值的 [ID].
 *
 * 这个数字可能是 [Int][IntID], [Long][LongID], [Float][FloatID], [Double][DoubleID],
 *
 * 或者一个平台下相关的 [其他 Number][ArbitraryNumericalID] 实现。
 *
 * 示例:
 * ```
 * // Kotlin
 * val id: LongID = 123L.ID
 * ```
 *
 * ```java
 * // Java
 * LongID id = Identifies.ID(123);
 * ```
 *
 * @see asNumber
 *
 * @see IntID
 * @see LongID
 * @see DoubleID
 * @see FloatID
 * @see ArbitraryNumericalID
 * @see BigDecimalID
 * @see BigIntegerID
 *
 * @see Int.ID
 * @see Long.ID
 * @see Double.ID
 * @see Float.ID
 */
@Suppress("MemberVisibilityCanBePrivate", "EqualsOrHashCode")
@SerialName("ID.Number")
@Serializable
public sealed class NumericalID<N : Number> : ID() {

    /**
     * 此数字ID的值。
     */
    public abstract val value: N

    override fun compareTo(other: ID): Int {
        if (other === this) return 0
        if (other is NumericalID<*>) {
            return when (other) {
                is IntID -> toInt().compareTo(other.value)
                is LongID -> toLong().compareTo(other.value)
                is DoubleID -> toDouble().compareTo(other.value)
                is FloatID -> toFloat().compareTo(other.value)
                is BigIntegerID -> when (this) {
                    is BigIntegerID -> this.value.compareTo(other.value)
                    is BigDecimalID -> this.value.compareTo(other.value.toBigDecimal())
                    is IntID -> toInt().compareTo(other.value.toInt())
                    is LongID -> toLong().compareTo(other.value.toLong())
                    is DoubleID -> toDouble().compareTo(other.value.toDouble())
                    is FloatID -> toFloat().compareTo(other.value.toFloat())
                }
                is BigDecimalID -> when (this) {
                    is BigDecimalID -> this.value.compareTo(other.value)
                    is BigIntegerID -> this.value.compareTo(other.value.toBigInteger())
                    is IntID -> toInt().compareTo(other.value.toInt())
                    is LongID -> toLong().compareTo(other.value.toLong())
                    is DoubleID -> toDouble().compareTo(other.value.toDouble())
                    is FloatID -> toFloat().compareTo(other.value.toFloat())
                }
            }
        }
        return toString().compareTo(other.toString())
    }

    //region from kotlin.Number
    /**
     * 将当前数字转为 [Double]. 同 [Number.toDouble].
     * @see Number.toDouble
     */
    public open fun toDouble(): Double = value.toDouble()

    /**
     * 将当前数字转为 [Float]. 同 [Number.toFloat].
     * @see Number.toFloat
     */
    public open fun toFloat(): Float = value.toFloat()

    /**
     * 将当前数字转为 [Long]. 同 [Number.toLong].
     * @see Number.toLong
     */
    public open fun toLong(): Long = value.toLong()

    /**
     * 将当前数字转为 [Int]. 同 [Number.toInt].
     * @see Number.toInt
     */
    public open fun toInt(): Int = value.toInt()

    /**
     * 将当前数字转为 [Char]. 同 [Number.toChar].
     * @see Number.toChar
     */
    public open fun toChar(): Char = value.toChar()

    /**
     * 将当前数字转为 [Short]. 同 [Number.toShort].
     * @see Number.toShort
     */
    public open fun toShort(): Short = value.toShort()

    /**
     * 将当前数字转为 [Byte]. 同 [Number.toByte].
     * @see Number.toByte
     */
    public open fun toByte(): Byte = value.toByte()

    //endregion
    override fun doEquals(other: ID): Boolean {
        if (other is NumericalID<*>) {
            return when (other) {
                is IntID -> toInt() == other.value
                is LongID -> toLong() == other.value
                is DoubleID -> toDouble() == other.value
                is FloatID -> toFloat() == other.value
                is BigIntegerID -> this is BigIntegerID && (value == other.value)
                is BigDecimalID -> this is BigDecimalID && (value == other.value)
            }
        }

        return false
    }

    override fun hashCode(): Int = value.hashCode()
    final override fun toString(): String = value.toString()
}

//region 标准的基础数据ID实现
/** 使用 [Int] 或 [Char] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.Int")
@Serializable(with = IntID.Serializer::class)
public data class IntID(public val number: Int) : NumericalID<Int>() {
    override val value: Int
        get() = number

    public constructor(char: Char) : this(char.code)

    override fun toInt(): Int = number
    override fun toChar(): Char = number.toChar()
    override fun clone(): IntID = copy()

    internal object Serializer : KSerializer<IntID> {
        override fun deserialize(decoder: Decoder): IntID = IntID(decoder.decodeInt())
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntID", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: IntID) {
            encoder.encodeInt(value.number)
        }
    }
}

/** 使用 [Long] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.Long")
@Serializable(with = LongID.Serializer::class)
public data class LongID(public val number: Long) : NumericalID<Long>() {
    override val value: Long
        get() = number

    override fun toLong(): Long = number
    override fun toInt(): Int = number.toInt()
    override fun clone(): LongID = copy()

    internal object Serializer : KSerializer<LongID> {
        override fun deserialize(decoder: Decoder): LongID = LongID(decoder.decodeLong())
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LongID", PrimitiveKind.LONG)
        override fun serialize(encoder: Encoder, value: LongID) {
            encoder.encodeLong(value.number)
        }
    }
}

/** 使用 [Double] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.Double")
@Serializable(with = DoubleID.Serializer::class)
public data class DoubleID(public val number: Double) : NumericalID<Double>() {
    override val value: Double
        get() = number

    override fun toDouble(): Double = number
    override fun clone(): DoubleID = copy()

    internal object Serializer : KSerializer<DoubleID> {
        override fun deserialize(decoder: Decoder): DoubleID = DoubleID(decoder.decodeDouble())
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DoubleID", PrimitiveKind.DOUBLE)
        override fun serialize(encoder: Encoder, value: DoubleID) {
            encoder.encodeDouble(value.number)
        }
    }
}

/** 使用 [Float] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.Float")
@Serializable(with = FloatID.Serializer::class)
public data class FloatID(public val number: Float) : NumericalID<Float>() {
    override val value: Float
        get() = number

    override fun toFloat(): Float = number
    override fun clone(): FloatID = copy()

    internal object Serializer : KSerializer<FloatID> {
        override fun deserialize(decoder: Decoder): FloatID = FloatID(decoder.decodeFloat())
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FloatID", PrimitiveKind.FLOAT)
        override fun serialize(encoder: Encoder, value: FloatID) {
            encoder.encodeFloat(value.number)
        }
    }
}
//endregion


/**
 * 一个任意的 [数字ID][NumericalID] 实例, 由平台进行实现。
 * 作为一个任意的 [数字][Number] ID，实现的内部字面量需要是不可变的。
 *
 * @see BigDecimalID
 * @see BigIntegerID
 *
 */
@Suppress("CanBeParameter")
@SerialName("ID.ArbitraryNumerical")
@Serializable
public sealed class ArbitraryNumericalID<N : Number> : NumericalID<N>()


/**
 * 由 [BigDecimal] 作为字面量值的 [NumericalID] 实现。
 *
 * @see BigDecimal.ID
 */
@SerialName("ID.BigDecimal")
@Serializable(with = BigDecimalID.Serializer::class)
public class BigDecimalID(override val value: BigDecimal) : ArbitraryNumericalID<BigDecimal>() {
    override fun clone(): BigDecimalID = BigDecimalID(value)

    internal object Serializer : KSerializer<BigDecimalID> {
        override fun deserialize(decoder: Decoder): BigDecimalID = BigDecimalID(BigDecimal(decoder.decodeString()))
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimalID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BigDecimalID) {
            encoder.encodeString(value.value.toString())
        }
    }


    /**
     * 转为 [BigIntegerID].
     *
     * @throws ArithmeticException 如果[value]有一个非零小数部分。
     *
     * @see BigDecimal.toBigIntegerExact
     */
    @JvmOverloads
    public fun toBigIntegerID(exact: Boolean = false): BigIntegerID = if (exact) value.toBigIntegerExact().ID
    else value.toBigInteger().ID


    public companion object {
        @JvmStatic
        public val ZERO: BigDecimalID = BigDecimalID(BigDecimal.ZERO)

        @JvmStatic
        public val ONE: BigDecimalID = BigDecimalID(BigDecimal.ONE)

        @JvmStatic
        public val TEN: BigDecimalID = BigDecimalID(BigDecimal.TEN)
    }
}


/**
 * 由 [BigInteger] 作为字面量值的 [NumericalID] 实现。
 *
 * @see BigInteger.ID
 */
@SerialName("ID.BigInteger")
@Serializable(with = BigIntegerID.Serializer::class)
public class BigIntegerID(override val value: BigInteger) : ArbitraryNumericalID<BigInteger>() {
    override fun clone(): BigIntegerID = BigIntegerID(value)

    internal object Serializer : KSerializer<BigIntegerID> {
        override fun deserialize(decoder: Decoder): BigIntegerID = BigIntegerID(BigInteger(decoder.decodeString()))
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigIntegerID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BigIntegerID) {
            encoder.encodeString(value.value.toString())
        }
    }

    /**
     * 转为 [BigDecimalID].
     */
    public fun toBigDecimalID(): BigDecimalID = value.toBigDecimal().ID

    /**
     * 转为 [BigDecimalID].
     */
    @JvmOverloads
    public fun toBigDecimalID(scale: Int, mathContext: MathContext = MathContext.UNLIMITED): BigDecimalID =
        value.toBigDecimal(scale, mathContext).ID


    public companion object {
        @JvmStatic
        public val ZERO: BigIntegerID = BigIntegerID(BigInteger.ZERO)

        @JvmStatic
        public val ONE: BigIntegerID = BigIntegerID(BigInteger.ONE)

        @JvmStatic
        public val TEN: BigIntegerID = BigIntegerID(BigInteger.TEN)
    }
}

/**
 * 得到一个字面值为 [BigDecimal] 的 [NumericalID].
 */
@Suppress("FunctionName")
@get:JvmName("ID")
public val BigDecimal.ID: BigDecimalID
    get() = when (this) {
        BigDecimal.ZERO -> BigDecimalID.ZERO
        BigDecimal.ONE -> BigDecimalID.ONE
        BigDecimal.TEN -> BigDecimalID.TEN
        else -> BigDecimalID(this)
    }

/**
 * 得到一个字面值为 [BigInteger] 的 [NumericalID].
 */
@Suppress("FunctionName")
@get:JvmName("ID")
public val BigInteger.ID: BigIntegerID
    get() = when (this) {
        BigInteger.ZERO -> BigIntegerID.ZERO
        BigInteger.ONE -> BigIntegerID.ONE
        BigInteger.TEN -> BigIntegerID.TEN
        else -> BigIntegerID(this)
    }

@Suppress("FunctionName")
@get:JvmName("ID")
public val AtomicLong.ID: LongID
    get() = this.toLong().ID


/**
 * 将一个 [NumericalID] 作为 [Number] 使用.
 */
public fun NumericalID<*>.asNumber(): Number = NumericalIdNumber(this)

private class NumericalIdNumber(private val id: NumericalID<*>) : Number() {
    override fun toByte(): Byte = id.toByte()
    override fun toShort(): Short = id.toShort()
    override fun toInt(): Int = id.toInt()
    override fun toLong(): Long = id.toLong()
    override fun toChar(): Char = id.toChar()
    override fun toDouble(): Double = id.toDouble()
    override fun toFloat(): Float = id.toFloat()
}


/**
 * 以 [字符序列][CharSequence] 作为字面值的 [ID].
 *
 * ```kotlin
 * // Kotlin
 * val id = "forte".ID
 * ```
 *
 * ```java
 * // Java
 * StringID id = Identifies.ID("forte");
 * ```
 *
 * ### 可变字符序列
 * 注意，尽可能避免将 [StringBuilder] 等可变序列作为参数提供, 除非你明确的知道你在做什么。
 * [CharSequenceID] 的 [value][CharSequenceID.value] 目前将会直接使用其引用而非瞬时值作为属性。
 *
 * ### 转化
 * 所有的ID都拥有转化为字符序列ID的能力。
 *
 * ### 构建
 * 获取 [CharSequenceID]:
 * ```kotlin
 * val id: CharSequenceID = "Hello".ID
 * ```
 *
 * 转化 [CharSequenceID]
 * ```kotlin
 * val otherId: IntID = 123.ID
 * val newId: CharSequenceID = otherId.toCharSequenceID()
 * ```
 *
 * @see CharSequence.ID
 * @see ID.toCharSequenceID
 * @see ID.AsCharSequenceIDSerializer
 * @property value 用于代表当前ID值的字符序列。
 */
@SerialName("ID.CharSequence")
@Serializable(with = CharSequenceID.Serializer::class)
public data class CharSequenceID(val value: CharSequence) : ID() {
    /**
     * 直接通过 [ID.equals] 的最后逻辑进行toString判断。
     */
    override fun doEquals(other: ID): Boolean = false
    override fun toString(): String = value.toString()
    override fun compareTo(other: ID): Int = if (other === this) 0 else toString().compareTo(other.toString())
    override fun clone(): CharSequenceID = copy()

    /**
     * 当前字符序列长度。
     */
    public val length: Int get() = value.length

    /**
     * [CharSequenceID] 的字面值序列化器。
     */
    public object Serializer : KSerializer<CharSequenceID> {
        override fun deserialize(decoder: Decoder): CharSequenceID = CharSequenceID(decoder.decodeString())
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CharSequenceID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: CharSequenceID) {
            encoder.encodeString(value.toString())
        }
    }

    public companion object {
        @JvmStatic
        public val EMPTY: CharSequenceID = CharSequenceID("")
    }
}

/**
 * 绝大多数情况下，你都会将 [CharSequenceID] 当成 `StringID` 来使用，不是么？
 */
public typealias StringID = CharSequenceID

/**
 * 所有的ID都拥有转化为字符序列ID的能力。
 */
public fun ID.toCharSequenceID(): CharSequenceID = if (this is CharSequenceID) this else toString().ID


/**
 * 尝试将当前ID转为一个 [NumericalID].
 *
 * @throws IDException 无法进行转化时。
 */
public fun ID.tryToNumericalID(): NumericalID<*> = if (this is NumericalID<*>) this
else try {
    BigDecimalID(BigDecimal(toString()))
} catch (nfe: NumberFormatException) {
    throw IDException("Unable to convert id [$this] to LongID", nfe)
}


/**
 * 尝试将当前ID转为一个 [LongID].
 *
 * 最终会尝试通过 [BigDecimal] 进行转化。
 *
 * @throws IDException 无法进行转化时。
 */
public fun ID.tryToLongID(): LongID = when (this) {
    is LongID -> this
    is NumericalID<*> -> this.toLong().ID
    else -> try {
        BigDecimal(toString()).toLong().ID
    } catch (nfe: NumberFormatException) {
        throw IDException("Unable to convert id [$this] to LongID", nfe)
    }
}


/**
 * 与 [ID] 相关的异常。
 */
public open class IDException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

