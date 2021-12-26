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

import love.forte.simbot.event.EventListenerProcessingContext

/**
 *
 * 对 [EventListenerProcessingContext.textContent] 的前置处理器，
 * 可以通过事件实体和context来决定目标监听函数所需要使用的content内容。
 *
 * 本质上与拦截器类似, 通过 [process] 来对 context 中的 `textContent` 进行处理, 原则上应当属于最外层的拦截器。
 *
 * @see StandardTextContentProcessor
 * @author ForteScarlet
 */
public interface EventListenerTextContentProcessor {

    /**
     * 通过 [context] 实例，解析并最终对 [EventListenerProcessingContext.textContent] 进行设置处理.
     */
    public suspend fun process(context: EventListenerProcessingContext)

}


/**
 *
 * 标准的前置处理器。
 *
 */
public sealed class StandardTextContentProcessor : EventListenerTextContentProcessor {

    /**
     * 当 [EventListenerProcessingContext.textContent] 不为 null 的时候，对其进行 trim 并重新设置。
     *
     * @see EventListenerTextContentProcessor
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
     */
    public object Null : StandardTextContentProcessor() {
        override suspend fun process(context: EventListenerProcessingContext) {
            context.textContent = null
        }
    }


}







