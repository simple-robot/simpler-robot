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
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.math.BigInteger


// public fun <N: Number> N.ID(): NumericalID<N> {
// }

/**
 * 由 [BigDecimal] 作为字面量值的 [NumericalID] 实现。
 */
@SerialName("ID.N.A.BD")
@Serializable(with = BigDecimalID.BigDecimalIDSerializer::class)
public class BigDecimalID(override val value: BigDecimal) : ArbitraryNumericalID<BigDecimal>() {
    internal object BigDecimalIDSerializer : KSerializer<BigDecimalID> {
        override fun deserialize(decoder: Decoder): BigDecimalID = BigDecimalID(BigDecimal(decoder.decodeString()))
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("bigDecimal", PrimitiveKind.STRING)
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
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("bigInteger", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BigIntegerID) {
            encoder.encodeString(value.value.toString())
        }
    }
}




/**
 * 得到平台实现的额外 [ArbitraryNumericalID] 实现。
 */
@Suppress("FunctionName", "UNCHECKED_CAST")
public actual fun <N : Number> N.ID(): ArbitraryNumericalID<N> {
    return when (this) {
        is BigDecimal -> BigDecimalID(this) as ArbitraryNumericalID<N>
        is BigInteger -> BigIntegerID(this) as ArbitraryNumericalID<N>
        // is

        else -> throw NoSuchIDTypeException(this::class.toString())
    }
}

//
// /**
//  * 一个任意的 [数字ID][NumericalID] 实例, 由平台进行实现。
//  * 作为一个任意的 [数字][Number] ID，实现的内部字面量需要是不可变的，因此实现不应是 [java.util.concurrent.atomic.AtomicInteger] 等可变类。
//
//  * 在 `JVM` 平台下，支持 BigDecimal 等常见 [Number] 实现。
//  */
// @Suppress("CanBeParameter")
// @SerialName("ID.N.A")
// @Serializable
// public abstract class ArbitraryNumericalID<N : Number> internal constructor(): NumericalID<N>()