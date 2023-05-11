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

package love.forte.simboot.annotation

import love.forte.simbot.Api4J
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.*
import love.forte.simbot.utils.runWithInterruptible

/**
 * 应用于 [@Filter][love.forte.simboot.annotation.Filter] 注解上的
 * [Filter.by][love.forte.simboot.annotation.Filter.by] 参数，用于
 * 通过参数构建一个当前 `Filter` 对应的过滤器实例。
 *
 * ```kotlin
 * @Filter(by = FooAnnotationEventFilterFactory::class)
 * suspend fun Event.onEvent() { ... }
 * ```
 *
 * @author ForteScarlet
 */
public interface AnnotationEventFilterFactory {
    
    
    /**
     * 通过提供的监听函数和过滤器注解参数来解析并得到一个 [EventFilter] 实例。
     *
     * 如果需要跳过本次解析，可以直接返回一个 null。
     *
     * @see Filter.by
     *
     */
    public fun resolveFilter(
        listener: EventListener,
        listenerAttributes: MutableAttributeMap,
        filter: Filter,
        filters: Filters,
    ): EventFilter?
    
}


/**
 * 应用于 [love.forte.simboot.annotation.Filter] 注解上用来直接处理对应注解的函数。
 *
 * 非挂起的阻塞实现参考 [BlockingAnnotationEventFilter].
 *
 * @see BlockingAnnotationEventFilter
 * @author ForteScarlet
 */
@Deprecated("TODO")
public interface AnnotationEventFilter : EventFilter {
    
    public fun init(listener: EventListener, filter: Filter, filters: Filters)
    
    
    @Deprecated("TODO")
    public enum class InitType {
        INDEPENDENT,
        UNITED
    }
    
    
    override suspend fun test(context: EventListenerProcessingContext): Boolean
    
}


/**
 * 应用于 [love.forte.simboot.annotation.Filter] 注解上用来直接处理对应注解的函数。
 *
 * 是阻塞的 [AnnotationEventFilter] 类型实现，主要用于为不支持挂起函数的实现方使用。
 *
 * @see AnnotationEventFilter
 *
 */
@Suppress("DEPRECATION")
@Deprecated("TODO")
@Api4J
public interface BlockingAnnotationEventFilter : AnnotationEventFilter, BlockingEventFilter {
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @Api4J
    override fun testBlocking(context: EventListenerProcessingContext): Boolean
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @JvmSynthetic
    override suspend fun test(context: EventListenerProcessingContext): Boolean =
        runWithInterruptible { testBlocking(context) }
    
    
}
