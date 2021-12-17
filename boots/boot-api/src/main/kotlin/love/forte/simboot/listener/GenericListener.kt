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

import love.forte.simbot.ID
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult

/**
 *
 * 通用监听函数接口，是在 boot-core 下解析生成的各类监听函数的统一标准接口。
 *
 *
 * @author ForteScarlet
 */
public interface GenericBootEventListener : EventListener {
    override val id: ID
    override val isAsync: Boolean
    override fun isTarget(eventType: Event.Key<*>): Boolean
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult
}

