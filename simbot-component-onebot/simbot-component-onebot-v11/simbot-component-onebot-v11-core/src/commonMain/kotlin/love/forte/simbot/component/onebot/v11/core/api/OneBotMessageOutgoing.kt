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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.utils.resolveToOneBotSegmentList
import love.forte.simbot.component.onebot.v11.message.OneBotMessageElement
import love.forte.simbot.component.onebot.v11.message.OneBotMessageElementSerializer
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegmentSerializer
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * 代表一个用于OneBot API中作为 `message` 发送的消息内容。
 * 它是一个**仅用于序列化**的类型。
 *
 * 在 OneBot11 协议中，API发送消息内容 `message` 时，
 * 可能会有：
 * - 字符串值
 * - 消息段数组
 * - 单个消息段对象
 * 三种类型的数据。前两个类型分别对应 [String] 和 List<[OneBotMessageElement]>，
 * 而第三个可以使用单元素列表表示，因此没有必要直接支持，可以使用 `listOf` 等方式以第二个类型实现。
 *
 * 由于序列化时无法保留具体的多态信息，因此 [OneBotMessageOutgoing] 只能保障序列化，
 * 而无法保障反序列化，请避免使用反序列化构建 [OneBotMessageOutgoing]。
 *
 * @author ForteScarlet
 */
@Serializable(OneBotMessageOutgoingSerializer::class)
public sealed class OneBotMessageOutgoing {
    /**
     * 字符串值格式的值。
     */
    @Serializable
    public data class StringValue(public val value: String) : OneBotMessageOutgoing()

    @Serializable
    public data class SegmentsValue(
        public val segments: List<OneBotMessageSegment>
    ) : OneBotMessageOutgoing()

    public companion object {
        /**
         * 通过字符串值构建 [StringValue]。
         */
        @JvmStatic
        public fun create(value: String): StringValue = StringValue(value)

        /**
         * 通过消息段列表构建 [SegmentsValue]。
         */
        @JvmStatic
        public fun create(segments: List<OneBotMessageSegment>): SegmentsValue = SegmentsValue(segments)

        /**
         * 通过 [Messages] 构建 [SegmentsValue]。
         * @since 1.9.0
         */
        @JvmStatic
        @ExperimentalOneBotAPI
        @JvmOverloads
        public fun create(message: Message, bot: OneBotBot? = null): SegmentsValue =
            create(message.resolveToOneBotSegmentList(bot))
    }
}

/**
 * [OneBotMessageOutgoing] 的序列化器实现。
 * 仅保证序列化，反序列化很可能会产生异常，且尽可能避免使用。
 */
public object OneBotMessageOutgoingSerializer : KSerializer<OneBotMessageOutgoing> {
    @FragileSimbotAPI
    override fun deserialize(decoder: Decoder): OneBotMessageOutgoing {
        return try {
            OneBotMessageOutgoing.create(decoder.decodeString())
        } catch (se: SerializationException) {
            try {
                OneBotMessageOutgoing.create(OneBotMessageSegmentSerializer.deserialize(decoder))
            } catch (e: Exception) {
                e.addSuppressed(se)
                throw e
            }
        }
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        "love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing"
    ) {
        element("value", String.serializer().descriptor, isOptional = true)
        element("segments", OneBotMessageElementSerializer.descriptor, isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: OneBotMessageOutgoing) {
        when (value) {
            is OneBotMessageOutgoing.SegmentsValue -> OneBotMessageSegmentSerializer.serialize(encoder, value.segments)
            is OneBotMessageOutgoing.StringValue -> encoder.encodeString(value.value)
        }
    }
}
