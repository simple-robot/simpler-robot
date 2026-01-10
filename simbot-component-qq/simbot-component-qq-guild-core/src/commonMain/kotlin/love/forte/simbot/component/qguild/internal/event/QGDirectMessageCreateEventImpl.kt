/*
 * Copyright (c) 2024-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.event.QGDirectMessageCreateEvent
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.dms.toDmsContact
import love.forte.simbot.component.qguild.internal.message.QGMessageContentImpl
import love.forte.simbot.component.qguild.message.QGMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendDmsMessage
import love.forte.simbot.component.qguild.utils.alsoEmitPostReplyEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Message


/**
 *
 * @author ForteScarlet
 */
internal class QGDirectMessageCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: Message,
    private val eventId: String?,
) : QGDirectMessageCreateEvent() {
    override val id: ID
        get() = eventId?.ID ?: sourceEventEntity.id.ID

    override val messageContent: QGMessageContent = QGMessageContentImpl(bot, sourceEventEntity)

    private val _content = sourceEventEntity.author.toDmsContact(
        bot = bot,
        guildId = sourceEventEntity.guildId,
        currentMsgId = sourceEventEntity.id
    )

    override suspend fun content(): QGDmsContact = _content

    override suspend fun reply(text: String): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(text))
    }

    override suspend fun reply(message: love.forte.simbot.message.Message): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(message))
    }

    override suspend fun reply(messageContent: MessageContent): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(messageContent))
    }

    private suspend fun emitPreReplyEventAndReply(message: InteractionMessage): QGMessageReceipt {
        val event = QGDirectMessageCreateEventPreReplyEventImpl(bot, this, message)
        bot.emitMessagePreSendEvent(event)
        return replyByInteractionMessage(event.useMessage())
    }

    private suspend fun replyByInteractionMessage(message: InteractionMessage): QGMessageReceipt {
        var ref: Message.Reference? = null

        fun MessageSendApi.Body.Builder.configMsgIdAndRef() {
            if (msgId == null) {
                msgId = sourceEventEntity.id
            }
            if (messageReference == null) {
                messageReference = ref ?: Message.Reference(sourceEventEntity.id).also { ref = it }
            }
        }

        return when (message) {
            is InteractionMessage.Message -> {
                bot.sendDmsMessage(sourceEventEntity.guildId, message.message) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendDmsMessage(sourceEventEntity.guildId, message.messageContent) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.Text -> {
                bot.sendDmsMessage(sourceEventEntity.guildId, message.text) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
        }.alsoEmitPostReplyEvent(bot, this, message)
    }
}