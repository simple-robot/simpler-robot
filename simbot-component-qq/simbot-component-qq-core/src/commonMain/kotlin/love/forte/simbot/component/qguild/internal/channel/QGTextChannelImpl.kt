/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.channel

import love.forte.simbot.ability.SendOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.channel.QGCategory
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.message.interceptSend
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.QGMessageSendContext
import love.forte.simbot.component.qguild.message.sendMessageDirect
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

    override suspend fun send(message: Message): QGMessageReceipt =
        send(message, *emptyArray<SendOption>())

    override suspend fun send(messageContent: MessageContent): QGMessageReceipt =
        send(messageContent, *emptyArray<SendOption>())

    override suspend fun send(text: String): QGMessageReceipt =
        send(text, *emptyArray<SendOption>())

    /**
     * 发送消息并让本次操作只进入一次拦截链。
     */
    override suspend fun send(text: String, vararg options: SendOption): QGMessageReceipt =
        bot.interceptSend(
            this,
            InteractionMessage.valueOf(text),
            options.toList(),
            ::sendByInteractionMessage
        )

    /**
     * 发送消息并让本次操作只进入一次拦截链。
     */
    override suspend fun send(message: Message, vararg options: SendOption): QGMessageReceipt =
        bot.interceptSend(
            this,
            InteractionMessage.valueOf(message),
            options.toList(),
            ::sendByInteractionMessage
        )

    /**
     * 保留发送选项，并让本次操作只进入一次拦截链。
     */
    override suspend fun send(messageContent: MessageContent, vararg options: SendOption): QGMessageReceipt =
        bot.interceptSend(
            this,
            InteractionMessage.valueOf(messageContent),
            options.toList(),
            ::sendByInteractionMessage
        )

    private suspend fun sendByInteractionMessage(context: QGMessageSendContext): QGMessageReceipt {
        val message = context.message
        fun MessageSendApi.Body.Builder.configMsgId() {
            if (msgId == null) {
                msgId = currentMsgId
            }
        }

        try {
            return when (message) {
                is InteractionMessage.Message -> {
                    bot.sendMessageDirect(source.id, message.message) {
                        configMsgId()
                    }
                }

                is InteractionMessage.MessageContent -> {
                    bot.sendMessageDirect(source.id, message.messageContent) {
                        configMsgId()
                    }
                }

                is InteractionMessage.Text -> {
                    bot.sendMessageDirect(source.id, message.text) {
                        configMsgId()
                    }
                }

                is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message.")
            }
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
