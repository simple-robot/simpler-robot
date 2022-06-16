package love.forte.simboot.annotation

import love.forte.simbot.Api4J
import love.forte.simbot.event.*
import javax.inject.Singleton

/**
 *
 */
public interface AnnotationEventFilterFactory {
    // TODO
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
    
    
    /**
     * 在此实例被获取/构建之后、应用之前被调用, 用于通过监听函数实例和对应的过滤器注解参数来对当前过滤器进行初始化。
     *
     * ## 使用
     * [AnnotationEventFilter] 应用于 [love.forte.simboot.annotation.Filter.by] 中，具体使用描述参考其文档注释。
     * ```kotlin
     * @Filter(by = FooAnnotationEventFilter::class)
     * suspend fun Event.onEvent() { ... }
     * ```
     *
     * ## 实例获取
     *
     * 如果当前过滤器存在于依赖管理之下并且作为唯一实例使用，则初始化函数 [init] 可能会执行多次；
     * 如果当前过滤器为 `object` 类型，[init] 同样可能会执行多次。
     * ```kotlin
     * object FooAnnotationEventFilter : AnnotationEventFilter {
     *      // ...
     * }
     * class BarAnnotationEventFilter : AnnotationEventFilter {
     *      // ...
     * }
     * ```
     *
     * 如果当前过滤器不存在于任何依赖管理中并且为普通的类，则会为每一个监听函数构建一个唯一的实例,
     * 此时 [init] 将会只执行一次。
     *
     *
     *
     * 假如 [AnnotationEventFilter] 的实现不是一个 `object` 类型，但是希望对所有的监听函数来讲
     * 其实例唯一，那么在使用的此类类型上标记 [Singleton]。标记了 [Singleton] 的普通实现，
     * 会在构造一次实例后进行缓存，保证所有监听函数对应的实例唯一。
     * 此时 [init] 将可能执行多次。
     * ```kotlin
     * @javax.inject.Singleton
     * class FooAnnotationEventFilter : AnnotationEventFilter {
     *      // ...
     * }
     * ```
     *
     * 除了交由依赖容器所管理的类型之外，所有要使用的实现类型必须保证存在一个公开无参的构造函数用于进行实例化。
     *
     * @return 此监听函数初始化后的整合类型
     *
     * @see Filter.by
     * @see InitType
     *
     */
    public fun init(listener: EventListener, filter: Filter, filters: Filters)
    
    
    /**
     * [AnnotationEventFilter] 初始化后, 用于代表当前过滤器针对于当前监听函数的状态类型。
     *
     * @see AnnotationEventFilter.init
     */
    @Deprecated("Unused")
    public enum class InitType {
        INDEPENDENT,
        UNITED
    }
    
    
    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
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