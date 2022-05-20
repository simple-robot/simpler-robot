package love.forte.simbot.core.event

import love.forte.simbot.*
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventProcessingResult
import love.forte.simbot.event.EventResult

// region 拦截器相关
@DslMarker
internal annotation class EventInterceptorsGeneratorDSL


/**
 * 事件拦截器的生成/构建器，用于 [CoreListenerManagerConfiguration] 中。
 *
 * ### 事件流程拦截器
 * 你可以通过 [processingIntercept] 等相关函数提供针对整个事件流程的拦截器.
 * ```kotlin
 * // Kotlin
 * processingIntercept {
 *     // do..
 *
 *     it.proceed()
 * }
 *
 * processingIntercept(id = randomID(), priority = PriorityConstant.FIRST) {
 *     // do..
 *
 *     it.proceed()
 * }
 * ```
 *
 * ```java
 * // Java
 *  generator
 *         .processingIntercept(Identifies.randomID(), PriorityConstant.FIRST, context -> {
 *             // do...
 *             return context.proceedBlocking();
 *         })
 *         .processingIntercept(Identifies.randomID(), PriorityConstant.FIRST, context -> {
 *             // do...
 *             return context.proceedBlocking();
 *         })
 *         .end() // back to config
 *         // ...
 *         ;
 * ```
 *
 * ### 监听函数拦截器
 * 你可以通过 [listenerIntercept] 等相关函数提供针对每个监听函数的拦截器.
 * ```kotlin
 * // Kotlin
 * listenerIntercept {
 *     // do...
 *
 *     it.proceed()
 * }
 * listenerIntercept(id = randomID(), priority = PriorityConstant.FIRST) {
 *     // do...
 *     it.proceed()
 * }
 * ```
 *
 * ```java
 * // Java
 *  generator
 *         .listenerIntercept(Identifies.randomID(), PriorityConstant.FIRST, context -> {
 *             // do...
 *             return context.proceedBlocking();
 *         })
 *         .listenerIntercept(Identifies.randomID(), PriorityConstant.FIRST, context -> {
 *             // do...
 *             return context.proceedBlocking();
 *         })
 *         .end() // back to config
 *         // ...
 *         ;
 * ```
 *
 *
 */
@EventInterceptorsGeneratorDSL
public class EventInterceptorsGenerator @InternalSimbotApi constructor() {
    @Volatile
    private var _processingInterceptors = mutableIDMapOf<EventProcessingInterceptor>()
    
    public val processingInterceptors: Map<ID, EventProcessingInterceptor>
        get() = _processingInterceptors
    
    @Volatile
    private var _listenerInterceptors = mutableIDMapOf<EventListenerInterceptor>()
    
    public val listenerInterceptors: Map<ID, EventListenerInterceptor>
        get() = _listenerInterceptors
    
    
    @Synchronized
    private fun addLis(id: ID, interceptor: EventListenerInterceptor) {
        _listenerInterceptors.merge(id, interceptor) { _, _ ->
            throw IllegalStateException("Duplicate ID: $id")
        }
    }
    
    private fun addPro(id: ID, interceptor: EventProcessingInterceptor) {
        _processingInterceptors.merge(id, interceptor) { _, _ ->
            throw IllegalStateException("Duplicate ID: $id")
        }
    }
    
    
    /**
     * 提供一个 [id] 和 [拦截器][interceptor] 实例。
     */
    @EventInterceptorsGeneratorDSL
    public fun processingIntercept(
        id: ID = randomID(),
        interceptor: EventProcessingInterceptor,
    ): EventInterceptorsGenerator = also {
        addPro(id, interceptor)
    }
    
    /**
     * 提供一个 [id] 和 [拦截器][interceptor] 实例。
     */
    @EventInterceptorsGeneratorDSL
    public fun processingIntercept(
        id: String,
        interceptor: EventProcessingInterceptor,
    ): EventInterceptorsGenerator = also {
        addPro(id.ID, interceptor)
    }
    
    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction]，在内部构建一个拦截器。
     */
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun processingIntercept(
        id: ID = randomID(),
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult,
    ): EventInterceptorsGenerator = also {
        coreProcessingInterceptor(priority, interceptFunction).also {
            addPro(id, it)
        }
    }
    
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun processingIntercept(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult,
    ): EventInterceptorsGenerator = also { processingIntercept(id.ID, priority, interceptFunction) }
    
    /**
     * 提供一个 [id] 和 [拦截器][interceptor],
     * 向内部注册一个对应的拦截器。
     */
    @EventInterceptorsGeneratorDSL
    public fun listenerIntercept(
        id: ID = randomID(),
        interceptor: EventListenerInterceptor,
    ): EventInterceptorsGenerator = also {
        addLis(id, interceptor)
    }
    
    /**
     * 提供一个 [id] 和 [拦截器][interceptor],
     * 向内部注册一个对应的拦截器。
     */
    @EventInterceptorsGeneratorDSL
    public fun listenerIntercept(
        id: String,
        interceptor: EventListenerInterceptor,
    ): EventInterceptorsGenerator = also {
        addLis(id.ID, interceptor)
    }
    
    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 向内部注册一个对应的拦截器。
     */
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun listenerIntercept(
        id: ID = randomID(),
        point: EventListenerInterceptor.Point = EventListenerInterceptor.Point.DEFAULT,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult,
    ): EventInterceptorsGenerator = also {
        listenerIntercept(id, coreListenerInterceptor(point, priority, interceptFunction))
    }
    
    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun listenerIntercept(
        id: String,
        point: EventListenerInterceptor.Point = EventListenerInterceptor.Point.DEFAULT,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult,
    ): EventInterceptorsGenerator = also { listenerIntercept(id.ID, point, priority, interceptFunction) }
    
    
    
    internal fun build(
        useListenerIterators: (Map<ID, EventListenerInterceptor>) -> Unit,
        useProcessingIterators: (Map<ID, EventProcessingInterceptor>) -> Unit,
    ) {
        useListenerIterators(this.listenerInterceptors)
        useProcessingIterators(this.processingInterceptors)
        
    }
    
}


/**
 * 针对 [CoreListenerManagerConfiguration.addProcessingInterceptors] 的扩展函数。
 * ```
 * addProcessingInterceptors(
 *    randomID() to coreProcessingInterceptor { ... },
 *    randomID() to coreProcessingInterceptor { ... },
 *    randomID() to coreProcessingInterceptor { ... },
 *    randomID() to coreProcessingInterceptor { ... },
 * )
 * ```
 */
public fun CoreListenerManagerConfiguration.addProcessingInterceptors(vararg interceptors: Pair<ID, EventProcessingInterceptor>) {
    if (interceptors.isNotEmpty()) {
        addProcessingInterceptors(interceptors.toMap())
    }
}


/**
 * 针对 [CoreListenerManagerConfiguration.addListenerInterceptors] 的扩展函数。
 * ```
 * addListenerInterceptors(
 *    randomID() to coreListenerInterceptor { ... },
 *    randomID() to coreListenerInterceptor { ... },
 *    randomID() to coreListenerInterceptor { ... },
 *    randomID() to coreListenerInterceptor { ... },
 * )
 * ```
 */
public fun CoreListenerManagerConfiguration.addListenerInterceptors(vararg interceptors: Pair<ID, EventListenerInterceptor>) {
    if (interceptors.isNotEmpty()) {
        addListenerInterceptors(interceptors.toMap())
    }
}


// endregion

