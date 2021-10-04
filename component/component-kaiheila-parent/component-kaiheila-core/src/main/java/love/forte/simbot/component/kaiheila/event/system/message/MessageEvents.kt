package love.forte.simbot.component.kaiheila.event.system.message

import love.forte.simbot.component.kaiheila.event.Event


/**
 *
 * 消息相关系统事件
 *
 * @author ForteScarlet
 */
public interface MessageEventExtraBody : Event.Extra.Sys.Body


/**
 * 消息相关事件 的事件体 `extra`
 *
 */
public interface MessageEventExtra<B : MessageEventExtraBody> : Event.Extra.Sys<B>
