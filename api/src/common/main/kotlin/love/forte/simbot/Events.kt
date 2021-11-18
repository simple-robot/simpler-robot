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