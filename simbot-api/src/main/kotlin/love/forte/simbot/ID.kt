/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("Identifies")

package love.forte.simbot

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.delegate.*
import love.forte.simbot.utils.RandomIDUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAccumulator
import java.util.concurrent.atomic.LongAdder
import kotlin.random.Random
import kotlin.random.asKotlinRandom

/**
 * 标记一个可能没有实际用途、令人感到迷惑的ID类型或相关方法/属性。
 *
 * 这些类型/方法/属性未来可能会调整、过时或被删除，且应当尽力避免使用它们。
 *
 * 比如 `FloatID`，没有人会使用浮点数作为ID的。
 *
 */
@RequiresOptIn(
    "An ID type that may have no practical use may be removed in the future",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class ConfusedIDType


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
 * ## 简单包装器
 *
 * [ID] 是一种简单包装器类型，因此建议那些对外公开的 [ID] 属性使用 **即用即造** 的形式，即不会急切的初始化属性，
 * 而是每次获取时临时创建。
 *
 * 例如：
 *
 * ```kotlin
 * val idContainer = ...
 *
 * // 下面会产生3个ID对象
 * useID(idContainer.id)
 * useID(idContainer.id)
 * useID(idContainer.id)
 * ```
 *
 * 也因此，大多数通过属性获取 [ID] 的情况下建议通过局部变量保存以复用。
 *
 * ```kotlin
 * val idContainer = ...
 * val id = idContainer.id
 *
 * // 重复利用
 * useID(id)
 * useID(id)
 * useID(id)
 * ```
 *
 * ## 序列化
 *
 * [ID] 支持序列化, 且 [ID] 的序列化器应当都是一个 `primitive` 序列化器。
 * [ID] 序列化后都不应是结构体, 而是一个值类型。
 *
 * 例如：
 * ```kotlin
 * @Serializable
 * data class User(val id: LongID, val name: String)
 *
 * val json = Json.encodeToString(User(213L.ID, "ForteScarlet"))
 * // json: {"id": 213, "name": "ForteScarlet"}
 * ```
 *
 * 而由于 [ID] 的最终序列化结果为值类型，因此在序列化是时使用 [ID]，需要使用一个具体的最终类型。
 *
 * ```kotlin
 * // 直接使用具体的ID类型，比如LongID
 * @Serializable
 * data class User(val id: LongID)
 * ```
 *
 * 在某个可序列化类型中，对于一个ID字段，如果你希望能够保证它能够完全的正反序列化，并且你只关心它的字面量而不关系其他方面，
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
 * 此序列化其会永远将ID视为字符串作为序列化目标。
 *
 * ## 构造
 *
 * [ID] 提供了名为 `ID` 的后缀扩展属性来将某个值构造为 [ID] 类型，
 * 其中你需要关系的是几个常见的ID类型，即字符串类型和各整型类型的ID。
 *
 * ```kotlin
 * 123.ID    // IntID
 * 123L.ID   // LongID
 * "str".ID  // CharSequenceID
 * 123u.ID   // UInt.ID
 * val ul: ULong = 123u
 * ul.ID     // ULongID
 * ```
 *
 * Java使用者可以通过 `Identifies.ID(...)` 来进行与上述类似的操作。
 *
 * ```java
 * IntID          intId    = Identifies.ID(123);
 * LongID         longId   = Identifies.ID(123L);
 * CharSequenceID strId    = Identifies.ID("str");
 * ```
 *
 *
 * 更多参考:
 *
 * - [Int.ID]
 * - [Long.ID]
 * - [CharSequence.ID]
 *
 * ## 随机ID
 *
 * 如果你需要获取一个随机的ID，可以考虑使用 [randomID]. [randomID] 内使用 [RandomIDUtil.randomID]
 * 生成一个 _类UUID风格_ 的随机字符串并包装为 [ID] 并返回.
 *
 * 不过需要注意，这个随机ID返回的内容并非标准的HEX字符串或UUID字符串。
 *
 * ## 具体类型
 *
 * [ID] 是一个抽象类型，有关其他具体实现类型参阅它们各自的说明。
 *
 *
 * ## 属性委托
 *
 * 面向公开属性的场景 [ID] 提供了一些更简便的属性委托API来简化代码。
 *
 * 有关它们的说明参考
 * - [IntIDDelegate.getValue]
 * - [UIntIDDelegate.getValue]
 * - [LongIDDelegate.getValue]
 * - [ULongIDDelegate.getValue]
 * - [CharSequenceIDDelegate.getValue]
 *
 *
 * @see CharSequenceID
 * @see IntID
 * @see LongID
 *
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName("ID")
@Suppress("EqualsOrHashCode")
@OptIn(ConfusedIDType::class)
public sealed class ID : Comparable<ID>, Cloneable {
    /*
        实际上常用的ID类型总共就那么几个：整型和字符串。
        如果算上无符号，实际上就只有5种类型就够用了
        正常人谁会用 Double 当ID类型?

        而且就算真有莫名其妙的类型，String会出手。
        也不知道当初谁写的这个ID，搞这么多类型有个屁用，还给自己留坑
     */

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
        @Bonus
        @JvmStatic
        public fun `$`(value: Int): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: Char): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: Long): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: Double): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: Float): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: CharSequence): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: UUID): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: AtomicInteger): ID = value.ID

        @Bonus
        @JvmStatic
        public fun `$`(value: LongAdder): ID = value.ID

        @Bonus
        @JvmStatic
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
@ConfusedIDType
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
@ConfusedIDType
public val Double.ID: DoubleID
    get() = DoubleID(this)

/**
 * 将一个 [Float] 作为 [ID][FloatID].
 */
@get:JvmName("ID")
@ConfusedIDType
public val Float.ID: FloatID
    get() = FloatID(this)

/**
 * 将一个 [CharSequence] 作为 [ID]。
 *
 * 注意，尽可能避免将 [StringBuilder] 等可变序列作为参数提供, 除非你明确的知道你在做什么。
 * [CharSequenceID] 的 [value][CharSequenceID.value] 会直接使用参数的引用，不会进行拷贝等操作。
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
 *
 * @see RandomIDUtil.randomID
 */
@JvmOverloads
public fun randomID(random: Random = Random): ID = RandomIDUtil.randomID(random).ID


/**
 * 取得一个随机ID。
 *
 * @see RandomIDUtil.randomID
 */
public fun randomID(random: java.util.Random): ID = RandomIDUtil.randomID(random.asKotlinRandom()).ID


/**
 * 将一个 [AtomicInteger] 当前的**瞬时值**作为ID。
 */
@get:JvmName("ID")
public val AtomicInteger.ID: IntID
    get() = this.get().ID

/**
 * 将一个 [LongAdder] 当前的**瞬时值**作为ID。
 */
@get:JvmName("ID")
public val LongAdder.ID: LongID
    get() = this.sum().ID

/**
 * 将一个 [LongAccumulator] 当前的**瞬时值**作为ID。
 */
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
 * @see ArbitraryNumericalID
 * @see BigIntegerID
 *
 * @see Int.ID
 * @see Long.ID
 */
@Suppress("MemberVisibilityCanBePrivate", "EqualsOrHashCode")
@SerialName("ID.Number")
@Serializable
@OptIn(ConfusedIDType::class)
public sealed class NumericalID<N : Number> : ID(), NumberSimilarly {

    /**
     * 此数字ID的值。
     */
    public abstract val value: N

    override fun compareTo(other: ID): Int {
        when {
            other === this -> return 0
            other is NumericalID<*> -> {
                return when (other) {
                    is IntID -> toInt().compareTo(other.number)
                    is LongID -> toLong().compareTo(other.number)
                    is DoubleID -> toDouble().compareTo(other.number)
                    is FloatID -> toFloat().compareTo(other.number)
                    is BigIntegerID -> when (this) {
                        is BigIntegerID -> this.value.compareTo(other.value)
                        is BigDecimalID -> this.value.compareTo(other.value.toBigDecimal())
                        is IntID -> number.compareTo(other.value.toInt())
                        is LongID -> number.compareTo(other.value.toLong())
                        is DoubleID -> number.compareTo(other.value.toDouble())
                        is FloatID -> number.compareTo(other.value.toFloat())
                    }

                    is BigDecimalID -> when (this) {
                        is BigDecimalID -> value.compareTo(other.value)
                        is BigIntegerID -> value.compareTo(other.value.toBigInteger())
                        is IntID -> number.compareTo(other.value.toInt())
                        is LongID -> number.compareTo(other.value.toLong())
                        is DoubleID -> number.compareTo(other.value.toDouble())
                        is FloatID -> number.compareTo(other.value.toFloat())
                    }
                }
            }

            other is UIntID -> return -other.compareTo(this)
            other is ULongID -> return -other.compareTo(this)
            else -> return toString().compareTo(other.toString())
        }

    }

    /**
     * 将当前数字转为 [Double]. 同 [Number.toDouble].
     * @see Number.toDouble
     */
    override fun toDouble(): Double = value.toDouble()

    /**
     * 将当前数字转为 [Float]. 同 [Number.toFloat].
     * @see Number.toFloat
     */
    override fun toFloat(): Float = value.toFloat()

    /**
     * 将当前数字转为 [Long]. 同 [Number.toLong].
     * @see Number.toLong
     */
    override fun toLong(): Long = value.toLong()

    /**
     * 将当前数字转为 [Int]. 同 [Number.toInt].
     * @see Number.toInt
     */
    override fun toInt(): Int = value.toInt()

    /**
     * 将当前数字转为 [Char]. 同 [Number.toChar].
     * @see Number.toChar
     */
    override fun toChar(): Char = value.toChar()

    /**
     * 将当前数字转为 [Short]. 同 [Number.toShort].
     * @see Number.toShort
     */
    override fun toShort(): Short = value.toShort()

    /**
     * 将当前数字转为 [Byte]. 同 [Number.toByte].
     * @see Number.toByte
     */
    override fun toByte(): Byte = value.toByte()

    override fun doEquals(other: ID): Boolean {
        if (other is NumericalID<*>) {
            return when (other) {
                is IntID -> toInt() == other.number
                is LongID -> toLong() == other.number
                is DoubleID -> toDouble() == other.number
                is FloatID -> toFloat() == other.number
                is BigIntegerID -> this is BigIntegerID && (value == other.value)
                is BigDecimalID -> this is BigDecimalID && (value == other.value)
            }
        }

        return false
    }

    override fun hashCode(): Int = value.hashCode()
    final override fun toString(): String = value.toString()
}

// region 标准的基础数据ID实现
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
@ConfusedIDType
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
@ConfusedIDType
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
// endregion


/**
 * 使用 [UInt] 字面值的 [ID] 实现。
 *
 * [UInt] 不是 [Number] 的实现，因此 [UIntID] 并非 [NumericalID] 类型。
 * 但是 [UInt] 会提供类似于 [NumericalID] 的一些数值转化类型，并且允许与 [NumericalID] 进行数值比较。
 *
 * [UIntID] 与 [IntID] 是**不同**的，不要使用值可能为负的 [IntID] 来代替 [UIntID]。
 * [UIntID] 在进行 `equals` 和 `compareTo` 的时候，如果目标为数值类型且值为负，
 * 则会直接将其判定为不相等或小数值。
 *
 * 举个明显的例子：
 * ```kotlin
 *  val ui = UInt.MAX_VALUE
 *  val i = UInt.MAX_VALUE.toInt()
 *
 *  println(ui.toInt() == i)            // true
 *  println(ui == i.toUInt())           // true
 *  println(ui.toInt() compareTo i)     // 0
 *  println(ui compareTo i.toUInt())    // 0
 *
 *  val uiID: ID = ui.ID
 *  val iID: ID = i.ID
 *
 *  println(uiID == iID)                // false
 *  println(uiID compareTo iID)         // 1
 * ```
 *
 * 在JVM中，当仅作为数值而互相转化、比较时，`ui` 和 `i` 的实际数值是一样的，
 * 而当它们各自作为 [UIntID] 和 [IntID] 时，
 * 不论是 `equals` 还是 `compareTo` 的结果都表明它们是不同的。
 *
 * @see UInt.ID
 *
 * @since 3.1.0
 *
 */
@SerialName("ID.UInt")
@Serializable(with = UIntID.Serializer::class)
@OptIn(ConfusedIDType::class)
public data class UIntID(public val number: UInt) : ID(), NumberSimilarly {
    override fun toDouble(): Double = number.toDouble()
    override fun toFloat(): Float = number.toFloat()
    override fun toLong(): Long = number.toLong()
    override fun toInt(): Int = number.toInt()
    override fun toChar(): Char = number.toInt().toChar()
    override fun toShort(): Short = number.toShort()
    override fun toByte(): Byte = number.toByte()

    override fun compareTo(other: ID): Int {
        when (other) {
            is UIntID -> return number.compareTo(other.number)
            is NumericalID<*> -> {
                when (other) {
                    is IntID -> {
                        if (other.number < 0) {
                            return 1
                        }

                        return number.compareTo(other.number.toUInt())
                    }

                    is LongID -> {
                        if (other.number < 0) {
                            return 1
                        }
                        if (other.number > UINT_MAX_LONG_VALUE) {
                            return -1
                        }

                        return number.compareTo(other.number.toUInt())
                    }

                    is DoubleID -> {
                        if (other.number < 0) {
                            return 1
                        }

                        if (other.number > UINT_MAX_D_VALUE) {
                            return -1
                        }

                        return number.compareTo(other.number.toUInt())
                    }

                    is FloatID -> {
                        if (other.number < 0) {
                            return 1
                        }

                        return number.compareTo(other.number.toUInt())
                    }

                    is BigDecimalID -> {
                        if (other.value < BigDecimal.ZERO) {
                            return 1
                        }

                        if (other.value > UINT_MAX_BGD) {
                            return -1
                        }

                        return other.value.compareTo(BigDecimal(number.toString()))
                    }

                    is BigIntegerID -> {
                        if (other.value < BigInteger.ZERO) {
                            return 1
                        }

                        if (other.value > UINT_MAX_BGI) {
                            return -1
                        }

                        return other.value.compareTo(BigInteger(number.toString()))
                    }
                }
            }

            else -> return number.toString().compareTo(other.toString())
        }
    }

    override fun doEquals(other: ID): Boolean {
        if (other is UIntID) return number == other.number
        if (other is ULongID) return number == other.number.toUInt()
        if (other is NumericalID<*>) {
            val v = other.toLong()
            if (v < 0) {
                return false
            }

            return number == v.toUInt()
        }

        return false
    }

    public companion object {
        /**
         * [UInt.MAX_VALUE] 作为 [Long] 的值。
         */
        private const val UINT_MAX_LONG_VALUE: Long = 4294967295L
        private const val UINT_MAX_D_VALUE: Double = 4294967295.0
        private val UINT_MAX_BGD = BigDecimal(UInt.MAX_VALUE.toString())
        private val UINT_MAX_BGI = BigInteger(UInt.MAX_VALUE.toString())
    }

    internal object Serializer : KSerializer<UIntID> {
        override fun deserialize(decoder: Decoder): UIntID {
            val value = UInt.serializer().deserialize(decoder)
            return UIntID(value)
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UIntID", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: UIntID) {
            UInt.serializer().serialize(encoder, value.number)
        }
    }
}


/**
 * 使用 [ULong] 字面值的 [ID] 实现。
 *
 * [ULong] 不是 [Number] 的实现，因此 [ULongID] 并非 [NumericalID] 类型。
 * 但是 [ULong] 会提供类似于 [NumericalID] 的一些数值转化类型，并且允许与 [NumericalID] 进行数值比较。
 *
 * [ULongID] 与 [LongID] 是**不同**的，不要使用值可能为负的 [LongID] 来代替 [ULongID]。
 * [ULongID] 在进行 `equals` 和 `compareTo` 的时候，如果目标为数值类型且值为负，
 * 则会直接将其判定为不相等或小数值。
 *
 * 举个明显的例子：
 * ```kotlin
 *  val ul = ULong.MAX_VALUE
 *  val l = ULong.MAX_VALUE.toLong()
 *
 *  println(ul.toLong() == l)          // true
 *  println(ul == l.toULong())         // true
 *  println(ul.toLong() compareTo l)   // 0
 *  println(ul compareTo l.toULong())  // 0
 *
 *  val ulID: ID = ul.ID
 *  val lID: ID = l.ID
 *
 *  println(ulID == lID)                // false
 *  println(ulID compareTo lID)         // 1
 * ```
 *
 * 在JVM中，当仅作为数值而互相转化、比较时，`ul` 和 `l` 的实际数值是一样的，
 * 而当它们各自作为 [ULongID] 和 [LongID] 时，
 * 不论是 `equals` 还是 `compareTo` 的结果都表明它们是不同的。
 *
 * @see ULong.ID
 *
 * @since 3.1.0
 *
 */
@SerialName("ID.ULong")
@Serializable(with = ULongID.Serializer::class)
@OptIn(ConfusedIDType::class)
public data class ULongID(public val number: ULong) : ID(), NumberSimilarly {
    override fun toDouble(): Double = number.toDouble()
    override fun toFloat(): Float = number.toFloat()
    override fun toLong(): Long = number.toLong()
    override fun toInt(): Int = number.toInt()
    override fun toChar(): Char = number.toInt().toChar()
    override fun toShort(): Short = number.toShort()
    override fun toByte(): Byte = number.toByte()

    override fun compareTo(other: ID): Int {
        when (other) {
            is UIntID -> return number.compareTo(other.number)
            is ULongID -> return number.compareTo(other.number)
            is NumericalID<*> -> {
                when (other) {
                    is IntID -> {
                        if (other.number < 0) {
                            return 1
                        }

                        return number.compareTo(other.number.toULong())
                    }

                    is LongID -> {
                        if (other.number < 0) {
                            return 1
                        }
                        return number.compareTo(other.number.toULong())
                    }

                    is DoubleID -> {
                        if (other.number < 0) {
                            return 1
                        }

                        return number.compareTo(other.number.toULong())
                    }

                    is FloatID -> {
                        if (other.number < 0) {
                            return 1
                        }

                        return number.compareTo(other.number.toULong())
                    }

                    is BigDecimalID -> {
                        if (other.value < BigDecimal.ZERO) {
                            return 1
                        }

                        if (other.value > ULONG_MAX_BGD) {
                            return -1
                        }

                        return other.value.compareTo(BigDecimal(number.toString()))
                    }

                    is BigIntegerID -> {
                        if (other.value < BigInteger.ZERO) {
                            return 1
                        }

                        if (other.value > ULONG_MAX_BGI) {
                            return -1
                        }

                        return other.value.compareTo(BigInteger(number.toString()))
                    }
                }
            }

            else -> return number.toString().compareTo(other.toString())
        }
    }

    override fun doEquals(other: ID): Boolean {
        if (other is ULongID) return number == other.number
        if (other is UIntID) return number == other.number.toULong()
        if (other is NumericalID<*>) {
            val v = other.toLong()
            if (v < 0) {
                return false
            }

            return number == v.toULong()
        }

        return false
    }

    public companion object {
        // ULong MAX = 18446744073709551615
        private val ULONG_MAX_BGD = BigDecimal(ULong.MAX_VALUE.toString())
        private val ULONG_MAX_BGI = BigInteger(ULong.MAX_VALUE.toString())
    }

    internal object Serializer : KSerializer<ULongID> {
        override fun deserialize(decoder: Decoder): ULongID {
            val value = ULong.serializer().deserialize(decoder)
            return ULongID(value)
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ULongID", PrimitiveKind.LONG)
        override fun serialize(encoder: Encoder, value: ULongID) {
            ULong.serializer().serialize(encoder, value.number)
        }
    }
}


/**
 * 将一个 [UInt] 作为 [ID][UIntID].
 *
 * @since 3.1.0
 */
@get:JvmName("UID")
public val UInt.ID: UIntID
    get() = UIntID(this)


/**
 * 将一个 [ULong] 作为 [ID][ULongID].
 *
 * @since 3.1.0
 */
@get:JvmName("UID")
public val ULong.ID: ULongID
    get() = ULongID(this)


/**
 * 一个任意的 [数字ID][NumericalID] 实例, 由平台进行实现。
 * 作为一个任意的 [数字][Number] ID，实现的内部字面量需要是不可变的。
 *
 * @see BigDecimalID
 * @see BigIntegerID
 *
 */
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
@ConfusedIDType
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
 * _过长的整型应当考虑使用 [CharSequenceID] 作为ID_
 *
 * @see BigInteger.ID
 */
@SerialName("ID.BigInteger")
@Serializable(with = BigIntegerID.Serializer::class)
@ConfusedIDType
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
@get:JvmName("ID")
@ConfusedIDType
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
@get:JvmName("ID")
@ConfusedIDType
public val BigInteger.ID: BigIntegerID
    get() = when (this) {
        BigInteger.ZERO -> BigIntegerID.ZERO
        BigInteger.ONE -> BigIntegerID.ONE
        BigInteger.TEN -> BigIntegerID.TEN
        else -> BigIntegerID(this)
    }

/**
 * 将 [AtomicLong] 的瞬时值转化为 [LongID]。
 */
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
 * 尝试将当前ID转为一个 [NumericalID]。
 *
 * 如果当前ID不是一个 [NumericalID] 类型，则会尝试将其转化为合适的数字类型。
 *
 * _应优先考虑使用更明确的 [tryToLongID]_
 *
 * @param intAsLong 如果为 `true`, 则当提供的ID字面值中的字符小于 `10` 个时，依旧将其作为 [Long] 处理。否则将会作为 [Int] 处理。
 * @throws IDException 无法进行转化或内部抛出 [NumberFormatException] 时。
 */
@JvmOverloads
@ConfusedIDType
public fun ID.tryToNumericalID(intAsLong: Boolean = true): NumericalID<*> {
    if (this is NumericalID<*>) return this
    else {
        try {
            // Int MAX : 2147483647
            // Long MAX: 9223372036854775807
            val literal = this.literal
            if (literal.isEmpty()) throw IDException("Unable to convert an empty string ('') to NumericalID.")

            fun bigDecimalID(): BigDecimalID = BigDecimal(literal).ID

            if (literal.length > 19) {
                // > Long MAX
                return bigDecimalID()
            }

            val i1 = literal.indexOf('.')
            if (i1 >= 0 && literal.lastIndexOf('.') == i1) {
                // maybe double
                return literal.toDouble().ID
            }

            if (literal.length == 19) {
                return literal.toLongOrNull()?.ID ?: bigDecimalID()
            }


            if (literal.length in 10..18) {
                return literal.toLong().ID
            }
            // less than 10, int value.
            return if (intAsLong) literal.toLong().ID else literal.toInt().ID
        } catch (nfe: NumberFormatException) {
            throw IDException("Unable to convert id [$this] to NumericalID", nfe)
        }
    }
}

/**
 * 尝试将当前ID转为一个 [LongID].
 *
 * 如果不是数字ID类型，则会尝试通过 [String.toLong] 进行转化。
 *
 * @throws IDException 无法转化为数字（内部抛出 [NumberFormatException]）时。
 */
public fun ID.tryToLongID(): LongID = when (this) {
    is LongID -> this
    is NumericalID<*> -> this.toLong().ID
    else -> try {
        literal.toLong().ID
    } catch (nfe: NumberFormatException) {
        throw IDException("Unable to convert id [$this] to LongID", nfe)
    }
}

/**
 * 尝试将当前ID转化为一个 [Long]。
 *
 * 如果不是数字ID类型，则会尝试通过 [String.toLong] 进行转化。
 *
 * @throws IDException 无法转化为数字（内部抛出 [NumberFormatException]）时。
 */
public fun ID.tryToLong(): Long = when (this) {
    is LongID -> this.number
    is NumericalID<*> -> this.toLong()
    else -> try {
        literal.toLong()
    } catch (nfe: NumberFormatException) {
        throw IDException("Unable to convert id [$this] to Long", nfe)
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

/**
 * 一个提供数值转化能力的接口。
 *
 * @since 3.1.0
 */
public interface NumberSimilarly {
    /**
     * 将当前数字转为 [Double]. 类似于 [Number.toDouble].
     * @see Number.toDouble
     */
    public fun toDouble(): Double

    /**
     * 将当前数字转为 [Float]. 类似于 [Number.toFloat].
     * @see Number.toFloat
     */
    public fun toFloat(): Float

    /**
     * 将当前数字转为 [Long]. 类似于 [Number.toLong].
     * @see Number.toLong
     */
    public fun toLong(): Long

    /**
     * 将当前数字转为 [Int]. 类似于 [Number.toInt].
     * @see Number.toInt
     */
    public fun toInt(): Int

    /**
     * 将当前数字转为 [Char]. 类似于 [Number.toChar].
     * @see Number.toChar
     */
    public fun toChar(): Char

    /**
     * 将当前数字转为 [Short]. 类似于 [Number.toShort].
     * @see Number.toShort
     */
    public fun toShort(): Short

    /**
     * 将当前数字转为 [Byte]. 类似于 [Number.toByte].
     * @see Number.toByte
     */
    public fun toByte(): Byte
}

