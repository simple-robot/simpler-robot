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


// public fun <N: Number> N.ID(): NumericalID<N> {
// }

@SerialName("ID.N.A.BD")
@Serializable(with = BigDecimalID.BigDecimalIDSerializer::class)
public class BigDecimalID(value: BigDecimal) : ArbitraryNumericalID<BigDecimal>(value) {
    internal object BigDecimalIDSerializer : KSerializer<BigDecimalID> {
        override fun deserialize(decoder: Decoder): BigDecimalID = BigDecimalID(BigDecimal(decoder.decodeString()))
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("bigDecimal", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BigDecimalID) {
            encoder.encodeString(value.value.toString())
        }
    }
}

public actual fun <N: Number> N.resolveToID(): ArbitraryNumericalID<N> {

    TODO()
}

/*
Conflicting overloads:
public expect fun <N : Number> N.ID(): NumericalID<N>
defined in love.forte.simbot in file ID_jvm.kt,
public expect fun <N : Number> N.ID(): NumericalID<N>
defined in love.forte.simbot in file ID_jvm.kt
 */