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

package love.forte.simbot.component.onebot.v11.core.utils

import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing
import love.forte.simbot.component.onebot.v11.core.api.SendMsgApi
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.AbstractMessagePreSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.message.OneBotMessageContent
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotReply
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import org.jetbrains.annotations.ApiStatus

/**
 * 构建一个用于发送的纯文本消息。
 *
 * @param messageType see [SendMsgApi]`.MESSAGE_TYPE_*`
 */
@InternalOneBotAPI
@ApiStatus.Internal
public fun sendTextMsgApi(
    messageType: String,
    target: ID,
    text: String,
    reply: ID? = null,
): SendMsgApi {
    return if (reply == null) {
        SendMsgApi.create(
            messageType = messageType,
            userId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_PRIVATE },
            groupId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_GROUP },
            message = OneBotMessageOutgoing.create(text),
            autoEscape = true
        )
    } else {
        SendMsgApi.create(
            messageType = messageType,
            userId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_PRIVATE },
            groupId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_GROUP },
            message = OneBotMessageOutgoing.create(
                listOf(
                    OneBotReply.create(reply),
                    OneBotText.create(text),
                )
            ),
        )
    }
}

@InternalOneBotAPI
@ApiStatus.Internal
public fun sendPrivateTextMsgApi(
    target: ID,
    text: String,
    reply: ID? = null,
): SendMsgApi = sendTextMsgApi(
    messageType = SendMsgApi.MESSAGE_TYPE_PRIVATE,
    target = target,
    text = text,
    reply = reply,
)

@InternalOneBotAPI
@ApiStatus.Internal
public fun sendGroupTextMsgApi(
    target: ID,
    text: String,
    reply: ID? = null,
): SendMsgApi = sendTextMsgApi(
    messageType = SendMsgApi.MESSAGE_TYPE_GROUP,
    target = target,
    text = text,
    reply = reply,
)


/**
 * 构建一个发送 segments 的纯文本消息。
 *
 * @param messageType see [SendMsgApi]`.MESSAGE_TYPE_*`
 */
@InternalOneBotAPI
@ApiStatus.Internal
public fun sendMsgApi(
    messageType: String,
    target: ID,
    message: List<OneBotMessageSegment>,
    reply: ID? = null,
): SendMsgApi {
    return if (reply == null) {
        SendMsgApi.create(
            messageType = messageType,
            userId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_PRIVATE },
            groupId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_GROUP },
            message = OneBotMessageOutgoing.create(message),
            autoEscape = true
        )
    } else {
        val newMessage = resolveReplyMessageSegmentList(message, reply)

        SendMsgApi.create(
            messageType = messageType,
            userId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_PRIVATE },
            groupId = target.takeIf { messageType == SendMsgApi.MESSAGE_TYPE_GROUP },
            message = OneBotMessageOutgoing.create(newMessage)
        )
    }
}

@InternalOneBotAPI
@ApiStatus.Internal
public fun resolveReplyMessageSegmentList(
    message: List<OneBotMessageSegment>,
    reply: ID,
): List<OneBotMessageSegment> {
    val newMessage = ArrayList<OneBotMessageSegment>(message.size + 1)
    var hasReply = false
    for (segment in message) {
        if (!hasReply && segment is OneBotReply) {
            hasReply = true
        }

        newMessage.add(segment)
    }

    if (!hasReply) {
        newMessage.add(0, OneBotReply.create(reply))
    }
    return newMessage
}

@InternalOneBotAPI
@ApiStatus.Internal
public fun sendPrivateMsgApi(
    target: ID,
    message: List<OneBotMessageSegment>,
    reply: ID? = null,
): SendMsgApi = sendMsgApi(
    messageType = SendMsgApi.MESSAGE_TYPE_PRIVATE,
    target = target,
    message = message,
    reply = reply,
)

@InternalOneBotAPI
@ApiStatus.Internal
public fun sendGroupMsgApi(
    target: ID,
    message: List<OneBotMessageSegment>,
    reply: ID? = null,
): SendMsgApi = sendMsgApi(
    messageType = SendMsgApi.MESSAGE_TYPE_GROUP,
    target = target,
    message = message,
    reply = reply,
)


internal suspend fun OneBotBotImpl.emitMessagePreSendEventAndUseCurrentMessage(
    event: AbstractMessagePreSendEventImpl
): InteractionMessage {
    emitMessagePreSendEvent(event)
    return event.useCurrentMessage()
}

internal inline fun <T> resolveInteractionMessage(
    interactionMessage: InteractionMessage,
    allowSegmentMessage: Boolean = true,
    onSegments: (List<OneBotMessageSegment>) -> T,
    onMessage: (Message) -> T,
    onText: (String) -> T,
    onOther: (InteractionMessage) -> T = {
        error("Unknown InteractionMessage type: $interactionMessage")
    },
): T {
    if (!allowSegmentMessage) {
        return resolveInteractionMessageDisallowSegmentMessage(
            interactionMessage = interactionMessage,
            onSegments = onSegments,
            onMessage = onMessage,
            onText = onText,
            onOther = onOther
        )
    }

    return when (interactionMessage) {
        is InteractionMessage.Message -> onMessage(interactionMessage.message)
        is InteractionMessage.MessageContent -> {
            val messageContent = interactionMessage.messageContent
            if (messageContent is OneBotMessageContent) {
                onSegments(messageContent.sourceSegments)
            } else {
                onMessage(messageContent.messages)
            }
        }

        is InteractionMessage.Text -> onText(interactionMessage.text)
        is OneBotSegmentsInteractionMessage -> resolveInteractionMessageDisallowSegmentMessage(
            interactionMessage = interactionMessage.message,
            onSegments = onSegments,
            onMessage = onMessage,
            onText = onText,
            onOther = onOther
        )

        else -> onOther(interactionMessage)
    }
}

private inline fun <T> resolveInteractionMessageDisallowSegmentMessage(
    interactionMessage: InteractionMessage,
    onSegments: (List<OneBotMessageSegment>) -> T,
    onMessage: (Message) -> T,
    onText: (String) -> T,
    onOther: (InteractionMessage) -> T
): T {
    return when (interactionMessage) {
        is InteractionMessage.Message -> onMessage(interactionMessage.message)
        is InteractionMessage.MessageContent -> {
            val messageContent = interactionMessage.messageContent
            if (messageContent is OneBotMessageContent) {
                onSegments(messageContent.sourceSegments)
            } else {
                onMessage(messageContent.messages)
            }
        }

        is InteractionMessage.Text -> onText(interactionMessage.text)
        is OneBotSegmentsInteractionMessage -> {
            error(
                "InteractionMessage.message does not support the type OneBotSegmentsInteractionMessage, " +
                    "but $interactionMessage"
            )
        }

        else -> onOther(interactionMessage)
    }
}
