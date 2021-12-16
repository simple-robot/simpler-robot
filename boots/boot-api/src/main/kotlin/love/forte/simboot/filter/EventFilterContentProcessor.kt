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

package love.forte.simboot.filter

import love.forte.simbot.event.EventProcessingContext

/**
 *
 * 事件处理器匹配正文处理器。
 *
 * @author ForteScarlet
 */
public interface EventFilterContentProcessor {

    /**
     * 对事件进行处理，得到需要被匹配的值。[preContent] 为上个处理器提供的结果。如果是第一个处理器，
     * 如果事件是 [love.forte.simbot.event.MessageEvent],  则为 [MessageEvent.messageContent.plainText][love.forte.simbot.message.MessageContent.plainText],
     * 否则为null。
     */
    public suspend fun process(preContent: String?, context: EventProcessingContext): String?

}