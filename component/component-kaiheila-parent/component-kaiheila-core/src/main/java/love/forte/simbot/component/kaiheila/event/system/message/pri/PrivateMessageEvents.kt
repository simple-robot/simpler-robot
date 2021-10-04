package love.forte.simbot.component.kaiheila.event.system.message.pri

import love.forte.simbot.component.kaiheila.event.system.message.MessageEventExtra
import love.forte.simbot.component.kaiheila.event.system.message.MessageEventExtraBody


/**
 * 私聊消息相关
 *
 */
public interface PrivateMessageEventExtraBody : MessageEventExtraBody


/**
 * 私聊消息 Extra 数据。
 */
public interface PrivateMessageEventExtra<B : PrivateMessageEventExtraBody> : MessageEventExtra<B>


