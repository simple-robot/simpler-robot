/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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
