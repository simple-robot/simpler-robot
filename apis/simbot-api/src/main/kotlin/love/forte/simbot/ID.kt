/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
import love.forte.simbot.utils.UUIDUtil
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
 * ID一般存在与各个元数据中，作为元数据的唯一标识。
 *
 * 一个 [ID]，其代表了两种数据：ID的类型，以及ID具体的值。
 * [ID] 的类型即为其自身，不同类型的ID无论如何也不应相同。而 [ID] 中具体的值千变万化，且不一定仅存一个，由实现者自行决定。
 *
 * 假若一个 [ID] 中实际存储的值仅有一个，则它的 [toString] 应当就是它的字面值。
 *
 * [ID] 是[可排序的][Comparable]。
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
 * 因此在使用 [ID] 的时候，必须实现 [序列化][Serializable], 且使用一个具体的最终类型。
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
 *  class {
 *  // ....
 *  @Serializable(ID.AsCharSequenceIDSerializer::class)
 *  val id: ID,
 *  // ...
 * }
 * ```
 *
 * 此序列化其会永远将ID视为其字面值字符串作为序列化目标。
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
 * @see CharSequenceID
 * @see NumericalID
 *
 *
 * @author ForteScarlet
 */
@Serializable
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
        if (other !is ID) return false
        if (doEquals(other)) return true
        return toString() == other.toString()
    }

    protected open fun doEquals(other: ID): Boolean = false
    abstract override fun hashCode(): Int

    public companion object;

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
public fun randomID(): ID = UUIDUtil.randomUUID().ID


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
 * e.g.:
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
@SerialName("ID.N")
@Serializable
public sealed class NumericalID<N : Number> : ID() {
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
    public open fun toDouble(): Double = value.toDouble()
    public open fun toFloat(): Float = value.toFloat()
    public open fun toLong(): Long = value.toLong()
    public open fun toInt(): Int = value.toInt()
    public open fun toChar(): Char = value.toChar()
    public open fun toShort(): Short = value.toShort()
    public open fun toByte(): Byte = value.toByte()

    //endregion
    override fun doEquals(other: ID): Boolean {
        if (other is NumericalID<*>) {
            return when (other) {
                is IntID -> toInt() == other.value
                is LongID -> toLong() == other.value
                is DoubleID -> toDouble() == other.value
                is FloatID -> toFloat() == other.value
                is BigIntegerID -> if (this is BigIntegerID) this == other else toString() == other.toString()
                is BigDecimalID -> if (this is BigDecimalID) this == other else toString() == other.toString()
            }
        }

        return false
    }

    override fun hashCode(): Int = value.hashCode()
    final override fun toString(): String = value.toString()
}

//region 标准的基础数据ID实现
/** 使用 [Int] 或 [Char] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.N.I")
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
@SerialName("ID.N.L")
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
@SerialName("ID.N.D")
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
@SerialName("ID.N.F")
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
@SerialName("ID.N.A")
@Serializable
public sealed class ArbitraryNumericalID<N : Number> private constructor() : NumericalID<N>()


/**
 * 由 [BigDecimal] 作为字面量值的 [NumericalID] 实现。
 *
 * @see BigDecimal.ID
 */
@SerialName("ID.N.A.BD")
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
    public fun toBigIntegerID(exact: Boolean = false): BigIntegerID =
        if (exact) value.toBigIntegerExact().ID
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
@SerialName("ID.N.A.BI")
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
 * 以 [字符][CharSequence] 作为字面值的 [ID].
 *
 * ```kt
 * // Kotlin
 * val id = "ID".ID
 * ```
 *
 * ```java
 * // Java
 * StringID id = ID.by("ID");
 * ```
 *
 * 序列化的时候，如果需要将 [CharSequenceID] 字段作为字符串字面量序列化，可以使用 [CharSequenceID.Serializer].
 *
 * 注意，尽可能避免将 [StringBuilder] 等可变序列作为参数提供, 除非你明确的知道你在做什么。
 * [CharSequenceID] 的 [value][CharSequenceID.value] 目前将会直接使用其引用作为参数。
 *
 * 所有的ID都拥有转化为字符序列ID的能力。
 *
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
 */
@SerialName("ID.CS")
@Serializable(with = CharSequenceID.Serializer::class)
public data class CharSequenceID(val value: CharSequence) : ID() {
    /**
     * 直接通过 [ID.equals] 的最后逻辑进行toString判断。
     */
    override fun doEquals(other: ID): Boolean = false
    override fun toString(): String = value.toString()
    override fun compareTo(other: ID): Int = if (other === this) 0 else toString().compareTo(other.toString())
    override fun clone(): CharSequenceID = copy()

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
 * 所有的ID都拥有转化为字符序列ID的能力。
 */
public fun ID.toCharSequenceID(): CharSequenceID = if (this is CharSequenceID) this else toString().ID


/**
 * 尝试将当前ID转为一个 [NumericalID].
 *
 * @throws IDException 无法进行转化时。
 */
public fun ID.tryToNumericalID(): NumericalID<*> =
    if (this is NumericalID<*>) this
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
public fun ID.tryToLongID(): LongID =
    when (this) {
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

