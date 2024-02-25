/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.event

import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IDContainer
import love.forte.simbot.definition.*
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.STP

/**
 * 一个含有 [MessageContent] 的事件。
 */
public interface MessageContentAwareEvent : Event {
    /**
     * 事件中收到的消息内容。
     */
    public val messageContent: MessageContent
}

/**
 * 一个 [Bot] 收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface MessageEvent : BotEvent, ReplySupport, MessageContentAwareEvent {
    /**
     * 这个消息的发送者的ID。
     */
    public val authorId: ID

    /**
     * 事件中收到的消息内容。
     */
    override val messageContent: MessageContent
}

/**
 * 可以感知到发送消息目标的 [MessageEvent] 事件类型。
 *
 * @see ActorAuthorAwareMessageEvent
 */
@STP
public interface AuthorAwareMessageEvent : MessageEvent {
    /**
     * 此消息的发送者。发送者存在一个 ID 标识。
     */
    public suspend fun author(): IDContainer
}

/**
 * 可以感知到发送消息的 [Actor] 目标的 [MessageEvent] 事件类型。
 *
 * @see MemberAuthorAwareMessageEvent
 */
@STP
public interface ActorAuthorAwareMessageEvent : AuthorAwareMessageEvent {
    /**
     * 此消息的发送 [Actor]。
     */
    public override suspend fun author(): Actor
}

/**
 * 可以感知到发送消息的 [Member] 目标的 [MessageEvent] 事件类型。
 *
 * @see ChatGroupMessageEvent
 * @see ChatChannelMessageEvent
 */
@STP
public interface MemberAuthorAwareMessageEvent : ActorAuthorAwareMessageEvent {
    /**
     * 此消息的发送 [Member]。
     */
    public override suspend fun author(): Member
}


/**
 * 一个 [Bot] 从 [ChatRoom] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatRoomMessageEvent : MessageEvent, ChatRoomEvent

/**
 * 一个 [Bot] 从 [ChatGroup] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatGroupMessageEvent : ChatRoomMessageEvent, ChatGroupEvent, MemberAuthorAwareMessageEvent

/**
 * 一个 [Bot] 从 [ChatChannel] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatChannelMessageEvent : ChatRoomMessageEvent, ChatChannelEvent, MemberAuthorAwareMessageEvent

/**
 * 一个 [Bot] 从 [Member] 处收到私聊消息的事件。
 *
 * @author ForteScarlet
 */
public interface MemberMessageEvent : MessageEvent, MemberEvent

/**
 * 一个 [Bot] 从 [ChatGroup] 中的 [Member] 处收到私聊消息的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatGroupMemberMessageEvent : MessageEvent, MemberEvent {
    /**
     * 事件中 [member][content] 所在的 [ChatGroup]。
     */
    override suspend fun source(): ChatGroup
}

/**
 * 一个 [Bot] 从 [Guild] 中的 [Member] 处收到私聊消息的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GuildMemberMessageEvent : MessageEvent, MemberEvent {
    /**
     * 事件中 [member][content] 所在的 [Guild]。
     */
    override suspend fun source(): Guild
}

/**
 * 一个 [Bot] 从 [Contact] 处收到私聊消息的事件。
 */
public interface ContactMessageEvent : MessageEvent, ContactEvent
