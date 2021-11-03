package love.forte.simbot.event

import kotlinx.serialization.Serializable
import love.forte.simbot.Bot
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.message.MessageContent

/**
 *
 * [事件][Event] 的顶层接口。
 *
 * @author ForteScarlet
 */
public interface Event {
    /**
     * 与这个事件有关系的 [Bot].
     */
    public val bot: Bot

    /**
     * 这个事件的[元数据][Metadata]。
     */
    public val metadata: Metadata


    /**
     * 事件的 [元数据][Metadata].
     *
     * 事件元数据记录这个事件较为原始的数据，例如其唯一ID、服务器时间等。
     *
     * 元数据中存在什么，完全由事件实现者决定。
     * 但是无论如何，元消息应当存在一个能够决定当前事件唯一性的 [id].
     *
     * 对于两个事件之间是否相同，即使用 [component] 和 [Metadata.id] 进行决定, 当同一个组件下的事件之间的 [Metadata.id] 的 [equals] 相同，
     * 则认为两个事件相同。
     *
     * 元消息应能够支持 [序列化][Serializable].
     */
    public interface Metadata {
        /** 元数据唯一标识。 */
        public val id: ID
    }
}

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