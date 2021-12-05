/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.action.DeleteAction
import love.forte.simbot.action.MessageSendAction
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
public interface MessageEvent : Event, RemoteMessageContainer {
    override val bot: Bot
    override val metadata: Event.Metadata

    /**
     * 当前消息事件所对应的事件源头.
     *
     * 通常情况下，[source] 都是可以 [发送消息][MessageSendAction] 的。
     *
     */
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
 * 来自一个[联系人][Contact]消息消息事件。 这通常代表一个私聊消息事件。
 *
 */
public interface ContactMessageEvent : MessageEvent, UserEvent {
    /**
     * 私有消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    override val source: Contact

    @Api4J
    override val objective: Objectives
        get() = source

    @Api4J
    override val user: User
        get() = source

    /**
     * 通常情况下，联系人消息的可见性是私人的。
     */
    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PRIVATE

    public companion object Key : BaseEventKey<ContactMessageEvent>(
        "api.private_message",
        setOf(MessageEvent, UserEvent)
    ) {
        override fun safeCast(value: Any): ContactMessageEvent? = doSafeCast(value)
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
public interface ChatroomMessageEvent : MessageEvent, OrganizationEvent, DeleteAction, RemoteMessageContainer {
    /**
     * 来自的聊天室，通常是一个群或者一个频道。
     */
    override val source: ChatRoom

    @Api4J
    override val objective: Objectives
        get() = source

    /**
     * 这个消息的发送者.
     */
    public val author: Member


    /**
     * 预期内，假若当前bot拥有足够的权限则可以对消息进行删除（撤回）操作。
     *
     */
    override suspend fun delete(): Boolean


    public companion object Key : BaseEventKey<ChatroomMessageEvent>(
        "api.privateMessage",
        setOf(MessageEvent.Key)
    ) {
        override fun safeCast(value: Any): ChatroomMessageEvent? = doSafeCast(value)
    }

}


/**
 *  代表一个来自[群][Group]的消息事件。
 *
 */
public interface GroupMessageEvent : ChatroomMessageEvent, GroupEvent {

    /**
     * 消息来自的群。
     */
    override val source: Group
    override val author: Member

    @Api4J
    override val objective: Objectives
        get() = source

    @Api4J
    override val group: Group
        get() = source

    public companion object Key : BaseEventKey<GroupMessageEvent>(
        "api.group_message",
        setOf(ChatroomMessageEvent, GroupEvent)
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
     * 消息来自的频道
     */
    override val source: Channel
    override val author: Member
    override suspend fun channel(): Channel


    @Api4J
    override val objective: Objectives
        get() = source


    @Api4J
    override val channel: Channel
        get() = source

    public companion object Key : BaseEventKey<ChannelMessageEvent>(
        "api.channel_message",
        setOf(ChatroomMessageEvent, ChannelEvent)
    ) {
        override fun safeCast(value: Any): ChannelMessageEvent? = doSafeCast(value)
    }
}


