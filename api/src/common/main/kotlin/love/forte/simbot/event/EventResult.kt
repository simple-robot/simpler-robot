package love.forte.simbot.event

import love.forte.simbot.ID
import love.forte.simbot.event.EventResult.Invalid


/**
 *
 * 事件结果, 用于提供有关一次事件处理执行后的响应。
 *
 * [EventResult] 的特殊伴生对象 [Invalid] 可用于拦截器、过滤器中。事件处理器如果遇到了 [Invalid], 则应该直接忽略此值。
 *
 * @author ForteScarlet
 */
public interface EventResult {

    /**
     * 此 ID 为提供当前响应的
     */
    public val eventId: ID


    /**
     * 代表着 **无效** 的 [EventResult] 实例。事件处理器不应对此结果进行保留或处理。
     */
    public companion object Invalid : EventResult {
        override val eventId: ID = 0.ID

    }
}