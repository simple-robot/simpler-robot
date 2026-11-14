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

package love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
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
 * 在消息解析和平台请求之前执行一次应用级拦截，保留回复来源。
 */
internal abstract class AbstractReplySupportInteractionEvent : ReplySupport {
    /**
     * 提供当前操作所属的应用级拦截器容器。
     */
    protected abstract val bot: OneBotBotImpl

    /**
     * 仅在拦截器请求目标时获取会话信息。
     */
    protected abstract suspend fun replyTarget(): Any?

    /**
     * 执行平台回复，避免再次进入拦截。
     */
    protected abstract suspend fun replyText(text: String): SendMsgResult

    /**
     * 执行平台回复，避免再次进入拦截。
     */
    protected abstract suspend fun replySegments(segments: List<OneBotMessageSegment>): SendMsgResult

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(text: String): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(text))

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(message: Message): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(message))

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(messageContent: MessageContent): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(messageContent))

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(text), options.toList())

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(message: Message, vararg options: ReplyOption): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(message), options.toList())

    /**
     * 通过拦截点执行本次回复。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): OneBotMessageReceipt =
        interceptionAndReply(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 拦截并发送消息。
     * 仅在完整拦截链结束后校验入口要求的回执类型。
     */
    private suspend fun interceptionAndReply(
        message: InteractionMessage,
        options: List<ReplyOption> = emptyList(),
    ): OneBotMessageReceipt = MessageSendInterceptionPoint.invokeWithReceipt<OneBotMessageReceipt>(
        bot.interceptorContainer,
        MessageSendContext.DefaultReply(bot, this, message, options, target = { replyTarget() }),
    ) { context ->
        // OneBot 暂无专属 ReplyOption；未知选项按 ReplyOption 契约忽略。
        val segments = context.message.segmentsOrNull
        val result = if (segments != null) {
            replySegments(segments)
        } else {
            resolveInteractionMessage(
                interactionMessage = context.message,
                onSegments = { replySegments(it) },
                onMessage = {
                    if (it is PlainText) {
                        replyText(it.text)
                    } else {
                        replySegments(it.resolveToOneBotSegmentList(bot))
                    }
                },
                onText = { replyText(it) },
            )
        }
        result.toReceipt(bot).also { bot.notifyMessageSent(context, it) }
    }
}
