/*
 * Copyright (c) 2022-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.channel

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.channel.QGCategory
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.event.QGTextChannelSendSupportPreSendEventImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.component.qguild.utils.alsoEmitPostSendEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Channel
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

/**
 *
 * @author ForteScarlet
 */
internal class QGTextChannelImpl internal constructor(
    private val bot: QGBotImpl,
    override val source: QGSourceChannel,
    sourceGuild: QGGuild? = null,
    category: QGCategory? = null,
    /**
     * 如果是从一个事件而来，提供可用于消息回复的 msgId 来避免 event.channel().send(...) 出现问题
     */
    private val currentMsgId: String? = null,
) : QGTextChannel {
    override val category: QGCategory = category ?: QGCategoryImpl(
        bot = bot,
        guildId = source.guildId.ID,
        id = source.parentId.ID,
        sourceGuild = sourceGuild,
    )

    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val id: ID = source.id.ID

    override suspend fun send(message: Message): QGMessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(message))
    }

    override suspend fun send(messageContent: MessageContent): QGMessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(messageContent))
    }

    override suspend fun send(text: String): QGMessageReceipt {
        return emitPreSendEventAndSend(InteractionMessage.valueOf(text))
    }

    private suspend fun emitPreSendEventAndSend(
        message: InteractionMessage,
    ): QGMessageReceipt {
        val event = QGTextChannelSendSupportPreSendEventImpl(
            bot = bot,
            content = this,
            message = message
        )
        bot.emitMessagePreSendEvent(event)
        val message = event.useMessage()

        return sendByInteractionMessage(message)
    }

    private suspend fun sendByInteractionMessage(message: InteractionMessage): QGMessageReceipt {
        fun MessageSendApi.Body.Builder.configMsgId() {
            if (msgId == null) {
                msgId = currentMsgId
            }
        }

        try {
            return when (message) {
                is InteractionMessage.Message -> {
                    bot.sendMessage(source.id, message.message) {
                        configMsgId()
                    }
                }

                is InteractionMessage.MessageContent -> {
                    bot.sendMessage(source.id, message.messageContent) {
                        configMsgId()
                    }
                }

                is InteractionMessage.Text -> {
                    bot.sendMessage(source.id, message.text) {
                        configMsgId()
                    }
                }

                is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message.")
            }.alsoEmitPostSendEvent(bot, this, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "textChannel.send" }
        }
    }

    override fun toString(): String {
        return "QGTextChannel(id=$id, name=$name, category=$category)"
    }
}

internal fun Channel.toTextChannel(
    bot: QGBotImpl,
    sourceGuild: QGGuild? = null,
    category: QGCategory? = null,
    currentMsgId: String? = null,
): QGTextChannelImpl = QGTextChannelImpl(
    bot = bot,
    source = this,
    sourceGuild = sourceGuild,
    category = category,
    currentMsgId = currentMsgId
)
