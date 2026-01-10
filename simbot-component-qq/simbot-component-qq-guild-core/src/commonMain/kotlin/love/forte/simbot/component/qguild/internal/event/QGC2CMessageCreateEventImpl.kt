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

import love.forte.simbot.common.atomic.AtomicInt
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGC2CMessageCreateEvent
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.friend.toFriend
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.component.qguild.message.QGGroupAndC2CMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendUserMessage
import love.forte.simbot.component.qguild.utils.alsoEmitPostReplyEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.event.C2CMessageCreate


/**
 *
 * @author ForteScarlet
 */
internal class QGC2CMessageCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CMessageCreate,
    private val eventId: String?,
    private val msgSeq: AtomicInt = atomic(1)
) : QGC2CMessageCreateEvent() {
    override val id: ID
        get() = eventId?.ID ?: sourceEventEntity.data.id.ID

    override val messageContent: QGGroupAndC2CMessageContent =
        QGGroupAndC2CMessageContentImpl(
            sourceEventEntity.data.id.ID,
            sourceEventEntity.data.content,
            sourceEventEntity.data.attachments
        )

    override suspend fun content(): QGFriend {
        return sourceEventEntity.data.toFriend(
            bot = bot,
            seq = msgSeq
        )
    }

    override suspend fun reply(text: String): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(text))
    }

    override suspend fun reply(message: Message): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(message))
    }

    override suspend fun reply(messageContent: MessageContent): QGMessageReceipt {
        return emitPreReplyEventAndReply(InteractionMessage.valueOf(messageContent))
    }

    private suspend fun emitPreReplyEventAndReply(message: InteractionMessage): QGMessageReceipt {
        val event = QGC2CMessageCreateEventPreReplyEventImpl(bot, this, message)
        bot.emitMessagePreSendEvent(event)
        return replyByInteractionMessage(event.useMessage())
    }

    private suspend fun replyByInteractionMessage(message: InteractionMessage): QGMessageReceipt {
        fun GroupAndC2CSendBody.configMsgIdAndSeq() {
            if (msgId == null) {
                msgId = sourceEventEntity.data.id
            }
            if (msgSeq == null) {
                msgSeq = this@QGC2CMessageCreateEventImpl.msgSeq.getAndIncrement()
            }
        }

        return when (message) {
            is InteractionMessage.Message -> {
                bot.sendUserMessage(
                    openid = sourceEventEntity.data.author.userOpenid,
                    message = message.message,
                ) {
                    configMsgIdAndSeq()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendUserMessage(
                    openid = sourceEventEntity.data.author.userOpenid,
                    messageContent = message.messageContent,
                ) {
                    configMsgIdAndSeq()
                }
            }

            is InteractionMessage.Text -> {
                bot.sendUserMessage(
                    openid = sourceEventEntity.data.author.userOpenid,
                    text = message.text,
                    msgType = GroupMessageSendApi.MSG_TYPE_TEXT,
                ) {
                    configMsgIdAndSeq()
                }
            }

            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
        }.alsoEmitPostReplyEvent(bot, this, message)
    }
}