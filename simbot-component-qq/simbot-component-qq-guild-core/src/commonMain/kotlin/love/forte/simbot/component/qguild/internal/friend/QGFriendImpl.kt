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

package love.forte.simbot.component.qguild.internal.friend

import love.forte.simbot.common.atomic.AtomicInt
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.event.QGFriendSendSupportPreSendEventImpl
import love.forte.simbot.component.qguild.message.QGMedia
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendUserMessage
import love.forte.simbot.component.qguild.utils.alsoEmitPostSendEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.resource.Resource
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGFriendImpl(
    private val bot: QGBotImpl,
    override val id: ID,
    private val eventId: String? = null,
    private val sourceEvent: C2CMessageCreate.Data? = null,
    private val msgSeq: AtomicInt? = null,
) : QGFriend {
    override val coroutineContext: CoroutineContext =
        bot.newSupervisorCoroutineContext()

    override suspend fun uploadMedia(url: String, type: Int): QGMedia {
        return bot.uploadUserMedia(id, url, type)
    }

    @ExperimentalQGMediaApi
    override suspend fun uploadMedia(resource: Resource, type: Int): QGMedia {
        return bot.uploadUserMedia(id, resource, type)
    }

    private fun GroupAndC2CSendBody.initMsgIdAndSeq() {
        if (msgId == null) {
            if (sourceEvent != null) {
                msgId = sourceEvent.id
            } else if (eventId == null && this@QGFriendImpl.eventId != null) {
                eventId = this@QGFriendImpl.eventId
            }
        }

        if (msgSeq == null && this@QGFriendImpl.msgSeq != null) {
            msgSeq = this@QGFriendImpl.msgSeq.getAndIncrement()
        }
    }

    /**
     * 发送消息，然后emit post send事件并返回 receipt.
     */
    private suspend fun sendByInteractionMessage(message: InteractionMessage): QGMessageReceipt {
        return when (message) {
            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
            is InteractionMessage.Text -> {
                bot.sendUserMessage(
                    openid = id.literal,
                    text = message.text,
                    msgType = GroupAndC2CSendBody.MSG_TYPE_TEXT,
                ) {
                    initMsgIdAndSeq()
                }
            }
            is InteractionMessage.Message -> {
                bot.sendUserMessage(
                    openid = id.literal,
                    message = message.message,
                ) {
                    initMsgIdAndSeq()
                }
            }
            is InteractionMessage.MessageContent -> {
                bot.sendUserMessage(
                    openid = id.literal,
                    messageContent = message.messageContent,
                ) {
                    initMsgIdAndSeq()
                }
            }
        }.alsoEmitPostSendEvent(bot, this, message)
    }

    override suspend fun send(text: String): MessageReceipt {
        val event = QGFriendSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = InteractionMessage.valueOf(text),
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }

    override suspend fun send(message: Message): MessageReceipt {
        val event = QGFriendSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = InteractionMessage.valueOf(message),
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }

    override suspend fun send(messageContent: MessageContent): MessageReceipt {
        val event = QGFriendSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = InteractionMessage.valueOf(messageContent),
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }
}

internal fun C2CMessageCreate.Data.toFriend(
    bot: QGBotImpl,
    seq: AtomicInt? = null,
): QGFriendImpl =
    QGFriendImpl(
        bot = bot,
        id = author.userOpenid.ID,
        eventId = null,
        sourceEvent = this,
        msgSeq = seq
    )

internal fun idFriend(
    bot: QGBotImpl,
    id: ID,
    eventId: String? = null,
    seq: AtomicInt? = null,
): QGFriendImpl =
    QGFriendImpl(
        bot = bot,
        id = id,
        eventId = eventId,
        sourceEvent = null,
        msgSeq = seq
    )
