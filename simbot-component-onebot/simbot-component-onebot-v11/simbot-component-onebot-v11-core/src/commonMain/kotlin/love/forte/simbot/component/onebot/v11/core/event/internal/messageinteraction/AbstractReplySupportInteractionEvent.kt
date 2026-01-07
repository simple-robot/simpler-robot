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

import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotMessageEventPostReplyEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.segmentsOrNull
import love.forte.simbot.component.onebot.v11.core.internal.message.toReceipt
import love.forte.simbot.component.onebot.v11.core.utils.emitMessagePreSendEventAndUseCurrentMessage
import love.forte.simbot.component.onebot.v11.core.utils.resolveInteractionMessage
import love.forte.simbot.component.onebot.v11.core.utils.resolveToOneBotSegmentList
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.PlainText


/**
 *
 * @author ForteScarlet
 */
internal abstract class AbstractReplySupportInteractionEvent : ReplySupport {
    protected abstract val bot: OneBotBotImpl

    protected abstract fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage):
        AbstractMessagePreSendEventImpl

    protected abstract fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotMessageEventPostReplyEvent

    override suspend fun reply(text: String): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage(text = text)
        return interceptionAndReply(interactionMessage)
    }

    override suspend fun reply(messageContent: MessageContent): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage(content = messageContent)
        return interceptionAndReply(interactionMessage)
    }

    override suspend fun reply(message: Message): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage(message = message, bot = bot)
        return interceptionAndReply(interactionMessage)
    }

    protected abstract suspend fun replyText(text: String): SendMsgResult

    protected abstract suspend fun replySegments(segments: List<OneBotMessageSegment>): SendMsgResult

    protected suspend fun replyMessage(message: Message): SendMsgResult {
        return when (message) {
            is PlainText -> replyText(message.text)
            else -> replySegments(message.resolveToOneBotSegmentList(bot))
        }
    }

    protected suspend fun interceptionAndReply(
        interactionMessage: OneBotSegmentsInteractionMessage
    ): OneBotMessageReceipt {
        val event = preReplyEvent(interactionMessage)

        val currentMessage = bot.emitMessagePreSendEventAndUseCurrentMessage(event)
        val segments = currentMessage.segmentsOrNull
        if (segments != null) {
            return replySegments(segments).toReceipt(bot).alsoPostReply(currentMessage)
        }

        return replyByInteractionMessage(currentMessage).toReceipt(bot).alsoPostReply(currentMessage)
    }

    /**
     * 解析一个 [InteractionMessage] 为一个 [OneBotMessageSegment] 的列表并发送。
     * 始终认为 `segments` 为 `null`。
     */
    protected suspend fun replyByInteractionMessage(interactionMessage: InteractionMessage): SendMsgResult {
        return resolveInteractionMessage(
            interactionMessage = interactionMessage,
            onSegments = { replySegments(it) },
            onMessage = { replyMessage(it) },
            onText = { replyText(it) },
        )
    }

    protected fun OneBotMessageReceipt.alsoPostReply(
        interactionMessage: InteractionMessage
    ): OneBotMessageReceipt = apply {
        val event = postReplyEvent(interactionMessage)
        bot.pushEventAndLaunch(event)
    }
}
