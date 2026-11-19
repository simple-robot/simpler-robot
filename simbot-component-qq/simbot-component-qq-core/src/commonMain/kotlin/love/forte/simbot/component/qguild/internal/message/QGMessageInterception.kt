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

package love.forte.simbot.component.qguild.internal.message

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.dms.QGDmsContactImpl
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.QGMessageReplyContext
import love.forte.simbot.component.qguild.message.QGMessageSendContext
import love.forte.simbot.component.qguild.message.QGMessageSendTarget
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.event.MessageSentEvent
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.MessageSendInterceptionPoint
import love.forte.simbot.interception.invokeWithReceipt
import love.forte.simbot.message.MessageReceipt

/**
 * 在消息解析和网络请求之前执行标准发送拦截链。
 * 未知发送选项由终端忽略。
 */
internal suspend inline fun QGBotImpl.interceptSend(
    source: SendSupport,
    message: InteractionMessage,
    options: List<SendOption>,
    crossinline send: suspend (QGMessageSendContext) -> QGMessageReceipt,
): QGMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<QGMessageReceipt>(
    interceptorContainer,
    QGMessageSendContext.create(this, source, message, source.sendDestination(), options, target = { source }),
) { context ->
    send(context as QGMessageSendContext).also { receipt ->
        pushEventAndCollectAsync(QGMessageSentEventImpl(context, receipt))
    }
}

/**
 * 保留回复来源、延迟解析目标，并直接调用终端以避免再次进入 send 链。
 * 上下文保留调用方的选项，回复终端根据最终选项和原消息确定引用。
 */
internal suspend inline fun QGBotImpl.interceptReply(
    source: ReplySupport,
    message: InteractionMessage,
    options: List<ReplyOption>,
    noinline target: suspend () -> Any?,
    crossinline reply: suspend (QGMessageReplyContext) -> QGMessageReceipt,
): QGMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<QGMessageReceipt>(
    interceptorContainer,
    QGMessageReplyContext.create(
        this,
        source,
        message,
        source.replyDestination(),
        options,
        target,
    ),
) { context ->
    reply(context as QGMessageReplyContext).also { receipt ->
        pushEventAndCollectAsync(QGMessageSentEventImpl(context, receipt))
    }
}

/**
 * 在真实发送成功时的最终上下文。
 */
internal class QGMessageSentEventImpl(
    override val context: MessageSendContext,
    override val receipt: MessageReceipt,
) : MessageSentEvent {
    override val id: ID = UUID.random()
    override val time: Timestamp = Timestamp.now()
}

/**
 * 为 Bot 的按 ID 发送入口执行一次拦截，延迟解析消息及目标。
 * QQ 暂无组件专属发送选项，未知选项忽略。
 */
@PublishedApi
internal suspend fun QGBot.interceptDirectSend(
    target: QGMessageSendTarget,
    message: InteractionMessage,
    send: suspend (InteractionMessage) -> QGMessageReceipt,
): QGMessageReceipt {
    val bot = this as? QGBotImpl ?: return send(message)
    return MessageSendInterceptionPoint.invokeWithReceipt<QGMessageReceipt>(
        bot.interceptorContainer,
        QGMessageSendContext.create(bot, bot, message, target),
    ) { context ->
        send(context.message).also { receipt ->
            bot.pushEventAndCollectAsync(QGMessageSentEventImpl(context, receipt))
        }
    }
}

private fun SendSupport.sendDestination(): QGMessageSendTarget = when (this) {
    is QGTextChannel -> QGMessageSendTarget.channel(id)
    is QGDmsContactImpl -> sendDestination
    is QGMemberImpl -> sendDestination
    is QGFriend -> QGMessageSendTarget.user(id)
    is QGGroup -> QGMessageSendTarget.group(id)
    else -> error("Unsupported QQ send source: $this")
}

private fun ReplySupport.replyDestination(): QGMessageSendTarget = when (this) {
    is QGAtMessageCreateEvent -> QGMessageSendTarget.channel(sourceEventEntity.channelId.ID)
    is QGDirectMessageCreateEvent -> QGMessageSendTarget.dms(sourceEventEntity.guildId.ID)
    is QGGroupAtMessageCreateEvent -> QGMessageSendTarget.group(sourceEventEntity.data.groupOpenid.ID
    )
    is QGGroupMessageCreateEvent -> QGMessageSendTarget.group(sourceEventEntity.data.groupOpenid.ID
    )
    is QGC2CMessageCreateEvent -> QGMessageSendTarget.user(sourceEventEntity.data.author.userOpenid.ID
    )
    else -> error("Unsupported QQ reply source: $this")
}
