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

package love.forte.simboot.listener

import love.forte.simboot.interceptor.ListenerMatchPreparer
import love.forte.simboot.interceptor.ListenerPreparer
import love.forte.simbot.event.EventListenerProcessingContext


/**
 *
 * 标准的前置处理器。
 * [StandardTextContentProcessor] 是对 [EventListenerProcessingContext.textContent] 的前置处理器，
 * 可以通过事件实体和context来决定目标监听函数所需要使用的content内容。
 *
 * 标准的前置处理器均为 [ListenerMatchPreparer], 因此它们只能使用于 [@Preparer][love.forte.simboot.annotation.Preparator] 中。
 *
 * @see ListenerMatchPreparer
 */
public sealed class StandardTextContentProcessor : ListenerPreparer {

    /**
     * 当 [EventListenerProcessingContext.textContent] 不为 null 的时候，对其进行 trim 并重新设置。
     *
     * @see StandardTextContentProcessor
     * @see love.forte.simboot.annotation.ContentTrim
     */
    public object Trim : StandardTextContentProcessor(), ListenerMatchPreparer {
        override suspend fun prepareMatch(context: EventListenerProcessingContext) {
            val text = context.textContent
            if (text != null) {
                context.textContent = text.trim()
            }
        }
    }

    /**
     * 将结果直接置为空值。
     *
     * @see StandardTextContentProcessor
     * @see love.forte.simboot.annotation.ContentToNull
     */
    public object Null : StandardTextContentProcessor(), ListenerMatchPreparer {
        override suspend fun prepareMatch(context: EventListenerProcessingContext) {
            context.textContent = null
        }
    }


}







