@file:JvmName("Identifies")

package love.forte.simbot

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.NumericalID.*
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * 唯一标识 [ID].
 * ID一般存在与各个元数据中，作为元数据的唯一标识。
 *
 * 一个 [ID]，其代表了两种数据：ID的类型，以及ID具体的值。
 * [ID] 的类型即为其自身，不同类型的ID无论如何也不应相同。而 [ID] 中具体的值千变万化，且不一定仅存一个，由实现者自行决定。
 *
 * 假若一个 [ID] 中实际存储的值仅有一个，则它的 [toString] 应当就是它的字面值。
 *
 * [ID] 应当支持序列化, 在使用 [ID] 的时候，你应该主动
 *
 * [ID] 是可以进行排序的。
 *
 * @see CharSequenceID
 * @see NumericalID
 * @see ComplexID
 *
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class ID : Comparable<ID> {
    /**
     * 必须实现 [toString].
     */
    abstract override fun toString(): String

    /**
     * ID之间应当是可以排序的。
     */
    abstract override fun compareTo(other: ID): Int


    @Suppress("FunctionName")
    public companion object {
        @JvmStatic
        @JsName("byNumber")
        public fun by(n: Number): NumericalID<*> = TODO() // NumericalID(n)


        @JvmStatic
        @JsName("byString")
        public fun by(s: String): CharSequenceID = TODO() // s.ID
    }
}

public val Int.ID: IntID get() = IntID(this)
public val Char.ID: IntID get() = IntID(this)
public val Long.ID: LongID get() = LongID(this)
public val Double.ID: DoubleID get() = DoubleID(this)
public val Float.ID: FloatID get() = FloatID(this)


// public expect fun <N: Number> N.ID(): NumericalID<N>

// public val Number.ID: NumberID get() = NumberID(this.toLong())
// public val String.ID: StringID get() = StringID(this)


/**
 * 以一个 [数字][Number] 作为字面值的 [ID].
 *
 * 这个数字可能是 [Int], [Long], 又或许是
 *
 * ```kt
 * // Kotlin
 * val id = 123.ID
 * ```
 *
 * ```java
 * // Java
 * LongID id = ID.by(123);
 * ```
 *
 * @see asNumber
 *
 * @see IntID
 * @see LongID
 * @see DoubleID
 * @see FloatID
 * @see ArbitraryNumericalID
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@SerialName("ID.N")
@Serializable
public sealed class NumericalID<N : Number>(public val value: N) : ID() {
    override fun compareTo(other: ID): Int {
        if (other === this) return 0
        if (other is NumericalID<*>) {
            return toLong().compareTo(other.toLong())
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

    final override fun toString(): String = value.toString()
}

//region 标准的基础数据ID实现
/** 使用 [Int] 或 [Char] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.N.I")
@Serializable(with = IntID.Serializer::class)
public data class IntID(public val number: Int) : NumericalID<Int>(number) {
    public constructor(char: Char) : this(char.code)

    override fun toInt(): Int = number
    override fun toChar(): Char = number.toChar()

    internal object Serializer : KSerializer<IntID> {
        override fun deserialize(decoder: Decoder): IntID = IntID(decoder.decodeInt())
        override val descriptor: SerialDescriptor = Int.serializer().descriptor
        override fun serialize(encoder: Encoder, value: IntID) {
            encoder.encodeInt(value.number)
        }
    }
}

/** 使用 [Long] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.N.L")
@Serializable(with = LongID.Serializer::class)
public data class LongID(public val number: Long) : NumericalID<Long>(number) {
    override fun toLong(): Long = number
    override fun toInt(): Int = number.toInt()

    internal object Serializer : KSerializer<LongID> {
        override fun deserialize(decoder: Decoder): LongID = LongID(decoder.decodeLong())
        override val descriptor: SerialDescriptor = Long.serializer().descriptor
        override fun serialize(encoder: Encoder, value: LongID) {
            encoder.encodeLong(value.number)
        }
    }
}

/** 使用 [Double] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.N.D")
@Serializable(with = DoubleID.Serializer::class)
public data class DoubleID(public val number: Double) : NumericalID<Double>(number) {
    override fun toDouble(): Double = number

    internal object Serializer : KSerializer<DoubleID> {
        override fun deserialize(decoder: Decoder): DoubleID = DoubleID(decoder.decodeDouble())
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ID.NUMBER.DOUBLE", kotlinx.serialization.descriptors.PrimitiveKind.DOUBLE)

        override fun serialize(encoder: Encoder, value: DoubleID) {
            encoder.encodeDouble(value.number)
        }
    }
}

/** 使用 [Float] 字面值的 [NumericalID] 实现。 */
@SerialName("ID.N.F")
@Serializable(with = FloatID.Serializer::class)
public data class FloatID(public val number: Float) : NumericalID<Float>(number) {
    override fun toFloat(): Float = number

    internal object Serializer : KSerializer<FloatID> {
        override fun deserialize(decoder: Decoder): FloatID = FloatID(decoder.decodeFloat())
        override val descriptor: SerialDescriptor = Float.serializer().descriptor
        override fun serialize(encoder: Encoder, value: FloatID) {
            encoder.encodeFloat(value.number)
        }
    }
}
//endregion


// @SerialName("ID.N.A")
// @Serializable
// internal expect sealed class ArbitraryNumericalID<N : Number> : NumericalID<N>

// The feature "multi platform projects" is experimental and should be enabled explicitly

/**
 * [NumericalID] as [Number].
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


//
// /**
//  * [NumericalID] 的字面值序列化器。
//  */
// public class NumberIDSerializer : KSerializer<NumericalID> {
//     override fun deserialize(decoder: Decoder): NumericalID = NumericalID(decoder.decodeLong())
//     override val descriptor: SerialDescriptor =
//         PrimitiveSerialDescriptor("simbot.LongIDAsLong", PrimitiveKind.LONG)
//
//     override fun serialize(encoder: Encoder, value: NumericalID) {
//
//         // encoder.encodeLong(value)
//     }
// }

/**
 * 以 [String] 作为字面值的 [ID].
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
 * 序列化的时候，如果需要将 [CharSequenceID] 字段作为字符串字面量序列化，可以使用 [CharSequenceID.PrimitiveSerialSerializer].
 *
 * @see ID.by
 * @see String.ID
 */
@SerialName("ID.C")
@Serializable
public data class CharSequenceID internal constructor(val value: CharSequence) : ID() {
    override fun toString(): String = value.toString()
    override fun compareTo(other: ID): Int = if (other === this) 0 else toString().compareTo(other.toString())

    public companion object {
        public fun primitiveSerialSerializer(): KSerializer<CharSequenceID> = PrimitiveSerialSerializer
    }

    /**
     * [CharSequenceID] 的字面值序列化器。
     */
    public object PrimitiveSerialSerializer : KSerializer<CharSequenceID> {
        override fun deserialize(decoder: Decoder): CharSequenceID = CharSequenceID(decoder.decodeString())
        override val descriptor: SerialDescriptor = String.serializer().descriptor
        override fun serialize(encoder: Encoder, value: CharSequenceID) {
            encoder.encodeString(value.toString())
        }
    }
}


/**
 * 如果是一个复杂ID, 即无法通过 [NumericalID] 或 [CharSequenceID] 进行表示的，
 * 则实现此抽象类。
 */
@Serializable
public abstract class ComplexID : ID()

