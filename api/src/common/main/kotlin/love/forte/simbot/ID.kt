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
@file:JvmMultifileClass
package love.forte.simbot

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@Serializable
public data class User(val id: LongID, val name: String)

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
 * ```
 * // 直接使用具体的ID类型，比如LongID
 * @Serializable
 * data class User(val id: LongID)
 *
 * // 一个具体的类型，不要使用模糊化的ID
 *
 * ```
 *
 *
 *
 *
 * [ID] 是[可排序的][Comparable]。
 *
 * @see CharSequenceID
 * @see NumericalID
 * @see ComplexID 其他自定义ID
 * @see DelegateID 委托ID
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


    @Suppress("FunctionName")
    public companion object {
        // @JvmStatic
        // @JsName("byNumber")
        // public fun <N: Number> by(n: N): NumericalID<N> = n.ID()
        // @JvmStatic
        // @JsName("byInt")
        // public fun by(n: Int): IntID = n.ID
        // @JvmStatic
        // @JsName("byChar")
        // public fun by(n: Char): IntID = n.ID
        // @JvmStatic
        // @JsName("byLong")
        // public fun by(n: Long): LongID = n.ID
        // @JvmStatic
        // @JsName("byDouble")
        // public fun by(n: Double): DoubleID = n.ID
        // @JvmStatic
        // @JsName("byFloat")
        // public fun by(n: Float): FloatID = n.ID
        //
        // @JvmStatic
        // @JsName("byString")
        // public fun by(s: String): CharSequenceID = s.ID
    }
}

@get:JvmName("ID")
public val @ParameterName("value") Int.ID: IntID get() = IntID(this)
@get:JvmName("ID")
public val @ParameterName("value") Char.ID: IntID get() = IntID(this)
@get:JvmName("ID")
public val @ParameterName("value") Long.ID: LongID get() = LongID(this)
@get:JvmName("ID")
public val @ParameterName("value") Double.ID: DoubleID get() = DoubleID(this)
@get:JvmName("ID")
public val @ParameterName("value") Float.ID: FloatID get() = FloatID(this)
@get:JvmName("ID")
public val @ParameterName("value") CharSequence.ID: CharSequenceID get() = CharSequenceID(this)


/**
 * 由平台实现的, 通过一个 [数字][Number] 实例而得到的 [数字ID][NumericalID]。
 *
 * 在 `JVM` 平台下，支持 BigDecimal 等常见 [Number] 实现，
 * 在 `JS` 平台下无额外实现，将会直接抛出 [NoSuchIDTypeException].
 *
 * @throws NoSuchIDTypeException 如果所使用的 [Number] 不被支持。
 */
@Suppress("FunctionName")
public expect fun <N : Number> N.ID(): ArbitraryNumericalID<N>

/**
 * 以一个 [数字][Number] 作为字面值的 [ID].
 *
 * 这个数字可能是 [Int][IntID], [Long][LongID], [Float][FloatID], [Double][DoubleID],
 *
 * 或者一个平台下相关的 [其他 Number][ArbitraryNumericalID] 实现。
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
 * @see Int.ID
 * @see Long.ID
 * @see Double.ID
 * @see Float.ID
 * @see Number.ID
 */
@Suppress("MemberVisibilityCanBePrivate")
@SerialName("ID.N")
@Serializable
public sealed class NumericalID<N : Number> : ID() {
    public abstract val value: N

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
public data class IntID(public val number: Int) : NumericalID<Int>() {
    override val value: Int
        get() = number

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
public data class LongID(public val number: Long) : NumericalID<Long>() {
    override val value: Long
        get() = number

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
public data class DoubleID(public val number: Double) : NumericalID<Double>() {
    override val value: Double
        get() = number

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
public data class FloatID(public val number: Float) : NumericalID<Float>() {
    override val value: Float
        get() = number

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



/**
 * 一个任意的 [数字ID][NumericalID] 实例, 由平台进行实现。
 * 作为一个任意的 [数字][Number] ID，实现的内部字面量需要是不可变的。
 *
 * 在 `JVM` 平台下，支持 BigDecimal 等常见 [Number] 实现。
 *
 * @see Number.ID
 *
 */
@kotlin.Suppress("CanBeParameter")
@SerialName("ID.N.A")
@Serializable
public abstract class ArbitraryNumericalID<N : Number> internal constructor(): NumericalID<N>()


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
 * @see String.ID
 */
@SerialName("ID.CS")
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
@SerialName("ID.CX")
@Serializable
public abstract class ComplexID : ID()


/**
 * 一个内部委托的ID实现。
 * 在某些情况下，也许对于一个 [ID] 你可能所需要的最终表现与目前已存的其他类型的 [ID] 一致，
 * 但是出于对某些原因的考虑（例如需要额外的计算、远程网络调用或者懒加载），
 * 你需要通过一些方法的实现来计算这个 [ID] 而无法在初始化的时候得到此 [ID] 实例，
 * 那么你则可以通过实现 [DelegateID] 来在委托一个目标的 [ID][T], 并在适当的时候对其进行计算。
 *
 * 需要注意，如果你实现了此ID，那么你需要保证最终得到的真实ID的实例是始终唯一的。
 *
 * [DelegateID] 中的 [hashCode] [equals] [compareTo] [toString] 都会直接使用委托对象实现。
 *
 */
@SerialName("ID.D")
public abstract class DelegateID<T : ID> : ID() {
    public abstract val delegate: T
    final override fun toString(): String = delegate.toString()
    final override fun hashCode(): Int = delegate.hashCode()
    final override fun equals(other: Any?): Boolean = delegate == other
    final override fun compareTo(other: ID): Int = delegate.compareTo(other)
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

/**
 * ID类型不存在异常。
 */
public open class NoSuchIDTypeException : IDException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}