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

package love.forte.simboot.core.listener

import love.forte.simboot.listener.ListenerAnnotationProcessor
import love.forte.simboot.listener.ListenerData
import love.forte.simbot.event.EventListenerRegistrar

/**
 *
 * [ListenerAnnotationProcessor] 基础实现类, 用于通过 [ListenerData] 解析并注册 [love.forte.simbot.event.EventListener].
 *
 * TODO 合并 过滤器处理器和监听函数拦截器处理器到当前处理器中：
 *  过滤器与拦截器都应该深度服务监听函数。
 *
 * @author ForteScarlet
 */
internal class ListenerAnnotationProcessorImpl : ListenerAnnotationProcessor {

    override fun process(listenerData: ListenerData, listenerRegistrar: EventListenerRegistrar): Boolean {
        TODO("Not yet implemented")
    }
}