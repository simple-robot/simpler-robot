package love.forte.simbot

import love.forte.simbot.message.MessageContent


/**
 * 一个存在消息内容的[事件][Event]。
 */
public interface MessageEvent : Event {
    override val bot: Bot
    override val metadata: Event.Metadata

    /**
     * 当前消息事件的消息正文。
     */
    public val messageContent: MessageContent

}


/**
 * 事件的所属组件。
 */
public inline val Event.component: Component get() = bot.component

/**
 * 事件的唯一ID。
 */
public inline val Event.id: ID get() = metadata.id