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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.event.*
import love.forte.simbot.toCharSequenceID
import love.forte.simbot.utils.view
import java.util.concurrent.ConcurrentHashMap


/**
 * 核心监听函数管理器。
 */
public class CoreEventManager private constructor(
    configuration: CoreEventMangerConfiguration
) : EventListenerManager {

    public companion object {
        @JvmStatic // For java
        public fun newInstance(configuration: CoreEventMangerConfiguration): CoreEventManager =
            CoreEventManager(configuration)
    }

    /**
     * 事件过程拦截器入口。
     */
    private val processingInterceptEntrance =
        EventInterceptEntrance.eventProcessingInterceptEntrance(configuration.processingInterceptors.sortedBy { it.priority })

    /**
     * 监听函数拦截器集。
     */
    private val listenerIntercepts = configuration.listenerInterceptors.sortedBy { it.priority }

    /**
     * 监听函数列表。ID唯一
     */
    private val listeners: MutableMap<CharSequenceID, EventListener> = ConcurrentHashMap()

    /**
     * 完成缓存与处理的监听函数队列.
     */
    private val resolvedInvokers: MutableMap<Event.Key<*>, List<ListenerInvoker>> = ConcurrentHashMap()


    private fun getInvokers(type: Event.Key<*>): List<ListenerInvoker> {
        return resolvedInvokers.computeIfAbsent(type) { key ->
            // 计算缓存
            listeners.values
                .filter { it.isTarget(key) }
                .map(::ListenerInvoker)
                .sortedWith { o1, o2 ->
                    if (o1.isAsync == o2.isAsync) {
                        o1.listener.priority.compareTo(o2.listener.priority)
                    } else {
                        if (o1.isAsync) 1 else 0
                    }
                }.ifEmpty { emptyList() }
        }
    }

    /**
     * 注册一个监听函数。
     */
    override fun register(listener: EventListener) {
        val id = listener.id.toCharSequenceID()
        listeners.merge(id, listener) { _, _ ->
            throw IllegalStateException("The event listener with ID $id already exists")
        }
    }

    /**
     * 获取一个监听函数。
     */
    override fun get(id: ID): EventListener? = listeners[id.toCharSequenceID()]

    /**
     * 判断指定事件类型在当前事件管理器中是否能够被执行（存在任意对应的监听函数）。
     */
    public operator fun contains(eventType: Event.Key<*>): Boolean {
        return getInvokers(eventType).isNotEmpty()
    }

    /**
     * 推送一个事件。
     */
    override suspend fun push(event: Event): EventProcessingResult {
        val invokers = getInvokers(event.key)
        if (invokers.isEmpty()) return EventProcessingResult

        return doInvoke(resolveToContext(event, invokers.size), invokers)
    }


    /**
     * 切换到当前管理器中的调度器并触发对应事件的内容。
     */
    private suspend fun doInvoke(
        context: EventProcessingContext,
        invokers: List<ListenerInvoker>
    ): EventProcessingResult {
        val bot = context.event.bot
        val botContext = context.event.bot.coroutineContext

        return withContext(botContext + context) {
            processingInterceptEntrance.doIntercept(context) {
                // do invoke with intercept
                invokers.forEach { inv ->
                    val result = inv(bot, context)
                    // append result
                    appendResult(context, result)
                }

                // resolve to processing result
                CoreEventProcessingResult(context.results)
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private val resolver: EventProcessingContextResolver<EventProcessingContext> =
        configuration.eventProcessingContextResolver as EventProcessingContextResolver<EventProcessingContext>

    /**
     * 通过 [Event] 得到一个 [EventProcessingContext].
     */
    private suspend fun resolveToContext(event: Event, listenerSize: Int): EventProcessingContext {
        return resolver.resolveEventToContext(event, listenerSize)
    }

    private suspend fun appendResult(context: EventProcessingContext, result: EventResult) {
        resolver.appendResultIntoContext(context, result)
    }


    private inner class ListenerInvoker(
        val listener: EventListener,
    ) : suspend (CoroutineScope, EventProcessingContext) -> EventResult {
        val isAsync = listener.isAsync
        private val listenerInterceptEntrance =
            EventInterceptEntrance.eventListenerInterceptEntrance(listener, listenerIntercepts)

        private val function: suspend (CoroutineScope, EventProcessingContext) -> EventResult =
            if (listener.isAsync) { scope, context ->
                EventResult.async(scope.async { listenerInterceptEntrance.doIntercept(context, listener::invoke) })
            }
            else { _, context -> listenerInterceptEntrance.doIntercept(context, listener::invoke) }

        override suspend fun invoke(scope: CoroutineScope, context: EventProcessingContext): EventResult {
            return function(scope, context)
        }

    }

}


/**
 * 事件流程上下文的管理器，[CoreEventManager] 通过此接口实例完成对 [EventProcessingContext] 的统一管理。
 *
 *  在 [CoreEventManager] 中仅会使用同一个 [EventProcessingContextResolver] 实例。
 *
 * @sample CoreEventProcessingContextResolver
 */
public interface EventProcessingContextResolver<C : EventProcessingContext> {

    /**
     * 根据一个事件得到对应的流程上下文。
     * 只有在对应事件存在至少一个对应的监听函数的时候才会被触发。
     */
    public suspend fun resolveEventToContext(event: Event, listenerSize: Int): C

    /**
     * 向提供的上下文 [C] 的 [EventProcessingContext.results] 中追加一个 [EventResult].
     *
     * [CoreEventManager] 会对所有得到的结果进行尝试推送，包括 [EventResult.Invalid],
     * 但是建议不会真正的添加 [EventResult.Invalid].
     *
     */
    public suspend fun appendResultIntoContext(context: C, result: EventResult)
}


/**
 * 核心默认的事件上下文处理器。
 */
private object CoreEventProcessingContextResolver : EventProcessingContextResolver<CoreEventProcessingContext> {
    /**
     * 根据一个事件和当前事件对应的监听函数数量得到一个事件上下文实例。
     */
    override suspend fun resolveEventToContext(event: Event, listenerSize: Int): CoreEventProcessingContext {
        return CoreEventProcessingContext(event) { ArrayList(listenerSize) }
    }


    /**
     * 将一次事件结果拼接到当前上下文结果集中。
     */
    override suspend fun appendResultIntoContext(context: CoreEventProcessingContext, result: EventResult) {
        if (result != EventResult.Invalid) {
            context._results.add(result)
        }
    }

}


private class CoreEventProcessingContext(
    override val event: Event,
    resultInit: () -> MutableList<EventResult>
) : EventProcessingContext {

    @Suppress("PropertyName")
    @JvmSynthetic
    val _results = resultInit()

    override val results: List<EventResult> = _results.view()

}


private data class CoreEventProcessingResult(override val results: List<EventResult>) : EventProcessingResult