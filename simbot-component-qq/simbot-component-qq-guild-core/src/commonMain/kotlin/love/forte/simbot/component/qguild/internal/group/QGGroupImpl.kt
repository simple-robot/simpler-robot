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

package love.forte.simbot.component.qguild.internal.group

import love.forte.simbot.common.atomic.AtomicInt
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.event.QGGroupSendSupportPreSendEventImpl
import love.forte.simbot.component.qguild.message.QGMedia
import love.forte.simbot.component.qguild.message.sendGroupMessage
import love.forte.simbot.component.qguild.utils.alsoEmitPostSendEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.event.GroupAtMessageCreate
import love.forte.simbot.resource.Resource
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGGroupImpl(
    private val bot: QGBotImpl,
    override val id: ID,
    private val sourceEvent: GroupAtMessageCreate.Data?,
    private val msgSeq: AtomicInt?,
    private val isFake: Boolean,
) : QGGroup {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    override suspend fun botAsMember(): QGGroupMember =
        QGBotMemberImpl(bot)

    private fun GroupAndC2CSendBody.initMsgIdAndSeq() {
        if (msgId == null && sourceEvent != null) {
            msgId = sourceEvent.id
        }
        if (msgSeq == null && this@QGGroupImpl.msgSeq != null) {
            msgSeq = this@QGGroupImpl.msgSeq.getAndIncrement()
        }
    }

    override suspend fun send(text: String): MessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(text))
    }

    override suspend fun send(message: Message): MessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(message))
    }

    override suspend fun send(messageContent: MessageContent): MessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(messageContent))
    }

    private suspend fun emitPreSendEventAndSend(message: InteractionMessage): MessageReceipt {
        val event = QGGroupSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = message
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()
        return sendByInteractionMessage(message)
    }

    private suspend fun sendByInteractionMessage(message: InteractionMessage): MessageReceipt {
        return when (message) {
            is InteractionMessage.Text -> {
                bot.sendGroupMessage(
                    openid = id.literal,
                    text = message.text,
                    msgType = GroupMessageSendApi.MSG_TYPE_TEXT
                ) {
                    initMsgIdAndSeq()
                }
            }

            is InteractionMessage.Message -> {
                bot.sendGroupMessage(
                    openid = id.literal,
                    message = message.message,
                ) {
                    initMsgIdAndSeq()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendGroupMessage(
                    openid = id.literal,
                    messageContent = message.messageContent,
                ) {
                    initMsgIdAndSeq()
                }
            }

            else -> {
                error("Unknown type of InteractionMessage: $message")
            }
        }.alsoEmitPostSendEvent(bot, this, message)
    }

    override suspend fun uploadMedia(url: String, type: Int): QGMedia {
        return bot.uploadGroupMedia(id, url, type)
    }

    @ExperimentalQGMediaApi
    override suspend fun uploadMedia(resource: Resource, type: Int): QGMedia {
        return bot.uploadGroupMedia(id, resource, type)
    }

    override fun toString(): String {
        return "QGGroup(id=$id, isFake=$isFake)"
    }
}

internal fun GroupAtMessageCreate.Data.toGroup(bot: QGBotImpl, msgSeq: AtomicInt? = null): QGGroupImpl =
    QGGroupImpl(bot, groupOpenid.ID, this, msgSeq, false)

internal fun idGroup(bot: QGBotImpl, id: ID, msgSeq: AtomicInt? = null, isFake: Boolean = true): QGGroupImpl =
    QGGroupImpl(bot, id, null, msgSeq, isFake)
