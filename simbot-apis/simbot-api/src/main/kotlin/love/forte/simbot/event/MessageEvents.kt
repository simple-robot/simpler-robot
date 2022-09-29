/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.action.ReplySupport
import love.forte.simbot.action.SendSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.*
import love.forte.simbot.utils.runInBlocking


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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun user(): Contact
    
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun user(): Friend
    
    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    override suspend fun source(): ChatRoom
    
    /**
     * 这个消息的发送者.
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
    public suspend fun author(): Member
    
    
    /**
     * 预期内，假若当前bot拥有足够的权限则可以对消息进行删除（撤回）操作。
     *
     * Deprecated: 使用 [messageContent.delete][RemoteMessageContent.delete]。
     *
     * @see messageContent
     */
    @JvmSynthetic
    @Deprecated("Use messageContent.delete()", ReplaceWith("messageContent.delete()"), level = DeprecationLevel.ERROR)
    public suspend fun delete(): Boolean = messageContent.delete()
    
    /**
     * 预期内，假若当前bot拥有足够的权限则可以对消息进行删除（撤回）操作。
     *
     * Deprecated: 使用 [messageContent.deleteBlocking][RemoteMessageContent.delete]。
     *
     * @see messageContent
     */
    @Api4J
    @Deprecated(
        "Use getMessageContent().deleteBlocking()",
        ReplaceWith("messageContent.deleteBlocking()"),
        level = DeprecationLevel.ERROR
    )
    public fun deleteBlocking(): Boolean = runInBlocking { messageContent.delete() }
    
    
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
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
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true)
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
