/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
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
import love.forte.simbot.utils.runWithInterruptible

/**
 *
 * 通用监听函数接口，是在 boot-core 下解析生成的各类监听函数的统一标准接口。
 *
 * 所有通过扫描函数而得到的监听函数，其都建议使用可挂起的(`suspend`) 函数作为执行体。
 * 对于那些非可挂起的监听函数（普通函数、Java函数等），函数的执行逻辑 (KFunction.call(...)) 将会在 [runWithInterruptible] 中，默认以 [kotlinx.coroutines.Dispatchers.IO] 作为调度器被执行。
 *
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

