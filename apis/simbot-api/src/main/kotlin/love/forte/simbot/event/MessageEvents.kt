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
 *
 */

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.action.SendSupport
import love.forte.simbot.definition.*
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.RemoteMessageContainer
import love.forte.simbot.message.doSafeCast


/**
 * 一个存在消息内容的[事件][Event]。
 *
 * @see ContactMessageEvent
 * @see ChatroomMessageEvent
 *
 */
@BaseEvent
public interface MessageEvent : Event, RemoteMessageContainer {
    override val id: ID
    override val bot: Bot


    /**
     * 当前消息事件所对应的事件源头. 如果是组织相关的，则可能是 [ChatRoom] 的子类型，
     * 如果是私聊相关，则代表发送者。
     *
     * 通常情况下，[source] 都是可以 [发送消息][SendSupport] 的。
     *
     */
    @JvmSynthetic
    public suspend fun source(): Objectives


    /**
     * 当前消息事件所对应的事件源头. 如果是组织相关的，则可能是 [ChatRoom] 的子类型，
     * 如果是私聊相关，则代表发送者。
     *
     * 通常情况下，[source] 都是可以 [发送消息][SendSupport] 的。
     *
     */
    @Api4J
    public val source: Objectives


    /**
     * 当前消息事件的消息正文。
     */
    override val messageContent: ReceivedMessageContent


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
    @JvmSynthetic
    override suspend fun user(): Contact

    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    @Api4J
    override val user: Contact


    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    @JvmSynthetic
    override suspend fun source(): Contact

    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    @Api4J
    override val source: Contact


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
    @JvmSynthetic
    override suspend fun user(): Friend

    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @Api4J
    override val user: Friend

    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @JvmSynthetic
    override suspend fun source(): Friend

    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    @Api4J
    override val source: Friend


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
public interface ChatroomMessageEvent : MessageEvent, OrganizationEvent, DeleteSupport, RemoteMessageContainer {
    override val id: ID

    /**
     * 来自的聊天室，通常是一个[群][Group]或者一个[频道][Channel]。
     */
    @JvmSynthetic
    override suspend fun source(): ChatRoom

    /**
     * 来自的聊天室，通常是一个[群][Group]或者一个[频道][Channel]。
     */
    @Api4J
    override val source: ChatRoom

    /**
     * 这个消息的发送者.
     */
    @JvmSynthetic
    public suspend fun author(): Member

    /**
     * 这个消息的发送者.
     */
    @Api4J
    public val author: Member


    /**
     * 预期内，假若当前bot拥有足够的权限则可以对消息进行删除（撤回）操作。
     *
     * @see DeleteSupport
     */
    @JvmSynthetic
    override suspend fun delete(): Boolean


    public companion object Key : BaseEventKey<ChatroomMessageEvent>(
        "api.privateMessage", MessageEvent.Key
    ) {
        override fun safeCast(value: Any): ChatroomMessageEvent? = doSafeCast(value)
    }

}

/**
 * ```kotlin
 * event.useAuthor { author ->
 *      // ...
 * }
 * ```
 */
public suspend inline fun <R> ChatroomMessageEvent.useAuthor(block: (Member) -> R): R = author().let(block)

/**
 * ```kotlin
 * event.inAuthor { // this: Member
 *      // ...
 * }
 * ```
 */
public suspend inline fun <R> ChatroomMessageEvent.inAuthor(block: Member.() -> R): R = author().let(block)


/**
 *  代表一个来自[群][Group]的消息事件。
 *
 */
public interface GroupMessageEvent : ChatroomMessageEvent, GroupEvent {

    /**
     * 消息来自的[群][Group]。
     */
    @JvmSynthetic
    override suspend fun source(): Group


    /**
     * 消息来自的[群][Group]。
     */
    @Api4J
    override val source: Group


    public companion object Key : BaseEventKey<GroupMessageEvent>(
        "api.group_message", ChatroomMessageEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMessageEvent? = doSafeCast(value)
    }

}

/**
 *
 * 代表一个来自[频道][Channel]的消息事件。
 *
 */
public interface ChannelMessageEvent : ChatroomMessageEvent, ChannelEvent {

    /**
     * 消息事件来源的[频道][Channel].
     */
    override suspend fun source(): Channel

    /**
     * 消息事件来源的[频道][Channel].
     */
    @Api4J
    override val source: Channel


    public companion object Key : BaseEventKey<ChannelMessageEvent>(
        "api.channel_message", ChatroomMessageEvent, ChannelEvent
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
