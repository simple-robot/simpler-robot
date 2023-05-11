/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.core.event

import love.forte.simbot.Attribute
import love.forte.simbot.AttributeContainer
import love.forte.simbot.event.Event
import love.forte.simbot.event.Event.Key.Companion.isSub
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult


/**
 *
 * 核心模块中所提供的最基础的 [EventListener] 实现。
 *
 * [SimpleListener] 实现 [EventListener] 所有的基础功能。
 *
 * @author ForteScarlet
 */
public class SimpleListener(
    /**
     * 当前监听函数所需的事件目标类型集。当 [targets] 的元素为空时，视为监听全部。
     */
    private val targets: Set<Event.Key<*>>,
    
    /**
     * 内部使用的属性容器。
     */
    private val attributes: AttributeContainer? = null,
    
    /**
     * 过滤匹配函数。默认为始终放行。
     */
    internal val matcher: suspend EventListenerProcessingContext.() -> Boolean = { true },
    
    /**
     * 实际的监听函数。
     */
    internal val function: suspend EventListenerProcessingContext.() -> EventResult,
    
    ) : EventListener {
    private val targetMatcher = targets.toTargetMatcher()
    
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? = attributes?.getAttribute(attribute)
    
    override fun isTarget(eventType: Event.Key<*>): Boolean = targetMatcher(eventType)
    
    override suspend fun match(context: EventListenerProcessingContext): Boolean = context.matcher()
    
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult = context.function()
    
    override fun toString(): String {
        return "SimpleListener(targets=$targets)@${hashCode()}"
    }
    
    /**
     * 并构建一个新的 [SimpleListener].
     */
    public fun copy(
        targets: Set<Event.Key<*>> = this.targets.toSet(),
        attributes: AttributeContainer? = null,
        matcher: suspend EventListenerProcessingContext.() -> Boolean = this.matcher,
        function: suspend EventListenerProcessingContext.() -> EventResult = this.function,
    ): SimpleListener {
        return SimpleListener(targets, attributes, matcher, function)
    }
    
    public companion object
}

private fun Set<Event.Key<*>>.toTargetMatcher(): ((Event.Key<*>) -> Boolean) {
    if (isEmpty()) {
        return { true }
    }
    if (size == 1) {
        val target = first()
        return { it isSub target }
    }
    
    val targets = toSet()
    return {
        it in targets || targets.any { t -> it isSub t }
    }
}
