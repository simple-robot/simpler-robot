/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.core.event

import love.forte.simbot.ID
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventProcessingResult
import love.forte.simbot.event.EventResult
import java.util.*


@DslMarker
internal annotation class CoreEventManagerConfigDSL

/**
 * [CoreEventManager] 的配置文件.
 *
 * 当配置文件作为构建参数的时候，他会被立即使用。
 *
 */
@CoreEventManagerConfigDSL
public class CoreEventMangerConfiguration {
    @Volatile
    @JvmSynthetic
    internal var processingInterceptors =
        TreeSet<EventProcessingInterceptor>(Comparator.comparing { i -> i.id.toString() })

    @Volatile
    @JvmSynthetic
    internal var listenerInterceptors = TreeSet<EventListenerInterceptor>(Comparator.comparing { i -> i.id.toString() })


    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @Synchronized
    public fun addProcessingInterceptors(interceptors: Collection<EventProcessingInterceptor>) {
        val processingInterceptorsCopy =
            TreeSet<EventProcessingInterceptor>(Comparator.comparing { i -> i.id.toString() })
        processingInterceptorsCopy.addAll(processingInterceptors)
        for (interceptor in interceptors) {
            if (!processingInterceptorsCopy.add(interceptor)) {
                throw IllegalStateException("Duplicate ID: ${interceptor.id}")
            }
        }
        processingInterceptors = processingInterceptorsCopy
    }

    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @Synchronized
    public fun addListenerInterceptors(interceptors: Collection<EventListenerInterceptor>) {
        val listenerInterceptorsCopy = TreeSet<EventListenerInterceptor>(Comparator.comparing { i -> i.id.toString() })
        listenerInterceptorsCopy.addAll(listenerInterceptors)
        for (interceptor in interceptors) {
            if (!listenerInterceptorsCopy.add(interceptor)) {
                throw IllegalStateException("Duplicate ID: ${interceptor.id}")
            }
        }
        listenerInterceptors = listenerInterceptorsCopy
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
    public var eventProcessingContextResolver: EventProcessingContextResolver<*> = TODO()


}

@DslMarker
internal annotation class EventInterceptorsGeneratorDSL

@EventInterceptorsGeneratorDSL
public class EventInterceptorsGenerator {
    @Volatile
    private var _processingInterceptors: MutableSet<EventProcessingInterceptor> =
        TreeSet(Comparator.comparing { i -> i.id.toString() })

    public val processingInterceptors: Set<EventProcessingInterceptor>
        get() = _processingInterceptors

    @Volatile
    private var _listenerInterceptors: MutableSet<EventListenerInterceptor> =
        TreeSet(Comparator.comparing { i -> i.id.toString() })

    public val listenerInterceptors: Set<EventListenerInterceptor>
        get() = _listenerInterceptors


    @Synchronized
    private fun addLis(interceptor: EventListenerInterceptor) {
        if (!_listenerInterceptors.add(interceptor)) {
            throw IllegalStateException("Duplicate ID: ${interceptor.id}")
        }
    }

    private fun addPro(interceptor: EventProcessingInterceptor) {
        if (!_processingInterceptors.add(interceptor)) {
            throw IllegalStateException("Duplicate ID: ${interceptor.id}")
        }
    }

    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventProcessingInterceptor].
     */
    @JvmOverloads
    @EventInterceptorsGeneratorDSL
    public fun processing(
        id: ID,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
    ): EventProcessingInterceptor = processingInterceptor(id, priority, interceptFunction).also(::addPro)

    @JvmOverloads
    @EventInterceptorsGeneratorDSL
    public fun processing(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
    ): EventProcessingInterceptor = processing(id.ID, priority, interceptFunction)


    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmOverloads
    @EventInterceptorsGeneratorDSL
    public fun listener(
        id: ID,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
    ): EventListenerInterceptor =
        listenerInterceptor(id, priority, interceptFunction).also(::addLis)

    /**
     * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
     * 得到一个流程拦截器 [EventListenerInterceptor].
     */
    @JvmOverloads
    @EventInterceptorsGeneratorDSL
    public fun listener(
        id: String,
        priority: Int = PriorityConstant.NORMAL,
        interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
    ): EventListenerInterceptor =
        listener(id.ID, priority, interceptFunction)


}