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

import love.forte.simbot.Bot
import love.forte.simbot.action.MessageSendSupport
import love.forte.simbot.definition.Target
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.RemoteMessageContainer
import love.forte.simbot.message.doSafeCast


/**
 * 一个存在消息内容的[事件][Event]。
 */
public interface MessageEvent : Event, RemoteMessageContainer {
    override val bot: Bot
    override val metadata: Event.Metadata

    /**
     * 当前消息事件所对应的事件源头.
     *
     * 通常情况下，[source] 都是可以 [发送消息][MessageSendSupport] 的。
     *
     */
    public val source: Target

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
 * 一个私有消息消息事件。
 *
 * 私有消息代表此事件只能由当前bot与聊天对象可见。
 *
 */
public interface PrivateMessageEvent : MessageEvent {
    /**
     * 通常情况下，私有消息的可见性是私人的。
     */
    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PRIVATE

    public companion object Key : BaseEventKey<PrivateMessageEvent>(
        "api.privateMessage",
        setOf(MessageEvent.Key)
    ) {
        override fun safeCast(value: Any): PrivateMessageEvent? = doSafeCast(value)
    }
}


/**
 * 一个组织消息事件
 *
 */
public interface OrgMessageEvent : MessageEvent {


}







