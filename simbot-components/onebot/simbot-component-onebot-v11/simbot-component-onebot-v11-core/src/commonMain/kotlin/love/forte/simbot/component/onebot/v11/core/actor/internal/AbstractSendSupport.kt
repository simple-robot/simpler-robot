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

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.AbstractMessagePreSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotInternalMessagePostSendEvent
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
internal abstract class AbstractSendSupport : SendSupport {
    protected abstract val bot: OneBotBotImpl

    protected abstract fun preSendEvent(
        interactionMessage: OneBotSegmentsInteractionMessage
    ): AbstractMessagePreSendEventImpl

    protected abstract fun OneBotMessageReceipt.postSendEvent(
        interactionMessage: InteractionMessage
    ): OneBotInternalMessagePostSendEvent

    protected abstract suspend fun sendText(text: String): SendMsgResult

    protected abstract suspend fun sendSegments(segments: List<OneBotMessageSegment>): SendMsgResult

    protected suspend fun sendMessage(message: Message): SendMsgResult {
        return when (message) {
            is PlainText -> sendText(message.text)
            else -> sendSegments(message.resolveToOneBotSegmentList(bot))
        }
    }

    override suspend fun send(text: String): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage(text = text)
        return interceptionAndSend(interactionMessage)
    }

    override suspend fun send(messageContent: MessageContent): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage(content = messageContent)
        return interceptionAndSend(interactionMessage)
    }

    override suspend fun send(message: Message): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage(message = message, bot = bot)
        return interceptionAndSend(interactionMessage)
    }

    protected suspend fun interceptionAndSend(
        interactionMessage: OneBotSegmentsInteractionMessage
    ): OneBotMessageReceipt {
        val event = preSendEvent(interactionMessage)

        val currentMessage = bot.emitMessagePreSendEventAndUseCurrentMessage(event)
        val segments = currentMessage.segmentsOrNull
        if (segments != null) {
            return sendSegments(segments).toReceipt(bot).alsoPostSend(currentMessage)
        }

        return sendByInteractionMessage(currentMessage).toReceipt(bot).alsoPostSend(currentMessage)
    }

    /**
     * 解析一个 [InteractionMessage] 为一个 [OneBotMessageSegment] 的列表并发送。
     * 始终认为 `segments` 为 `null`。
     */
    protected suspend fun sendByInteractionMessage(interactionMessage: InteractionMessage): SendMsgResult {
        return resolveInteractionMessage(
            interactionMessage = interactionMessage,
            onSegments = { sendSegments(it) },
            onMessage = { sendMessage(it) },
            onText = { sendText(it) },
        )
    }

    protected fun OneBotMessageReceipt.alsoPostSend(
        interactionMessage: InteractionMessage
    ): OneBotMessageReceipt = apply {
        val event = postSendEvent(interactionMessage)
        bot.pushEventAndLaunch(event)
    }
}
