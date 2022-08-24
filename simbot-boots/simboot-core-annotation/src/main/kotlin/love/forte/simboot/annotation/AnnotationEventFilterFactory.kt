/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
     * @suppress 使用 `testBlocking(EventListenerProcessingContext)`
     */
    @Api4J
    @Deprecated(
        "Use testBlocking(EventListenerProcessingContext)",
        ReplaceWith("testBlocking(context: EventListenerProcessingContext)")
    )
    override fun testBlocking(): Boolean = true
    
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