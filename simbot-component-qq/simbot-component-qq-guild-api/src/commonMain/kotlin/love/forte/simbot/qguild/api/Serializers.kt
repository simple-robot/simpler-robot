/*
 * Copyright (c) 2022-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * - `0` -> false
 * - other -> true, decode true to `1`
 */
public object NumberAsBooleanSerializer : KSerializer<Boolean> {
    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeInt() != 0
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BooleanToInt", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(value.toNumber())
    }

    /**
     * 将 [Boolean] 转为 [Int]。
     */
    private fun Boolean.toNumber(): Int = if (this) 1 else 0
}


internal object EmptyUnitSerializer : KSerializer<Unit> {
    private val ser = Unit.serializer()
    override fun deserialize(decoder: Decoder) {
        // Just do nothing?
    }

    override val descriptor: SerialDescriptor
        get() = ser.descriptor

    override fun serialize(encoder: Encoder, value: Unit) {
        ser.serialize(encoder, value)
    }
}
