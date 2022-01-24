/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.filter

import love.forte.simbot.event.EventListenerProcessingContext

/**
 *
 * 事件处理器匹配正文处理器。
 *
 * TODO
 *
 * @author ForteScarlet
 */
public interface EventFilterContentProcessor {

    /**
     * 对事件进行处理，得到需要被匹配的值。[preContent] 为上个处理器提供的结果。
     * 如果是第一个处理器，且事件是 [love.forte.simbot.event.MessageEvent] 类型,
     * 则为 [MessageEvent.messageContent.plainText][love.forte.simbot.message.MessageContent.plainText],
     * 否则为null。
     */
    public suspend fun process(preContent: String?, context: EventListenerProcessingContext): String?

}