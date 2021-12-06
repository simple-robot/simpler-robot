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
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.withContext
import love.forte.simbot.Api4J
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.event.*
import love.forte.simbot.toCharSequenceID
import love.forte.simbot.utils.view
import java.util.concurrent.CompletableFuture


@Deprecated("Just use CoreListenerManager", ReplaceWith("love.forte.simbot.core.event.CoreListenerManager"))
public typealias CoreEventManager = CoreListenerManager


/**
 * 核心监听函数管理器。
 *
 * ## 调度
 * [CoreListenerManager] 遵守 [EventProcessor] 接口描述，通过当前事件中的 [bot][Event.bot] 所提供的作用域进行事件调度。
 *
 *
 * ## 异步函数
 * [CoreListenerManager] 中，对于一个异步函数 ([EventListener.isAsync] == true 的函数) 的处理方式与其接口定义的描述相同，
 * 对于这个异步函数的拦截器会与当前异步函数共同进入一个由 [事件bot][Event.bot] 所提供的异步任务中，并对当前的 [EventProcessingContext] 立即返回一个 [AsyncEventResult].
 *
 *
 *
 * ## 监听函数的解析、缓存与获取
 * 在 [CoreListenerManager] 中，真正被执行的监听函数是经过缓存与转化的，它们只会在遇到一个目前缓存中未知的事件类型的时候进行同步转化缓存。
 *
 * 对于 [CoreListenerManager] 内部的监听事件 [注册][register]、[获取][get]，其二者是使用相同锁对象（**当前对象自身**）的同步代码，但是对于内部的监听函数缓存，
 * 其只有在缓存被清空的时候才会去通过上述锁对象进行同步锁定，然后对目前已知的监听函数进行解析。
 *
 * 因此，对于使用 [get] 的时候，尽管它是同步的，但是这并不一定会影响到内部事件调度，因为缓存在存在的情况下是不受锁的影响的。
 *
 * 而对于 [register], 由于它在注册的时候同样会使用当前对象进行锁定，因此如果存在多个需要注册的监听函数，最好在循环体外部锁住当前对象以防止监听函数缓存频繁争夺同步锁。
 *
 */
public class CoreListenerManager private constructor(
    configuration: CoreEventManagerConfiguration
) : EventListenerManager {

    public companion object {
        @JvmStatic
        public fun newInstance(configuration: CoreEventManagerConfiguration): CoreListenerManager =
            CoreListenerManager(configuration)
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
    private val listeners: MutableMap<CharSequenceID, EventListener> = LinkedHashMap()

    /**
     * 完成缓存与处理的监听函数队列.
     */
    private val resolvedInvokers: MutableMap<Event.Key<*>, List<ListenerInvoker>> = LinkedHashMap()


    private fun getInvokers(type: Event.Key<*>): List<ListenerInvoker> {
        val cached = resolvedInvokers[type]
        if (cached != null) return cached
        synchronized(this) {
            val entrant = resolvedInvokers[type]
            if (entrant != null) return entrant
            // 计算缓存
            val compute = listeners.values
                .filter { it.isTarget(type) }
                .map(::ListenerInvoker)
                .sortedWith { o1, o2 ->
                    if (o1.isAsync == o2.isAsync) {
                        o1.listener.priority.compareTo(o2.listener.priority)
                    } else {
                        if (o1.isAsync) 1 else 0
                    }
                }.ifEmpty { emptyList() }
            resolvedInvokers[type] = compute
            return compute
        }
    }

    /**
     * 注册一个监听函数。
     *
     * 每次注册监听函数都会直接清空缓存。
     *
     */
    override fun register(listener: EventListener) {
        synchronized(this) {
            val id = listener.id.toCharSequenceID()
            listeners.compute(id) { _, old ->
                if (old != null) throw IllegalStateException("The event listener with ID $id already exists")
                listener.also {
                    resolvedInvokers.clear()
                }
            }
        }
    }

    /**
     * 获取一个监听函数。
     */
    override fun get(id: ID): EventListener? = synchronized(this) { listeners[id.toCharSequenceID()] }

    /**
     * 判断指定事件类型在当前事件管理器中是否能够被执行（存在任意对应的监听函数）。
     */
    public operator fun contains(eventType: Event.Key<*>): Boolean {
        return getInvokers(eventType).isNotEmpty()
    }

    override fun isProcessable(eventKey: Event.Key<*>): Boolean {
        return getInvokers(eventKey).isNotEmpty()
    }

    /**
     * 推送一个事件。
     */
    override suspend fun push(event: Event): EventProcessingResult {
        val invokers = getInvokers(event.key)
        if (invokers.isEmpty()) return EventProcessingResult

        return doInvoke(resolveToContext(event, invokers.size), invokers)
    }

    @Api4J
    override fun pushAsync(event: Event): CompletableFuture<EventProcessingResult> {
        val invokers = getInvokers(event.key)
        if (invokers.isEmpty()) return  CompletableFuture<EventProcessingResult>().also {
            it.complete(EventProcessingResult)
        }


        val scope: CoroutineScope = event.bot
        val deferred = scope.async { doInvoke(resolveToContext(event, invokers.size), invokers) }
        return deferred.asCompletableFuture()
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
 * 事件流程上下文的管理器，[CoreListenerManager] 通过此接口实例完成对 [EventProcessingContext] 的统一管理。
 *
 *  在 [CoreListenerManager] 中仅会使用同一个 [EventProcessingContextResolver] 实例。
 *
 * @sample CoreEventProcessingContextResolver
 */
public interface EventProcessingContextResolver<C : EventProcessingContext> {

    /**
     * 根据一个事件得到对应的流程上下文。
     * 只有在对应事件存在至少一个对应的监听函数的时候才会被触发。
     */
    @JvmSynthetic
    public suspend fun resolveEventToContext(event: Event, listenerSize: Int): C

    /**
     * 向提供的上下文 [C] 的 [EventProcessingContext.results] 中追加一个 [EventResult].
     *
     * [CoreListenerManager] 会对所有得到的结果进行尝试推送，包括 [EventResult.Invalid],
     * 但是建议不会真正的添加 [EventResult.Invalid].
     *
     */
    @JvmSynthetic
    public suspend fun appendResultIntoContext(context: C, result: EventResult)
}


/**
 * 核心默认的事件上下文处理器。
 */
internal object CoreEventProcessingContextResolver : EventProcessingContextResolver<CoreEventProcessingContext> {
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


internal class CoreEventProcessingContext(
    override val event: Event,
    resultInit: () -> MutableList<EventResult>
) : EventProcessingContext {

    @Suppress("PropertyName")
    @JvmSynthetic
    val _results = resultInit()

    override val results: List<EventResult> = _results.view()

}


private data class CoreEventProcessingResult(override val results: List<EventResult>) : EventProcessingResult