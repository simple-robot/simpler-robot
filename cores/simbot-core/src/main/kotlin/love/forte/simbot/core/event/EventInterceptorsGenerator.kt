package love.forte.simbot.core.event

import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.utils.*
import java.util.function.Function
//region 拦截器相关
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
public class EventInterceptorsGenerator @InternalSimbotApi constructor(private val end: (Map<ID, EventProcessingInterceptor>, Map<ID, EventListenerInterceptor>) -> CoreListenerManagerConfiguration) {
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
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventProcessingInterceptor].
     */
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun processingIntercept(
        id: ID = randomID(),
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
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
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
    ): EventInterceptorsGenerator = also { processingIntercept(id.ID, priority, interceptFunction) }

    @JvmOverloads
    @Api4J
    public fun processingIntercept(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: Function<EventProcessingInterceptor.Context, EventProcessingResult>
    ): EventInterceptorsGenerator = also {
        processingIntercept(id.ID, priority) {
            runWithInterruptible { interceptFunction.apply(it) }
        }
    }

    @JvmOverloads
    @Api4J
    public fun processingIntercept(
        id: ID = randomID(),
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: Function<EventProcessingInterceptor.Context, EventProcessingResult>
    ): EventInterceptorsGenerator = also {
        processingIntercept(id, priority) {
            runWithInterruptible { interceptFunction.apply(it) }
        }
    }


    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun listenerIntercept(
        id: ID = randomID(),
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
    ): EventInterceptorsGenerator = also {
        coreListenerInterceptor(priority, interceptFunction).also {
            addLis(id, it)
        }
    }

    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun listenerIntercept(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
    ): EventInterceptorsGenerator = also { listenerIntercept(id.ID, priority, interceptFunction) }


    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmOverloads
    @Api4J
    public fun listenerIntercept(
        id: ID = randomID(),
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: Function<EventListenerInterceptor.Context, EventResult>
    ): EventInterceptorsGenerator = also {
        listenerIntercept(id, priority) {
            runWithInterruptible { interceptFunction.apply(it) }
        }
    }


    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmOverloads
    @Api4J
    public fun listenerIntercept(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: Function<EventListenerInterceptor.Context, EventResult>
    ): EventInterceptorsGenerator = also {
        listenerIntercept(id, priority) {
            runWithInterruptible { interceptFunction.apply(it) }
        }
    }

    /**
     * 回到配置主类
     */
    public fun end(): CoreListenerManagerConfiguration = end(processingInterceptors, listenerInterceptors)

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
        addProcessingInterceptors(mapOf(*interceptors))
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
        addListenerInterceptors(mapOf(*interceptors))
    }
}


//endregion

