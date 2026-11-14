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
import love.forte.simbot.component.qguild.message.*
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.event.MessageSentEvent
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.MessageSendInterceptionPoint
import love.forte.simbot.interception.invokeWithReceipt
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.stdlib.MessageDestination

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
 * 回复引用通过上下文选项传递。
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
        source.initialReplyOptions(options),
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
    is QGTextChannel -> QGMessageSendTarget.create(MessageDestination.CHANNEL, id)
    is QGDmsContactImpl -> sendDestination
    is QGMemberImpl -> sendDestination
    is QGFriend -> QGMessageSendTarget.create(MessageDestination.USER, id)
    is QGGroup -> QGMessageSendTarget.create(MessageDestination.GROUP, id)
    else -> error("Unsupported QQ send source: $this")
}

private fun ReplySupport.replyDestination(): QGMessageSendTarget = when (this) {
    is QGAtMessageCreateEvent -> QGMessageSendTarget.create(MessageDestination.CHANNEL, sourceEventEntity.channelId.ID)
    is QGDirectMessageCreateEvent -> QGMessageSendTarget.create(MessageDestination.DMS, sourceEventEntity.guildId.ID)
    is QGGroupAtMessageCreateEvent -> QGMessageSendTarget.create(
        MessageDestination.GROUP,
        sourceEventEntity.data.groupOpenid.ID
    )
    is QGGroupMessageCreateEvent -> QGMessageSendTarget.create(
        MessageDestination.GROUP,
        sourceEventEntity.data.groupOpenid.ID
    )
    is QGC2CMessageCreateEvent -> QGMessageSendTarget.create(
        MessageDestination.USER,
        sourceEventEntity.data.author.userOpenid.ID
    )
    else -> error("Unsupported QQ reply source: $this")
}

private fun ReplySupport.initialReplyOptions(options: List<ReplyOption>): List<ReplyOption> {
    if (options.any { it is QGMessageReferenceOption }) return options
    val id = when (this) {
        is QGAtMessageCreateEvent -> sourceEventEntity.id
        is QGDirectMessageCreateEvent -> sourceEventEntity.id
        is QGGroupAtMessageCreateEvent -> sourceEventEntity.data.id
        is QGGroupMessageCreateEvent -> sourceEventEntity.data.id
        else -> return options
    }
    return listOf(QGMessageReferenceOption.create(id.ID)) + options
}
