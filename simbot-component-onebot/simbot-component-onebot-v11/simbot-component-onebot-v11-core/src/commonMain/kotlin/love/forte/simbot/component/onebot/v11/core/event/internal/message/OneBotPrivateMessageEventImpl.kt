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

package love.forte.simbot.component.onebot.v11.core.event.internal.message

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.actor.internal.toFriend
import love.forte.simbot.component.onebot.v11.core.actor.internal.toMember
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.*
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupPrivateMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotPrivateMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotPrivateMessageEventPostReplyEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.toOneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.internal.message.OneBotMessageContentImpl
import love.forte.simbot.component.onebot.v11.core.utils.sendPrivateMsgApi
import love.forte.simbot.component.onebot.v11.core.utils.sendPrivateTextMsgApi
import love.forte.simbot.component.onebot.v11.event.message.RawPrivateMessageEvent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageContent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage


internal abstract class OneBotPrivateMessageEventImpl(
    final override val sourceEvent: RawPrivateMessageEvent,
    final override val bot: OneBotBotImpl,
) : AbstractReplySupportInteractionEvent(), OneBotPrivateMessageEvent {
    override val id: ID
        get() = "${sourceEvent.userId}-${sourceEvent.messageId}".ID

    override val authorId: ID
        get() = sourceEvent.userId

    override val messageContent: OneBotMessageContent = OneBotMessageContentImpl(
        sourceEvent.messageId,
        sourceEvent.message,
        bot
    )

    override suspend fun replyText(text: String): SendMsgResult {
        return bot.executeData(
            sendPrivateTextMsgApi(
                target = sourceEvent.userId,
                text,
            )
        )
    }

    override suspend fun replySegments(segments: List<OneBotMessageSegment>): SendMsgResult {
        return bot.executeData(
            sendPrivateMsgApi(
                target = sourceEvent.userId,
                message = segments
            )
        )
    }

    abstract override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage):
        AbstractMessagePreSendEventImpl

    abstract override fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotPrivateMessageEventPostReplyEvent
}

internal class OneBotFriendMessageEventImpl(
    override val sourceEventRaw: String?,
    sourceEvent: RawPrivateMessageEvent,
    bot: OneBotBotImpl,
) : OneBotPrivateMessageEventImpl(sourceEvent, bot),
    OneBotFriendMessageEvent {
    override suspend fun content(): OneBotFriend {
        return sourceEvent.sender.toFriend(bot)
    }

    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotFriendMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(
        interactionMessage: InteractionMessage
    ): OneBotPrivateMessageEventPostReplyEvent {
        return OneBotFriendMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotFriendMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String =
        eventToString("OneBotFriendMessageEvent")
}

internal class OneBotGroupPrivateMessageEventImpl(
    override val sourceEventRaw: String?,
    sourceEvent: RawPrivateMessageEvent,
    bot: OneBotBotImpl,
) : OneBotPrivateMessageEventImpl(sourceEvent, bot),
    OneBotGroupPrivateMessageEvent {
    override suspend fun content(): OneBotMember {
        return sourceEvent.sender.toMember(bot, groupId = null) // group id is unknown.
    }

    override suspend fun source(): OneBotGroup {
        // TODO 额，怎么知道群号？
        throw UnsupportedOperationException("The way to get the group id from PrivateMessageEvent is unknown yet.")
    }

    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotGroupPrivateMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(
        interactionMessage: InteractionMessage
    ): OneBotPrivateMessageEventPostReplyEvent {
        return OneBotGroupPrivateMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotGroupPrivateMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String =
        eventToString("OneBotGroupPrivateMessageEvent")
}

internal class OneBotDefaultPrivateMessageEventImpl(
    override val sourceEventRaw: String?,
    sourceEvent: RawPrivateMessageEvent,
    bot: OneBotBotImpl,
) : OneBotPrivateMessageEventImpl(sourceEvent, bot) {

    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotDefaultPrivateMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(
        interactionMessage: InteractionMessage
    ): OneBotPrivateMessageEventPostReplyEvent {
        return OneBotDefaultPrivateMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotDefaultPrivateMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String =
        eventToString("OneBotDefaultPrivateMessageEvent")
}
