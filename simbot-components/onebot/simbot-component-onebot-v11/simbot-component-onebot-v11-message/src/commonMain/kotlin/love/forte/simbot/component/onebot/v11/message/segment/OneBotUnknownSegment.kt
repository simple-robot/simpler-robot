/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.message.segment

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI


/**
 * 一个未知类型的 [OneBotMessageSegment]。
 *
 * 当所有已知的 [OneBotMessageSegment]
 * 子类型均无法直接通过多态序列化器反序列化时，
 * 将会直接被解析为 [OneBotUnknownSegment]。
 *
 * [OneBotUnknownSegment] 本身不可序列化，
 * 需要向 [OneBotUnknownSegmentDeserializer] 提供一个多态类型后进行序列化，
 * 且 [data] 的类型为 [JsonObject]，因此仅支持 JSON 格式。
 *
 * 实验性：[OneBotUnknownSegment] 的应用（包括序列化与反序列化）尚在实验中，
 * 可能不稳定，且未来可能会随时删除、更改。
 *
 * @see OneBotUnknownSegmentDeserializer
 *
 * @author ForteScarlet
 */
@FragileSimbotAPI
@ExperimentalOneBotAPI
public data class OneBotUnknownSegment
@InternalOneBotAPI
constructor(
    val type: String,
    override val data: JsonElement? = null
) : OneBotMessageSegment

/**
 * [OneBotUnknownSegment] 的反序列化器，
 * 将任意未知的segment内容解析为 [OneBotUnknownSegment]
 *
 */
@OptIn(FragileSimbotAPI::class)
@InternalOneBotAPI
@ExperimentalOneBotAPI
public object OneBotUnknownSegmentDeserializer :
    DeserializationStrategy<OneBotUnknownSegment> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("OneBotUnknownSegment") {
            element<String>("type")
            element<JsonObject?>("data", isOptional = true)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): OneBotUnknownSegment {
        return decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val type = decodeStringElement(descriptor, 0)
                val data = decodeNullableSerializableElement(descriptor, 1, JsonObject.serializer())

                return@decodeStructure OneBotUnknownSegment(type, data)
            }

            var type: String? = null
            var data: JsonElement? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    0 -> type = decodeStringElement(descriptor, 0)
                    1 -> data = decodeSerializableElement(descriptor, 1, JsonElement.serializer())
                    else -> error("Unexpected index: $index")
                }
            }

            if (type == null) throw SerializationException("Required property 'type' is null or miss")

            OneBotUnknownSegment(type, data)
        }

    }
}

/**
 * [OneBotUnknownSegment] 的多态序列化器，
 * 提供类型 `type` 后进行序列化。
 */
@OptIn(FragileSimbotAPI::class)
@InternalOneBotAPI
@ExperimentalOneBotAPI
public class OneBotUnknownSegmentPolymorphicSerializer(type: String) :
    SerializationStrategy<OneBotMessageSegment> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(type) {
            element<JsonObject?>("data", isOptional = true)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: OneBotMessageSegment) {
        if (value is OneBotUnknownSegment) {
            encoder.encodeStructure(descriptor) {
                this.encodeNullableSerializableElement(descriptor, 0, JsonElement.serializer(), value.data)
            }
        }
    }
}
