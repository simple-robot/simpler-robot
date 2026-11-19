/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.castChannel
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.internal.message.QGMessageContentImpl
import love.forte.simbot.component.qguild.internal.message.interceptReply
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.QGMessageReplyContext
import love.forte.simbot.component.qguild.message.qgMessageReference
import love.forte.simbot.component.qguild.message.sendMessageDirect
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

    override suspend fun reply(message: love.forte.simbot.message.Message): QGMessageReceipt =
        reply(message, *emptyArray<ReplyOption>())

    override suspend fun reply(messageContent: MessageContent): QGMessageReceipt =
        reply(messageContent, *emptyArray<ReplyOption>())

    override suspend fun reply(text: String): QGMessageReceipt =
        reply(text, *emptyArray<ReplyOption>())

    /**
     * 在解析回复目标之前执行拦截，避免短路时产生额外请求。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): QGMessageReceipt =
        bot.interceptReply(
            this,
            InteractionMessage.valueOf(text),
            options.toList(),
            { content() },
            ::replyByInteractionMessage
        )

    /**
     * 在解析回复目标之前执行拦截，避免短路时产生额外请求。
     */
    override suspend fun reply(
        message: love.forte.simbot.message.Message,
        vararg options: ReplyOption
    ): QGMessageReceipt =
        bot.interceptReply(
            this,
            InteractionMessage.valueOf(message),
            options.toList(),
            { content() },
            ::replyByInteractionMessage
        )

    /**
     * 在解析回复目标之前执行拦截，避免短路时产生额外请求。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): QGMessageReceipt =
        bot.interceptReply(
            this,
            InteractionMessage.valueOf(messageContent),
            options.toList(),
            { content() },
            ::replyByInteractionMessage
        )

    private suspend fun replyByInteractionMessage(context: QGMessageReplyContext): QGMessageReceipt {
        val message = context.message
        // 仅在构建请求时使用原消息作为默认引用，使拦截上下文只保留显式选项。
        val reference = Message.Reference(context.options.qgMessageReference()?.literal ?: sourceEventEntity.id)

        fun MessageSendApi.Body.Builder.configMsgIdAndRef() {
            if (msgId == null) {
                msgId = sourceEventEntity.id
            }
            if (messageReference == null) {
                messageReference = reference
            }
        }

        return when (message) {
            is InteractionMessage.Message -> {
                bot.sendMessageDirect(sourceEventEntity.channelId, message.message) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendMessageDirect(sourceEventEntity.channelId, message.messageContent) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.Text -> {
                bot.sendMessageDirect(sourceEventEntity.channelId, message.text) {
                    configMsgIdAndRef()
                }
            }

            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
        }
    }

    override suspend fun source(): QGGuild {
        return with(sourceEventEntity) {
            bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")
        }
    }

    override suspend fun author(): QGMemberImpl {
        return with(sourceEventEntity) {
            val authorId = author.id
            bot.queryMember(guildId = guildId, userId = authorId)
                ?: throw NoSuchElementException("member(id=$authorId)")
        }
    }

    override suspend fun content(): QGTextChannel {
        return with(sourceEventEntity) {
            bot.queryChannel(id = channelId, currentMsgId = id)
                ?.castChannel<QGTextChannel> { ChannelType.TEXT }
                ?: throw NoSuchElementException("channel(id=$channelId)")
        }
    }
}
