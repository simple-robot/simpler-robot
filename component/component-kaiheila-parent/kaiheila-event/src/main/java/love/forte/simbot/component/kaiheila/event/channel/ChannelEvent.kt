package love.forte.simbot.component.kaiheila.event.channel

import love.forte.simbot.component.kaiheila.event.Event


/**
 *
 * [频道相关事件](https://developer.kaiheila.cn/doc/event/channel)
 *
 */
public interface ChannelEvent<T> : Event<ChannelEvent.Extra<T>> {
    public interface Extra<T> : Event.Extra.Sys<T>
}


public abstract class BaseChannelEvent<T>(
    override val channelType: String = "GROUP",
    override val type: Event.Type,

) : ChannelEvent<T> {
    override val targetId: String
        get() = TODO("Not yet implemented")
    override val authorId: String
        get() = TODO("Not yet implemented")
    override val content: String
        get() = TODO("Not yet implemented")
    override val msgId: String
        get() = TODO("Not yet implemented")
    override val msgTimestamp: Long
        get() = TODO("Not yet implemented")
    override val nonce: String
        get() = TODO("Not yet implemented")
    override val extra: ChannelEvent.Extra<T>
        get() = TODO("Not yet implemented")
}
