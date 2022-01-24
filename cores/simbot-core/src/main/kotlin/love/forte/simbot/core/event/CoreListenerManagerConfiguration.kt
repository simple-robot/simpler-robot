/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.core.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.*
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventProcessingResult
import love.forte.simbot.event.EventResult
import java.util.function.Function
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@DslMarker
internal annotation class CoreEventManagerConfigDSL

/**
 * [CoreListenerManager] 的配置文件.
 *
 * 当配置文件作为构建参数的时候，他会被立即使用。
 *
 */
@CoreEventManagerConfigDSL
public class CoreListenerManagerConfiguration {

    /**
     * 事件管理器的上下文. 可以基于此提供调度器。
     * 但是 [CoreListenerManager] 并不是一个作用域，因此不可以提供 `Job`.
     */
    @CoreEventManagerConfigDSL
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    @Volatile
    @JvmSynthetic
    internal var processingInterceptors = mutableIDMapOf<EventProcessingInterceptor>()

    @Volatile
    @JvmSynthetic
    internal var listenerInterceptors = mutableIDMapOf<EventListenerInterceptor>()


    /**
     * 自定义的监听函数异常处理器。
     *
     */
    @Volatile
    @JvmSynthetic
    internal var listenerExceptionHandler: ((Throwable) -> EventResult)? = null


    @JvmSynthetic
    @CoreEventManagerConfigDSL
    public fun listenerExceptionHandler(handler: (Throwable) -> EventResult) {
        listenerExceptionHandler = handler
    }

    @Api4J
    @CoreEventManagerConfigDSL
    public fun listenerExceptionHandler(handler: Function<Throwable, EventResult>) {
        listenerExceptionHandler = handler::apply
    }


    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @Synchronized
    @CoreEventManagerConfigDSL
    public fun addProcessingInterceptors(interceptors: Map<ID, EventProcessingInterceptor>) {
        val processingInterceptorsCopy = processingInterceptors.toMutableMap()

        for ((id, interceptor) in interceptors) {
            processingInterceptorsCopy.merge(id, interceptor) { _, _ ->
                throw IllegalStateException("Duplicate ID: $id")
            }
        }
        processingInterceptors = mutableIDMapOf(processingInterceptorsCopy)
    }

    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @Synchronized
    @CoreEventManagerConfigDSL
    public fun addListenerInterceptors(interceptors: Map<ID, EventListenerInterceptor>) {
        val listenerInterceptorsCopy = listenerInterceptors.toMutableMap()

        for ((id, interceptor) in interceptors) {
            listenerInterceptorsCopy.merge(id, interceptor) { _, _ ->
                throw IllegalStateException("Duplicate ID: $id")
            }
        }

        listenerInterceptors = mutableIDMapOf(listenerInterceptorsCopy)
    }

    @CoreEventManagerConfigDSL
    public fun interceptors(block: EventInterceptorsGenerator.() -> Unit) {
        val generated = EventInterceptorsGenerator().also(block)
        addListenerInterceptors(generated.listenerInterceptors)
        addProcessingInterceptors(generated.processingInterceptors)
    }


    /**
     * 事件流程上下文的处理器。
     */
    // 暂时不公开
    // @CoreEventManagerConfigDSL
    public var eventProcessingContextResolver: (manager: CoreListenerManager, scope: CoroutineScope) -> EventProcessingContextResolver<*> =
        { _, scope -> CoreEventProcessingContextResolver(scope) }
        internal set


}

@DslMarker
internal annotation class EventInterceptorsGeneratorDSL

@EventInterceptorsGeneratorDSL
public class EventInterceptorsGenerator {
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
    ): EventProcessingInterceptor = coreProcessingInterceptor(priority, interceptFunction).also {
        addPro(id, it)
    }

    @JvmSynthetic
    @EventInterceptorsGeneratorDSL
    public fun processingIntercept(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
    ): EventProcessingInterceptor = processingIntercept(id.ID, priority, interceptFunction)

    @JvmOverloads
    @Api4J
    public fun processingIntercept(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: Function<EventProcessingInterceptor.Context, EventProcessingResult>
    ): EventProcessingInterceptor = processingIntercept(id.ID, priority) {
        runInterruptible { interceptFunction.apply(it) }
    }

    @JvmOverloads
    @Api4J
    public fun processingIntercept(
        id: ID = randomID(),
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: Function<EventProcessingInterceptor.Context, EventProcessingResult>
    ): EventProcessingInterceptor = processingIntercept(id, priority) {
        runInterruptible { interceptFunction.apply(it) }
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
    ): EventListenerInterceptor =
        coreListenerInterceptor(priority, interceptFunction).also {
            addLis(id, it)
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
    ): EventListenerInterceptor =
        listenerIntercept(id.ID, priority, interceptFunction)



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
    ): EventListenerInterceptor =
        listenerIntercept(id, priority) {
            runInterruptible { interceptFunction.apply(it) }
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
    ): EventListenerInterceptor =
        listenerIntercept(id, priority) {
            runInterruptible { interceptFunction.apply(it) }
        }


}