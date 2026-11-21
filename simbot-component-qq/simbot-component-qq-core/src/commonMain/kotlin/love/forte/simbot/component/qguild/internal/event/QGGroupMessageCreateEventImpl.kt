/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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
import love.forte.simbot.common.atomic.AtomicInt
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGGroupMessageCreateEvent
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupAuthor
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupAuthorImpl
import love.forte.simbot.component.qguild.internal.group.toGroup
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.component.qguild.internal.message.interceptReply
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.group.GroupMessageSendApi
import love.forte.simbot.qguild.event.GroupMessageCreate


/**
 *
 * @since 4.3.0
 * @author ForteScarlet
 */
internal class QGGroupMessageCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: GroupMessageCreate,
    private val eventId: String?,
    private val msgSeq: AtomicInt = atomic(1),
) : QGGroupMessageCreateEvent() {
    override val id: ID
        get() = eventId?.ID ?: sourceEventEntity.data.id.ID

    override suspend fun author(): QGGroupAuthor {
        return QGGroupAuthorImpl(
            bot,
            sourceEventEntity.data.groupOpenid.ID,
            sourceEventEntity.data.author,
        )
    }

    override val messageContent: QGGroupAndC2CMessageContent = QGGroupAndC2CMessageContentImpl(
        sourceEventEntity.data.id.ID,
        sourceEventEntity.data.content,
        sourceEventEntity.data.attachments
    ) { options ->
        bot.deleteGroupMessage(sourceEventEntity.data.groupOpenid, sourceEventEntity.data.id, *options)
    }

    override suspend fun content(): QGGroup =
        sourceEventEntity.data.toGroup(bot, msgSeq)

    override suspend fun reply(text: String): QGMessageReceipt =
        reply(text, *emptyArray<ReplyOption>())

    override suspend fun reply(message: Message): QGMessageReceipt =
        reply(message, *emptyArray<ReplyOption>())

    override suspend fun reply(messageContent: MessageContent): QGMessageReceipt =
        reply(messageContent, *emptyArray<ReplyOption>())

    /**
     * 在解析回复目标之前执行拦截，避免短路时产生额外请求。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): QGMessageReceipt =
        bot.interceptReply(
            source = this,
            message = InteractionMessage.valueOf(text),
            options = options.toList(),
            target = { content() },
            reply = ::replyByInteractionMessage
        )

    /**
     * 在解析回复目标之前执行拦截，避免短路时产生额外请求。
     */
    override suspend fun reply(
        message: love.forte.simbot.message.Message,
        vararg options: ReplyOption
    ): QGMessageReceipt =
        bot.interceptReply(
            source = this,
            message = InteractionMessage.valueOf(message),
            options = options.toList(),
            target = { content() },
            reply = ::replyByInteractionMessage
        )

    /**
     * 在解析回复目标之前执行拦截，避免短路时产生额外请求。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): QGMessageReceipt =
        bot.interceptReply(
            source = this,
            message = InteractionMessage.valueOf(messageContent),
            options = options.toList(),
            target = { content() },
            reply = ::replyByInteractionMessage
        )

    private suspend fun replyByInteractionMessage(context: QGMessageReplyContext): QGMessageReceipt {
        val message = context.message
        val resolveReference = context.replyReferenceResolver(sourceEventEntity.data.id)
        fun GroupAndC2CSendBody.configRefAndMsgIdAndSeq() {
            messageReference = resolveReference(messageReference)
            if (msgId == null) {
                msgId = sourceEventEntity.data.id
            }
            if (msgSeq == null) {
                msgSeq = this@QGGroupMessageCreateEventImpl.msgSeq.getAndIncrement()
            }
        }

        return when (message) {
            is InteractionMessage.Message -> {
                bot.sendGroupMessageDirect(
                    openid = sourceEventEntity.data.groupOpenid,
                    message = message.message,
                ) {
                    configRefAndMsgIdAndSeq()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendGroupMessageDirect(
                    openid = sourceEventEntity.data.groupOpenid,
                    messageContent = message.messageContent,
                ) {
                    configRefAndMsgIdAndSeq()
                }
            }

            is InteractionMessage.Text -> {
                bot.sendGroupMessageDirect(
                    openid = sourceEventEntity.data.groupOpenid,
                    text = message.text,
                    msgType = GroupMessageSendApi.MSG_TYPE_TEXT,
                ) {
                    configRefAndMsgIdAndSeq()
                }
            }

            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
        }
    }
}
