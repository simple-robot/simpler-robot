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

package love.forte.simbot.component.onebot.v11.core.actor.internal

import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
import love.forte.simbot.component.onebot.v11.core.actor.OneBotStranger
import love.forte.simbot.component.onebot.v11.core.api.GetFriendListResult
import love.forte.simbot.component.onebot.v11.core.api.GetStrangerInfoApi
import love.forte.simbot.component.onebot.v11.core.api.SendLikeApi
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.AbstractMessagePreSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.OneBotFriendPostSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.OneBotFriendPreSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotInternalMessagePostSendEvent
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.toOneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.utils.sendPrivateMsgApi
import love.forte.simbot.component.onebot.v11.core.utils.sendPrivateTextMsgApi
import love.forte.simbot.component.onebot.v11.event.message.RawPrivateMessageEvent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage
import kotlin.coroutines.CoroutineContext

internal abstract class OneBotFriendImpl : AbstractSendSupport(), OneBotFriend {
    abstract override val bot: OneBotBotImpl

    override fun preSendEvent(interactionMessage: OneBotSegmentsInteractionMessage): AbstractMessagePreSendEventImpl {
        return OneBotFriendPreSendEventImpl(
            this,
            bot,
            interactionMessage
        )
    }

    override suspend fun sendText(text: String): SendMsgResult {
        return bot.executeData(
            sendPrivateTextMsgApi(
                target = id,
                text = text,
            )
        )
    }

    override suspend fun sendSegments(segments: List<OneBotMessageSegment>): SendMsgResult {
        return bot.executeData(
            sendPrivateMsgApi(
                target = id,
                message = segments,
            )
        )
    }

    override fun OneBotMessageReceipt.postSendEvent(
        interactionMessage: InteractionMessage
    ): OneBotInternalMessagePostSendEvent {
        return OneBotFriendPostSendEventImpl(
            content = this@OneBotFriendImpl,
            bot = bot,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )
    }

    override suspend fun sendLike(times: Int) {
        bot.executeData(
            SendLikeApi.create(
                userId = id,
                times = times
            )
        )
    }

    override suspend fun toStranger(): OneBotStranger =
        bot.executeData(
            GetStrangerInfoApi.create(userId = id)
        ).toStranger(bot)

    override fun toString(): String = "OneBotFriend(id=$id, bot=${bot.id})"
}

/**
 * 基于 [RawPrivateMessageEvent.Sender] 的 [OneBotFriend] 实现
 *
 */
internal class OneBotFriendEventSenderImpl(
    private val sender: RawPrivateMessageEvent.Sender,
    override val bot: OneBotBotImpl,
) : OneBotFriendImpl(), OneBotFriend {
    override val coroutineContext: CoroutineContext = bot.subContext

    override val id: ID
        get() = sender.userId

    override val name: String
        get() = sender.nickname
}

internal fun RawPrivateMessageEvent.Sender.toFriend(bot: OneBotBotImpl): OneBotFriendEventSenderImpl =
    OneBotFriendEventSenderImpl(this, bot)

internal class OneBotFriendApiResultImpl(
    private val result: GetFriendListResult,
    override val bot: OneBotBotImpl,
) : OneBotFriendImpl(), OneBotFriend {
    override val coroutineContext: CoroutineContext = bot.subContext

    override val name: String
        get() = result.nickname

    override val id: ID
        get() = result.userId
}

internal fun GetFriendListResult.toFriend(bot: OneBotBotImpl): OneBotFriendApiResultImpl =
    OneBotFriendApiResultImpl(this, bot)

