/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.message.MessageReference
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * QQ频道的消息引用。与 [Message.Reference] 对应。
 *
 * [QGReference] 在消息链中被解析时，如果存在，会**覆盖**当前所有已经被解析存在的消息体的 [messageReference][MessageSendApi.Body.messageReference]，
 * 但是无法影响到尚未被构建的未知 builder。
 *
 * 更多相关说明参考 [MessageParsers.parse]。
 *
 * @author ForteScarlet
 */
@SerialName("qg.reference")
@Serializable
public class QGReference private constructor(
    public val messageId: ID,
    public val ignoreGetMessageError: Boolean,
    public val channelId: ID?
) : QGMessageElement, MessageReference {
    override val id: ID
        get() = messageId

    private lateinit var _source: Message.Reference

    /**
     * 得到当前 [QGReference] 对应的原始类型 [Message.Reference]。
     */
    public val source: Message.Reference
        get() = if (::_source.isInitialized) _source else {
            Message.Reference(messageId.literal, ignoreGetMessageError).also {
                _source = it
            }
        }


    public companion object {
        /**
         * 直接通过 [Message.Reference] 构造一个 [QGReference]。
         */
        @JvmStatic
        @JvmName("of")
        @JvmOverloads
        public fun Message.Reference.toMessage(channelId: ID? = null): QGReference =
            QGReference(messageId.ID, ignoreGetMessageError, channelId).also {
                it._source = this
            }

        /**
         * 通过属性构造 [QGReference]。
         *
         * @param messageId 同 [Message.Reference.messageId]
         * @param ignoreGetMessageError 同 [Message.Reference.ignoreGetMessageError]
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(messageId: ID, ignoreGetMessageError: Boolean = false, channelId: ID? = null): QGReference =
            QGReference(messageId, ignoreGetMessageError, channelId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QGReference) return false

        if (messageId != other.messageId) return false
        if (ignoreGetMessageError != other.ignoreGetMessageError) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageId.hashCode()
        result = 31 * result + ignoreGetMessageError.hashCode()
        return result
    }

    override fun toString(): String {
        return "QGReference(messageId=$messageId, ignoreGetMessageError=$ignoreGetMessageError, channelId=$channelId)"
    }


}


internal object ReferenceParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        if (element is QGReference) {
            builderContext.builders.forEach {
                it.messageReference = element.source
            }
        } else if (element is MessageReference) {
            builderContext.builders.forEach {
                it.messageReference = Message.Reference(messageId = element.id.literal)
            }
        }
    }

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGReference) {
            builderContext.builders.forEach {
                it.messageReference = element.source
            }
        } else if (element is MessageReference) {
            builderContext.builders.forEach {
                it.messageReference = Message.Reference(messageId = element.id.literal)
            }
        }
    }
}
