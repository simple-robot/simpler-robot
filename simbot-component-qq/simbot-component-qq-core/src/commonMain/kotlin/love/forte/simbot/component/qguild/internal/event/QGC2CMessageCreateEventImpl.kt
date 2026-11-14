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
import love.forte.simbot.component.qguild.event.QGC2CMessageCreateEvent
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.friend.toFriend
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.component.qguild.internal.message.interceptReply
import love.forte.simbot.component.qguild.message.QGGroupAndC2CMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendUserMessageDirect
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
                bot.sendUserMessageDirect(
                    openid = sourceEventEntity.data.author.userOpenid,
                    message = message.message,
                ) {
                    configMsgIdAndSeq()
                }
            }

            is InteractionMessage.MessageContent -> {
                bot.sendUserMessageDirect(
                    openid = sourceEventEntity.data.author.userOpenid,
                    messageContent = message.messageContent,
                ) {
                    configMsgIdAndSeq()
                }
            }

            is InteractionMessage.Text -> {
                bot.sendUserMessageDirect(
                    openid = sourceEventEntity.data.author.userOpenid,
                    text = message.text,
                    msgType = GroupMessageSendApi.MSG_TYPE_TEXT,
                ) {
                    configMsgIdAndSeq()
                }
            }

            is InteractionMessage.Extension -> error("Unknown type of InteractionMessage: $message")
        }
    }
}
