package love.forte.simboot.annotation

import love.forte.simbot.Api4J
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.*

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
@Deprecated("Unused")
@Suppress("KDocUnresolvedReference")
public interface AnnotationEventFilter : EventFilter {
    
    public fun init(listener: EventListener, filter: Filter, filters: Filters)
    
    
    @Deprecated("Unused")
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
@Deprecated("Unused")
@Api4J
public interface BlockingAnnotationEventFilter : AnnotationEventFilter, BlockingEventFilter {
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @Api4J
    override fun testBlocking(): Boolean
    
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @JvmSynthetic
    override suspend fun test(context: EventListenerProcessingContext): Boolean = testBlocking()
    
    
}