/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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
     * 对事件进行处理，得到需要被匹配的值。
     *
     * [preContent] 为上个处理器提供的结果。
     *
     * 如果是第一个处理器，且事件是 [love.forte.simbot.event.MessageEvent] 类型,
     * 则 [preContent] 为 [MessageEvent.messageContent.plainText][love.forte.simbot.message.MessageContent.plainText],
     * 否则为null。
     */
    public suspend fun process(preContent: String?, context: EventListenerProcessingContext): String?

}
