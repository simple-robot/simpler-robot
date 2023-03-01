/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.event

import love.forte.simbot.ID
import love.forte.simbot.JSTP
import love.forte.simbot.action.ReplySupport
import love.forte.simbot.action.SendSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.*


/**
 * 一个存在消息内容的[事件][Event]。
 *
 * 一个 [MessageEvent] 通常情况下都应支持 [ReplySupport].
 *
 * @see ContactMessageEvent
 * @see ChatRoomMessageEvent
 *
 */
@BaseEvent
public interface MessageEvent : Event, RemoteMessageContainer, ReplySupport {
    override val id: ID
    override val bot: Bot
    
    
    /**
     * 当前消息事件所对应的事件源头. 如果是组织相关的，则可能是 [ChatRoom] 的子类型，
     * 如果是私聊相关，则代表发送者，即 [Contact] 或其子类型。
     *
     * 通常情况下，[source] 都是可以 [发送消息][SendSupport] 的。
     *
     */
    @JSTP
    public suspend fun source(): Objective
    
    
    /**
     * 当前消息事件的消息正文。
     */
    override val messageContent: ReceivedMessageContent
    
    /**
     * 回复此事件。
     *
     * 一个 [MessageEvent] 通常都应支持 [ReplySupport].
     * [reply] 代表对本次事件中涉及的目标对象进行**针对性**的回复。
     * 通常表现形式会与直接进行 [send][SendSupport] 有一定区别，例如会自动携带一个 at 或者消息引用等。
     *
     * @throws love.forte.simbot.action.UnsupportedActionException 当此消息事件不支持进行回复时
     *
     */
    @JvmSynthetic
    override suspend fun reply(message: Message): MessageReceipt
    
    // Event key
    public companion object Key : BaseEventKey<MessageEvent>("api.message") {
        override fun safeCast(value: Any): MessageEvent? = doSafeCast(value)
    }
}


/**
 * 来自一个[联系人][Contact]消息消息事件。 这通常代表一个私聊消息事件，但对话者不一定是 [好友][Friend]。
 *
 */
public interface ContactMessageEvent : MessageEvent, UserEvent {
    override val id: ID
    
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    @JSTP
    override suspend fun user(): Contact
    
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    @JSTP
    override suspend fun source(): Contact
    
    
    public companion object Key : BaseEventKey<ContactMessageEvent>(
        "api.contact_message", MessageEvent, UserEvent
    ) {
        override fun safeCast(value: Any): ContactMessageEvent? = doSafeCast(value)
    }
}


/**
 * 一个来自于[好友][Friend]的消息事件。这通常代表为一个私聊消息事件。
 *
 * @see ContactMessageEvent
 */
public interface FriendMessageEvent : ContactMessageEvent, FriendEvent {
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @JvmSynthetic
    override suspend fun friend(): Friend
    
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @JSTP
    override suspend fun user(): Friend
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @JSTP
    override suspend fun source(): Friend
    
    
    public companion object Key : BaseEventKey<FriendMessageEvent>(
        "api.friend_message", ContactMessageEvent, FriendEvent
    ) {
        override fun safeCast(value: Any): FriendMessageEvent? = doSafeCast(value)
    }
}


/**
 * 一个来自聊天室的消息事件。
 *
 *
 * @see GroupMessageEvent
 * @see ChannelMessageEvent
 *
 */
public interface ChatRoomMessageEvent : MessageEvent, OrganizationEvent, RemoteMessageContainer {
    override val id: ID
    
    /**
     * 来自的聊天室，通常是一个[群][Group]或者一个[频道][Channel]。
     */
    @JSTP
    override suspend fun source(): ChatRoom
    
    /**
     * 这个消息的发送者.
     */
    @JSTP
    public suspend fun author(): Member
    
    public companion object Key : BaseEventKey<ChatRoomMessageEvent>(
        "api.chat_room_message", MessageEvent.Key
    ) {
        override fun safeCast(value: Any): ChatRoomMessageEvent? = doSafeCast(value)
    }
    
}

/**
 * ```kotlin
 * event.useAuthor { author ->
 *      // ...
 * }
 * ```
 */
public suspend inline fun <R> ChatRoomMessageEvent.useAuthor(block: (Member) -> R): R = author().let(block)

/**
 * ```kotlin
 * event.inAuthor { // this: Member
 *      // ...
 * }
 * ```
 */
public suspend inline fun <R> ChatRoomMessageEvent.inAuthor(block: Member.() -> R): R = author().let(block)


/**
 *  代表一个来自[群][Group]的消息事件。
 *
 */
public interface GroupMessageEvent : ChatRoomMessageEvent, GroupEvent {
    
    /**
     * 消息来自的[群][Group]。
     */
    @JSTP
    override suspend fun source(): Group
    
    
    public companion object Key : BaseEventKey<GroupMessageEvent>(
        "api.group_message", ChatRoomMessageEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMessageEvent? = doSafeCast(value)
    }
    
}

/**
 *
 * 代表一个来自[频道][Channel]的消息事件。
 *
 */
public interface ChannelMessageEvent : ChatRoomMessageEvent, ChannelEvent {
    
    /**
     * 消息事件来源的[频道][Channel].
     */
    @JSTP
    override suspend fun source(): Channel
    
    public companion object Key : BaseEventKey<ChannelMessageEvent>(
        "api.channel_message", ChatRoomMessageEvent, ChannelEvent
    ) {
        override fun safeCast(value: Any): ChannelMessageEvent? = doSafeCast(value)
    }
}


/**
 * 消息被回应事件。
 *
 * 消息被回应事件的内容十分不稳定，因此标准接口中仅提供最低限度的ID相关内容。
 *
 */
public interface MessageReactedEvent : MessageEvent {
    /**
     * 被回应的消息的ID。
     *
     * 事件定义无法保证被回应的 [消息][love.forte.simbot.message.MessageContent] 能够支持被获取，
     * 但是对于此事件，应当至少存在一个被回应消息的ID。
     */
    public val reactedId: ID
    
    
    /**
     * 回应这条消息的回应者ID。
     */
    public val reactorId: ID
    
}
