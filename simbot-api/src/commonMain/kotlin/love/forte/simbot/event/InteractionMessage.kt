/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.event

import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendSupport
import kotlin.jvm.JvmStatic

/**
 * 拦截或通知中 [SendSupport.send] 或 [ReplySupport.reply] 的消息内容。
 */
public sealed class InteractionMessage {
    /**
     * 当参数类型为 [String] 时，表示发送的文本消息。
     */
    public class Text internal constructor(public val text: String) : InteractionMessage() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Text) return false

            if (text != other.text) return false

            return true
        }

        override fun hashCode(): Int {
            return text.hashCode()
        }

        override fun toString(): String {
            return "Text(text='$text')"
        }
    }

    /**
     * 当参数类型为 [love.forte.simbot.message.Message] 时，表示发送的消息。
     */
    public class Message internal constructor(public val message: love.forte.simbot.message.Message) :
        InteractionMessage() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Message) return false

            if (message != other.message) return false

            return true
        }

        override fun hashCode(): Int {
            return message.hashCode()
        }

        override fun toString(): String {
            return "Message(message=$message)"
        }

    }

    /**
     * 当参数类型为 [love.forte.simbot.message.MessageContent] 时，表示发送的消息内容。
     */
    public class MessageContent internal constructor(
        public val messageContent: love.forte.simbot.message.MessageContent
    ) : InteractionMessage() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MessageContent) return false

            if (messageContent != other.messageContent) return false

            return true
        }

        override fun hashCode(): Int {
            return messageContent.hashCode()
        }

        override fun toString(): String {
            return "MessageContent(messageContent=$messageContent)"
        }
    }

    /**
     * 如果组件或 [SendSupport] 的实现者提供了其他三个类型参数以外的参数，
     * 则需要通过 [Extension] 对其进行扩展。
     */
    public abstract class Extension : InteractionMessage()

    public companion object {
        /**
         * 创建一个文本消息。
         * @see InteractionMessage
         */
        @JvmStatic
        public fun valueOf(text: String): Text = Text(text)

        /**
         * 创建一个消息。
         * @see InteractionMessage
         */
        @JvmStatic
        public fun valueOf(message: love.forte.simbot.message.Message): Message = Message(message)

        /**
         * 创建一个消息内容。
         * @see InteractionMessage
         */
        @JvmStatic
        public fun valueOf(messageContent: love.forte.simbot.message.MessageContent): MessageContent =
            MessageContent(messageContent)
    }
}
