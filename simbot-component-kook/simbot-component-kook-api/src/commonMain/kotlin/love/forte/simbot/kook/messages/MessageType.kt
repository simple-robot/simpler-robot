/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

@file:Suppress("unused")

package love.forte.simbot.kook.messages

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.kook.messages.MessageType.Companion.CARD
import love.forte.simbot.kook.messages.MessageType.Companion.FILE
import love.forte.simbot.kook.messages.MessageType.Companion.IMAGE
import love.forte.simbot.kook.messages.MessageType.Companion.KMARKDOWN
import love.forte.simbot.kook.messages.MessageType.Companion.TEXT
import love.forte.simbot.kook.messages.MessageType.Companion.VIDEO
import kotlin.jvm.JvmField


/**
 *
 * 消息的类型。
 * - [消息发送 - type字段](https://developer.kaiheila.cn/doc/http/message#%E5%8F%91%E9%80%81%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 * - [事件主要格式 - 格式说明](https://developer.kaiheila.cn/doc/event/event-introduction)
 *
 * @see TEXT
 * @see IMAGE
 * @see VIDEO
 * @see FILE
 * @see KMARKDOWN
 * @see CARD
 * @author ForteScarlet
 */
@Serializable(MessageTypeSerializer::class)
public sealed class MessageType {
    /**
     * type value.
     */
    public abstract val type: Int

    public companion object {

        /**
         * 文本消息
         */
        @JvmField
        public val TEXT: MessageType = StandardMessageType("TEXT", 1)

        /**
         * 图片消息
         */
        @JvmField
        public val IMAGE: MessageType = StandardMessageType("IMAGE", 2)

        /**
         * 视频消息
         */
        @JvmField
        public val VIDEO: MessageType = StandardMessageType("VIDEO", 3)

        /**
         * 文件消息
         */
        @JvmField
        public val FILE: MessageType = StandardMessageType("FILE", 4)

        /**
         * KMarkdown 消息
         */
        @JvmField
        public val KMARKDOWN: MessageType = StandardMessageType("KMARKDOWN", 9)

        /**
         * Card Message 消息
         */
        @JvmField
        public val CARD: MessageType = StandardMessageType("CARD", 10)

        /**
         * 系统消息
         */
        @JvmField
        public val SYSTEM: MessageType = StandardMessageType("SYSTEM", 255)

        /**
         * 标准消息类型的汇总列表。
         *
         * @see TEXT
         * @see IMAGE
         * @see VIDEO
         * @see FILE
         * @see KMARKDOWN
         * @see CARD
         */
        public val standards: List<MessageType> = listOf(
            TEXT,
            IMAGE,
            VIDEO,
            FILE,
            KMARKDOWN,
            CARD,
            SYSTEM
        )

        // TODO
//        /**
//         * 通过 [Event.Type] 解析得到 [MessageType].
//         */
//        public fun byEventType(eventType: Event.Type): MessageType? {
//            return when(eventType) {
//                Event.Type.TEXT -> TEXT
//                Event.Type.IMAGE -> IMAGE
//                Event.Type.VIDEO -> VIDEO
//                Event.Type.FILE -> FILE
//                // Event.Type.VOICE -> VOICE
//                Event.Type.KMD -> KMARKDOWN
//                Event.Type.CARD -> CARD
//                Event.Type.SYS -> SYSTEM // 不能用于发送
//                else -> null
//            }
//        }

    }

    internal class StandardMessageType internal constructor(private val standardName: String, override val type: Int) :
        MessageType() {
        override fun toString(): String = "StandardMessageType(name=$standardName, type=$type)"
        val name: String = standardName.lowercase()
    }

    internal class MixedMessageType(override val type: Int) : MessageType()

    override fun toString(): String = "MessageType(type=$type)"
}


/**
 * [MessageType] 的序列化器, 使用 [MessageType.type] 作为字面量数字进行序列化。
 */
public object MessageTypeSerializer : KSerializer<MessageType> {
    override fun deserialize(decoder: Decoder): MessageType {
        val typeValue = decoder.decodeInt()
        return MessageType.standards.find { it.type == typeValue } ?: MessageType.MixedMessageType(typeValue)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("messageType", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: MessageType) {
        encoder.encodeInt(value.type)
    }

}
