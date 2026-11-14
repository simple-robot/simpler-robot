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
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
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
 * QQ 当前没有组件专属发送选项，未知选项由终端忽略。
 */
internal suspend inline fun QGBotImpl.interceptSend(
    source: SendSupport,
    message: InteractionMessage,
    options: List<SendOption>,
    crossinline send: suspend (InteractionMessage) -> QGMessageReceipt,
): QGMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<QGMessageReceipt>(
    interceptorContainer,
    MessageSendContext.DefaultSend(this, source, message, options),
) { context ->
    send(context.message).also { receipt ->
        pushEventAndCollectAsync(QGMessageSentEventImpl(context, receipt))
    }
}

/**
 * 保留回复来源、延迟解析目标，并直接调用终端以避免再次进入 send 链。
 * QQ 当前没有组件专属回复选项，未知选项由终端忽略。
 */
internal suspend inline fun QGBotImpl.interceptReply(
    source: ReplySupport,
    message: InteractionMessage,
    options: List<ReplyOption>,
    noinline target: suspend () -> Any?,
    crossinline reply: suspend (InteractionMessage) -> QGMessageReceipt,
): QGMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<QGMessageReceipt>(
    interceptorContainer,
    MessageSendContext.DefaultReply(this, source, message, options, target),
) { context ->
    reply(context.message).also { receipt ->
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
        QGMessageSendContext(bot, message, target),
    ) { context ->
        send(context.message).also { receipt ->
            bot.pushEventAndCollectAsync(QGMessageSentEventImpl(context, receipt))
        }
    }
}
