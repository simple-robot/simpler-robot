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
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.actor.internal.toMember
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.*
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotAnonymousGroupMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotNormalGroupMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotNoticeGroupMessageEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotGroupMessageEventPostReplyEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.toOneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.internal.message.OneBotMessageContentImpl
import love.forte.simbot.component.onebot.v11.core.utils.sendGroupMsgApi
import love.forte.simbot.component.onebot.v11.core.utils.sendGroupTextMsgApi
import love.forte.simbot.component.onebot.v11.event.message.RawGroupMessageEvent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageContent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage

internal abstract class OneBotGroupMessageEventImpl(
    sourceEvent: RawGroupMessageEvent,
    final override val bot: OneBotBotImpl
) : AbstractReplySupportInteractionEvent(), OneBotGroupMessageEvent {

    override val id: ID
        get() = "${sourceEvent.groupId}-${sourceEvent.messageId}".ID

    override val authorId: ID
        get() = sourceEvent.userId

    override val messageContent: OneBotMessageContent = OneBotMessageContentImpl(
        sourceEvent.messageId,
        sourceEvent.message,
        bot
    )

    override suspend fun content(): OneBotGroup {
        return bot.groupRelation.group(sourceEvent.groupId)
            ?: error("Group with id $groupId is not found")
    }

    abstract override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage):
        AbstractMessagePreSendEventImpl

    abstract override fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotGroupMessageEventPostReplyEvent

    override suspend fun replyText(text: String): SendMsgResult {
        return bot.executeData(
            sendGroupTextMsgApi(
                target = sourceEvent.groupId,
                text,
                reply = sourceEvent.messageId
            )
        )
    }

    override suspend fun replySegments(segments: List<OneBotMessageSegment>): SendMsgResult {
        return bot.executeData(
            sendGroupMsgApi(
                target = sourceEvent.groupId,
                message = segments,
                reply = sourceEvent.messageId
            )
        )
    }
}

internal class OneBotNormalGroupMessageEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupMessageEvent,
    bot: OneBotBotImpl,
) : OneBotGroupMessageEventImpl(sourceEvent, bot),
    OneBotNormalGroupMessageEvent {
    override suspend fun author(): OneBotMember {
        return sourceEvent.sender.toMember(bot, sourceEvent.groupId, sourceEvent.anonymous)
    }

    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotNormalGroupMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotGroupMessageEventPostReplyEvent {
        return OneBotNormalGroupMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotNormalGroupMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String = eventToString("OneBotNormalGroupMessageEvent")
}


internal class OneBotAnonymousGroupMessageEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupMessageEvent,
    bot: OneBotBotImpl,
) : OneBotGroupMessageEventImpl(sourceEvent, bot),
    OneBotAnonymousGroupMessageEvent {
    override suspend fun author(): OneBotMember {
        return sourceEvent.sender.toMember(bot, sourceEvent.groupId, sourceEvent.anonymous)
    }

    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotAnonymousGroupMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotGroupMessageEventPostReplyEvent {
        return OneBotAnonymousGroupMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotAnonymousGroupMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String = eventToString("OneBotAnonymousGroupMessageEvent")
}

internal class OneBotNoticeGroupMessageEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupMessageEvent,
    bot: OneBotBotImpl,
) : OneBotGroupMessageEventImpl(sourceEvent, bot),
    OneBotNoticeGroupMessageEvent {
    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotNoticeGroupMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotGroupMessageEventPostReplyEvent {
        return OneBotNoticeGroupMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotNoticeGroupMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String = eventToString("OneBotNoticeGroupMessageEvent")
}


internal class OneBotDefaultGroupMessageEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupMessageEvent,
    bot: OneBotBotImpl,
) : OneBotGroupMessageEventImpl(sourceEvent, bot) {
    override fun preReplyEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotDefaultGroupMessageEventPreReplyEventImpl(
            bot,
            this,
            interactionMessage
        )
    }

    override fun OneBotMessageReceipt.postReplyEvent(interactionMessage: InteractionMessage):
        OneBotGroupMessageEventPostReplyEvent {
        return OneBotDefaultGroupMessageEventPostReplyEventImpl(
            bot,
            content = this@OneBotDefaultGroupMessageEventImpl,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override fun toString(): String = eventToString("OneBotDefaultGroupMessageEvent")
}
