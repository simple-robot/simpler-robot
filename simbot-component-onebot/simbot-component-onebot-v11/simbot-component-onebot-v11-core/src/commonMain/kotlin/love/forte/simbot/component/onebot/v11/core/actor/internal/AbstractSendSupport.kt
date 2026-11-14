/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.actor.internal

import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.notifyMessageSent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.segmentsOrNull
import love.forte.simbot.component.onebot.v11.core.internal.message.toReceipt
import love.forte.simbot.component.onebot.v11.core.utils.resolveInteractionMessage
import love.forte.simbot.component.onebot.v11.core.utils.resolveToOneBotSegmentList
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.interception.MessageSendInterceptionPoint
import love.forte.simbot.interception.invokeWithReceipt
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.PlainText

/**
 * 在消息解析和平台请求之前执行一次应用级拦截，保留发送来源。
 */
internal abstract class AbstractSendSupport : SendSupport {
    /**
     * 提供当前操作所属的应用级拦截器容器。
     */
    protected abstract val bot: OneBotBotImpl

    /**
     * 执行平台发送，避免再次进入拦截。
     */
    protected abstract suspend fun sendText(text: String): SendMsgResult

    /**
     * 执行平台发送，避免再次进入拦截。
     */
    protected abstract suspend fun sendSegments(segments: List<OneBotMessageSegment>): SendMsgResult

    /**
     * 通过拦截执行本次发送。
     */
    override suspend fun send(text: String): OneBotMessageReceipt =
        interceptionAndSend(InteractionMessage.valueOf(text))

    /**
     * 通过拦截执行本次发送。
     */
    override suspend fun send(message: Message): OneBotMessageReceipt =
        interceptionAndSend(InteractionMessage.valueOf(message))

    /**
     * 通过拦截执行本次发送。
     */
    override suspend fun send(messageContent: MessageContent): OneBotMessageReceipt =
        interceptionAndSend(InteractionMessage.valueOf(messageContent))

    /**
     * 通过拦截执行本次发送。
     */
    override suspend fun send(text: String, vararg options: SendOption): OneBotMessageReceipt =
        interceptionAndSend(InteractionMessage.valueOf(text), options.toList())

    /**
     * 通过拦截执行本次发送。
     */
    override suspend fun send(message: Message, vararg options: SendOption): OneBotMessageReceipt =
        interceptionAndSend(InteractionMessage.valueOf(message), options.toList())

    /**
     * 通过拦截执行本次发送。
     */
    override suspend fun send(messageContent: MessageContent, vararg options: SendOption): OneBotMessageReceipt =
        interceptionAndSend(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 拦截并发送消息。
     * 仅在完整拦截链结束后校验入口要求的回执类型。
     */
    private suspend fun interceptionAndSend(
        message: InteractionMessage,
        options: List<SendOption> = emptyList(),
    ): OneBotMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<OneBotMessageReceipt>(
        bot.interceptorContainer,
        MessageSendContext.DefaultSend(bot, this, message, options),
    ) { context ->
        // OneBot 暂无专属 SendOption；未知选项按 SendOption 契约忽略。
        val segments = context.message.segmentsOrNull
        val result = if (segments != null) {
            sendSegments(segments)
        } else {
            resolveInteractionMessage(
                interactionMessage = context.message,
                onSegments = { sendSegments(it) },
                onMessage = {
                    if (it is PlainText) {
                        sendText(it.text)
                    } else {
                        sendSegments(it.resolveToOneBotSegmentList(bot))
                    }
                },
                onText = { sendText(it) },
            )
        }
        result.toReceipt(bot).also { bot.notifyMessageSent(context, it) }
    }
}
