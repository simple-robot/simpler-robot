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


    override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult = EventResult.default()

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







