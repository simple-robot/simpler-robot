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

import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult

/**
 *
 * 对 [EventListenerProcessingContext.textContent] 的前置处理器，
 * 可以通过事件实体和context来决定目标监听函数所需要使用的content内容。
 *
 * [EventListenerTextContentProcessor] 是监听函数的专属拦截器的一种变体，属于 [AnnotatedEventListenerInterceptor].
 *
 * 因此，普通的拦截器将无法感知到处理器对 `textContent` 的处理。
 * 同样的，假如你在拦截器中已经有了对 `textContent` 的操作，那么我不再建议你通过当前处理器进行再次加工。
 *
 *
 * @see love.forte.simboot.annotation.Interceptor
 * @see AnnotatedEventListenerInterceptor
 * @see StandardTextContentProcessor
 * @author ForteScarlet
 */
public abstract class EventListenerTextContentProcessor internal constructor() : AnnotatedEventListenerInterceptor {
    override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult = process(context.eventContext).let { context.proceed() }

    /**
     * 通过 [context] 实例，解析并最终对 [EventListenerProcessingContext.textContent] 进行设置处理.
     */
    public abstract suspend fun process(context: EventListenerProcessingContext)

}


/**
 *
 * 标准的前置处理器。
 *
 * 标准的前置处理器均为 [AnnotatedEventListenerInterceptor], 因此它们只能使用于 [@Interceptor][love.forte.simboot.annotation.Interceptor] 中。
 *
 *
 */
public sealed class StandardTextContentProcessor : EventListenerTextContentProcessor() {

    /**
     * 当 [EventListenerProcessingContext.textContent] 不为 null 的时候，对其进行 trim 并重新设置。
     *
     * @see EventListenerTextContentProcessor
     * @see love.forte.simboot.annotation.ContentTrim
     */
    public object Trim : StandardTextContentProcessor() {
        override suspend fun process(context: EventListenerProcessingContext) {
            val text = context.textContent
            if (text != null) {
                context.textContent = text.trim()
            }
        }
    }

    /**
     * 将结果直接置为空值。
     *
     * @see EventListenerTextContentProcessor
     * @see love.forte.simboot.annotation.ContentToNull
     */
    public object Null : StandardTextContentProcessor() {
        override suspend fun process(context: EventListenerProcessingContext) {
            context.textContent = null
        }
    }


}







