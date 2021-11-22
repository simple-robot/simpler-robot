package love.forte.simbot.event

import love.forte.simbot.Bot
import love.forte.simbot.message.doSafeCast


/**
 *
 * 与 **请求** 有关的事件。
 *
 * @author ForteScarlet
 */
public interface RequestEvent : Event {
    override val metadata: Event.Metadata
    override val bot: Bot

    /**
     * 对于请求事件，可见范围普遍为 [Event.VisibleScope.INTERNAL] 或 [Event.VisibleScope.PRIVATE]. 
     */
    override val visibleScope: Event.VisibleScope



    public companion object Key : BaseEventKey<RequestEvent>("api-request") {
        override fun safeCast(value: Any): RequestEvent? = doSafeCast(value)
    }
}