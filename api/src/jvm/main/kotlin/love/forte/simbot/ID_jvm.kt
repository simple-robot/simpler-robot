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