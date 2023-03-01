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

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.BlockingFilter
import love.forte.simbot.Filter
import love.forte.simbot.PriorityConstant
import love.forte.simbot.utils.runWithInterruptible

/**
 * 事件过滤器。
 *
 * 事件过滤器一般与监听函数绑定，为简化监听函数相似的过滤条件服务。
 *
 * [EventFilter] 是 [EventListener] 的一种辅助特性，并非独立的机制存在。
 *
 * 通常情况下，一个 [EventListener] 内可能有隐式的多个filter，并在filter流程任意节点出现false的时候得到一个默认返回值.
 * 对于此接口的直接运用，常见的为在匹配失败的时候直接返回一个 [无效响应][EventResult.Invalid]。
 *
 * 过滤器存在 [优先级][priority], 默认情况下的优先级为 [PriorityConstant.NORMAL].
 *
 * 对于不支持挂起函数的实现者，可参考 [BlockingEventFilter]。
 *
 * @author ForteScarlet
 */
public interface EventFilter : Filter<EventListenerProcessingContext> {
    
    /**
     * 优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @JvmSynthetic
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun test(context: EventListenerProcessingContext): Boolean
    
}


/**
 * 事件过滤器。
 * 为不支持挂起函数（例如Java）的场景而提供的非挂起阻塞过滤器。
 *
 * @see EventFilter
 */
@Api4J
public interface BlockingEventFilter : EventFilter, BlockingFilter<EventListenerProcessingContext> {
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @Api4J
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun testBlocking(context: EventListenerProcessingContext): Boolean
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @JvmSynthetic
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun test(context: EventListenerProcessingContext): Boolean =
        runWithInterruptible { testBlocking(context) }
    
}
