/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simboot.listener

import love.forte.simboot.interceptor.ListenerMatchPreparator
import love.forte.simboot.interceptor.ListenerPreparator
import love.forte.simbot.event.EventListenerProcessingContext


/**
 *
 * 标准的前置处理器。
 * [StandardTextContentProcessor] 是对 [EventListenerProcessingContext.textContent] 的前置处理器，
 * 可以通过事件实体和context来决定目标监听函数所需要使用的content内容。
 *
 * 标准的前置处理器均为 [ListenerMatchPreparator], 因此它们只能使用于 [@Preparator][love.forte.simboot.annotation.Preparator] 中。
 *
 * @see ListenerMatchPreparator
 */
public sealed class StandardTextContentProcessor : ListenerPreparator {

    /**
     * 当 [EventListenerProcessingContext.textContent] 不为 null 的时候，对其进行 trim 并重新设置。
     *
     * @see StandardTextContentProcessor
     * @see love.forte.simboot.annotation.ContentTrim
     */
    public object Trim : StandardTextContentProcessor(), ListenerMatchPreparator {
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
    public object Null : StandardTextContentProcessor(), ListenerMatchPreparator {
        override suspend fun prepareMatch(context: EventListenerProcessingContext) {
            context.textContent = null
        }
    }


}







