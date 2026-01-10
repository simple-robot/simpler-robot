/*
 * Copyright (c) 2023-2025. ForteScarlet.
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
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.castChannel
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.internal.message.QGMessageContentImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.component.qguild.utils.alsoEmitPostReplyEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.Message


/**
 *
 * @author ForteScarlet
 */
internal class QGAtMessageCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: Message,
    private val eventId: String?,
) : QGAtMessageCreateEvent() {
    override val id: ID
        get() = eventId?.ID ?: with(sourceEventEntity) {
            buildString(guildId.length + channelId.length + id.length + 3) {
                append(guildId).append('.')
                append(channelId).append('.')
                append(id).append('.')
            }
        }.ID

    override val messageContent: QGMessageContentImpl = QGMessageContentImpl(bot, sourceEventEntity)

    override suspend fun reply(message: love.forte.simbot.message.Message): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(message))
    }

    override suspend fun reply(messageContent: MessageContent): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(messageContent))
    }

    override suspend fun reply(text: String): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(text))
    }

    private suspend fun emitPreReplyEventAndReply(message: InteractionMessage): QGMessageReceipt {
        val event = QGAtMessageCreateEventPreReplyEventImpl(bot, this, message)
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
                bot.sendMessage(sourceEventEntity.channelId, message.message) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendMessage(sourceEventEntity.channelId, message.messageContent) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.Text -> {
                bot.sendMessage(sourceEventEntity.channelId, message.text) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
        }.alsoEmitPostReplyEvent(bot, this, message)
    }

    override suspend fun source(): QGGuild {
        return with(sourceEventEntity) {
            bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")
        }
    }

    override suspend fun author(): QGMemberImpl {
        return with(sourceEventEntity) {
            bot.queryMember(guildId = guildId, userId = author.id)
                ?: throw NoSuchElementException("member(id=${author.id})")
        }
    }

    override suspend fun content(): QGTextChannel {
        return with(sourceEventEntity) {
            bot.queryChannel(id = channelId, currentMsgId = id)
                ?.castChannel<QGTextChannel> { ChannelType.TEXT }
                ?: throw NoSuchElementException("channel(id=${channelId})")
        }
    }
}
