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
