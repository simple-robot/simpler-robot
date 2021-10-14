package love.forte.simbot.kaiheila

import love.forte.simbot.api.message.MessageContent


/**
 *
 * Khl内部的 [MessageContent] 实例。
 *
 * @author ForteScarlet
 */
public interface KhlMessageContent : MessageContent {
    val messageType: MessageType get() = TODO()
}