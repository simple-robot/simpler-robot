package love.forte.simbot.event

import love.forte.simbot.ID


/**
 *
 * 事件结果, 用于提供有关一次事件处理执行后的响应。
 *
 * @author ForteScarlet
 */
public interface EventResult {

    /**
     * 此 ID 为提供当前响应的
     */
    public val eventId: ID

}