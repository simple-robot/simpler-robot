/*
 *     Copyright (c) 2022-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.kook.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


/**
 * 将 [Boolean] 转为 [Int], 其中，false = 0, true = 1.
 *
 * 在反序列化的时候，0 = false，其他情况为true。
 *
 * since 4.1.7: 在 JSON 序列化下，对 Boolean 的转化会更宽松，支持同时解析 int/boolean/string 类型。
 */
public object BooleanToIntSerializer : KSerializer<Boolean> {
    @Suppress("ReturnCount")
    override fun deserialize(decoder: Decoder): Boolean {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return decoder.decodeInt() != 0

        val decodeJsonElement = jsonDecoder.decodeJsonElement()
        val primitive = decodeJsonElement as? JsonPrimitive
            ?: error("Unexpected JSON element type: $decodeJsonElement")
        if (primitive is JsonNull) {
            return false
        }

        primitive.intOrNull?.let { return it != 0 }
        primitive.booleanOrNull?.let { return it }

        return when (primitive.content) {
            "true", "TRUE", "True", "1" -> true
            "false", "FALSE", "False", "0" -> false
            else -> error("Unexpected JSON element type: $primitive")
        }
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BooleanToInt", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(if (value) 1 else 0)
    }
}
