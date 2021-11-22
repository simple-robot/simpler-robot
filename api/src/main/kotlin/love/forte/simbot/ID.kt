/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
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
import java.math.BigDecimal
import java.math.BigInteger
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
 * [ID] 应当支持序列化, 且 [ID] 的序列化器应当都是一个 `primitive` 序列化器。
 * 所有的 [ID] 序列化后都应是结构体, 而应该是一个原始类型值。
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
 *
 * [ID] 是[可排序的][Comparable]。
 *
 * @see CharSequenceID
 * @see NumericalID
 * @see ComplexID 其他自定义ID
 *
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class ID : Comparable<ID> {
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

    public companion object {}
}

@get:JvmName("ID")
public val Int.ID: IntID
    get() = IntID(this)

@get:JvmName("ID")
public val Char.ID: IntID
    get() = IntID(this)

@get:JvmName("ID")
public val Long.ID: LongID
    get() = LongID(this)

@get:JvmName("ID")
public val Double.ID: DoubleID
    get() = DoubleID(this)

@get:JvmName("ID")
public val Float.ID: FloatID
    get() = FloatID(this)

/**
 * 注意，尽可能避免将 [StringBuilder] 等可变序列作为参数提供, 除非你明确的知道你在做什么。
 * [CharSequenceID] 的 [value][CharSequenceID.value] 目前将会直接使用其引用作为参数。
 */
@get:JvmName("ID")
public val CharSequence.ID: CharSequenceID
    get() = CharSequenceID(this)


@Suppress("FunctionName")
public fun AtomicInteger.ID(): IntID = this.get().ID

@Suppress("FunctionName")
public fun LongAdder.ID(): LongID = this.sum().ID

@Suppress("FunctionName")
public fun LongAccumulator.ID(): LongID = this.get().ID


/**
 * 根据当前时间作为 [LongID].
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
@kotlin.Suppress("CanBeParameter")
@SerialName("ID.N.A")
@Serializable
public sealed class ArbitraryNumericalID<N : Number> private constructor() : NumericalID<N>()




/**
 * 由 [BigDecimal] 作为字面量值的 [NumericalID] 实现。
 */
@SerialName("ID.N.A.BD")
@Serializable(with = BigDecimalID.BigDecimalIDSerializer::class)
public class BigDecimalID(override val value: BigDecimal) : ArbitraryNumericalID<BigDecimal>() {
    internal object BigDecimalIDSerializer : KSerializer<BigDecimalID> {
        override fun deserialize(decoder: Decoder): BigDecimalID = BigDecimalID(BigDecimal(decoder.decodeString()))
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimalID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BigDecimalID) {
            encoder.encodeString(value.value.toString())
        }
    }
}


/**
 * 由 [BigInteger] 作为字面量值的 [NumericalID] 实现。
 */
@SerialName("ID.N.A.BI")
@Serializable(with = BigIntegerID.BigIntegerIDSerializer::class)
public class BigIntegerID(override val value: BigInteger) : ArbitraryNumericalID<BigInteger>() {
    internal object BigIntegerIDSerializer : KSerializer<BigIntegerID> {
        override fun deserialize(decoder: Decoder): BigIntegerID = BigIntegerID(BigInteger(decoder.decodeString()))
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigIntegerID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BigIntegerID) {
            encoder.encodeString(value.value.toString())
        }
    }
}

/**
 * 得到一个字面值为 [BigDecimal] 的 [NumericalID].
 */
@Suppress("FunctionName")
public fun BigDecimal.ID(): BigDecimalID = BigDecimalID(this)

/**
 * 得到一个字面值为 [BigInteger] 的 [NumericalID].
 */
@Suppress("FunctionName")
public fun BigInteger.ID(): BigIntegerID = BigIntegerID(this)

@Suppress("FunctionName")
public fun AtomicLong.ID(): LongID = this.toLong().ID




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
 * 序列化的时候，如果需要将 [CharSequenceID] 字段作为字符串字面量序列化，可以使用 [CharSequenceID.CharSequenceIDSerializer].
 *
 * 注意，尽可能避免将 [StringBuilder] 等可变序列作为参数提供, 除非你明确的知道你在做什么。
 * [CharSequenceID] 的 [value][CharSequenceID.value] 目前将会直接使用其引用作为参数。
 *
 * 所有的ID都拥有转化为字符序列ID的能力。
 *
 *
 * @sample CharSequence.ID
 * @sample ID.toCharSequenceID
 */
@SerialName("ID.CS")
@Serializable(with = CharSequenceID.CharSequenceIDSerializer::class)
public data class CharSequenceID internal constructor(val value: CharSequence) : ID() {
    /**
     * 直接通过 [ID.equals] 的最后逻辑进行toString判断。
     */
    override fun doEquals(other: ID): Boolean = false


    override fun toString(): String = value.toString()
    override fun compareTo(other: ID): Int = if (other === this) 0 else toString().compareTo(other.toString())

    /**
     * [CharSequenceID] 的字面值序列化器。
     */
    public object CharSequenceIDSerializer : KSerializer<CharSequenceID> {
        override fun deserialize(decoder: Decoder): CharSequenceID = CharSequenceID(decoder.decodeString())
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CharSequenceID", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: CharSequenceID) {
            encoder.encodeString(value.toString())
        }
    }
}

/**
 * 所有的ID都拥有转化为字符序列ID的能力。
 */
public fun ID.toCharSequenceID(): CharSequenceID = if (this is CharSequenceID) this else CharSequenceID(this.toString())


/**
 * 如果是一个复杂ID, 即无法通过 [NumericalID] 或 [CharSequenceID] 进行表示的，
 * 则实现此抽象类。
 *
 * 需要保证实现类能够完全的序列化。
 */
@SerialName("ID.CX")
@Serializable
public abstract class ComplexID : ID()

//
// /**
//  * 一个内部委托的ID实现。
//  * 在某些情况下，也许对于一个 [ID] 你可能所需要的最终表现与目前已存的其他类型的 [ID] 一致，
//  * 但是出于对某些原因的考虑（例如需要额外的计算、远程网络调用或者懒加载），
//  * 你需要通过一些方法的实现来计算这个 [ID] 而无法在初始化的时候得到此 [ID] 实例，
//  * 那么你则可以通过实现 [DelegateID] 来在委托一个目标的 [ID][T], 并在适当的时候对其进行计算。
//  *
//  * 需要注意，如果你实现了此ID，那么你需要保证最终得到的真实ID的实例是始终唯一的。
//  *
//  * 并且你需要好好考虑考虑怎么实现序列化
//  *
//  * [DelegateID] 中的 [hashCode] [equals] [compareTo] [toString] 都会直接使用委托对象实现。
//  *
//  */
// @SerialName("ID.D")
// @Serializable
// public abstract class DelegateID<T : ID> : ID() {
//     public abstract val delegate: T
//     final override fun toString(): String = delegate.toString()
//     final override fun hashCode(): Int = delegate.hashCode()
//     final override fun equals(other: Any?): Boolean = delegate == other
//     final override fun compareTo(other: ID): Int = delegate.compareTo(other)
// }




/**
 * 与 [ID] 相关的异常。
 */
public open class IDException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * ID类型不存在异常。
 */
public open class NoSuchIDTypeException : IDException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

