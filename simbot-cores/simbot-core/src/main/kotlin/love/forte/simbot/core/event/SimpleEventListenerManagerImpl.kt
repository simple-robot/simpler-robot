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

package love.forte.simbot.core.event

import kotlinx.coroutines.*
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.*
import love.forte.simbot.core.scope.SimpleScope
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListener
import love.forte.simbot.utils.ListView
import love.forte.simbot.utils.view
import org.slf4j.Logger
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.CoroutineContext

internal class SimpleEventListenerManagerImpl internal constructor(
    configuration: SimpleListenerManagerConfiguration,
) : SimpleEventListenerManager {
    private companion object {
        private val counter: AtomicInteger = AtomicInteger(0)
        private val logger: Logger =
            LoggerFactory.getLogger("love.forte.simbot.core.event.SimpleEventListenerManagerImpl")
    }
    
    private val managerCoroutineContext: CoroutineContext
    private val managerScope: CoroutineScope
    
    /**
     * 异常处理器。 TODO
     */
    private val listenerExceptionHandler: ((Throwable) -> EventResult)?
    
    
    /**
     * 事件过程拦截器入口。
     */
    private val processingInterceptEntrance: EventInterceptEntrance<EventProcessingInterceptor.Context, EventProcessingResult, EventProcessingContext>
    
    /**
     * 监听函数拦截器集。
     */
    private val listenerIntercepts: List<EventListenerInterceptor>
    
    // /**
    //  * 监听函数列表。ID唯一
    //  */
    // private val _listeners: MutableMap<String, EventListener>
    
    // /**
    //  * 完成缓存与处理的监听函数队列.
    //  */
    // private val resolvedInvokers: MutableMap<Event.Key<*>, List<ListenerInvoker>> = LinkedHashMap()
    
    
    
    /**
     * 当前被注册的监听函数集.
     */
    private val invokers = PriorityListenerInvokers()
    
    /**
     * 事件是否可用缓存.
     */
    private val keyProcessableCache = EventKeyProcessableCache()
    
    
    private inner class EventKeyProcessableCache {
        private val cache = ConcurrentHashMap<Event.Key<*>, Boolean>()
        operator fun contains(key: Event.Key<*>): Boolean {
            return cache.compute(key) { k, curr ->
                curr ?: checkKeyInListeners(k)
            } ?: false
        }
        
        private fun checkKeyInListeners(key: Event.Key<*>): Boolean {
            return invokers.values.any { v -> v.queue.any { it.listener.isTarget(key) } }
        }
        fun removeCacheByListener(listener: EventListener) {
            cache.keys.removeIf(listener::isTarget)
        }
    }
    
    private inner class QueueListenerInvokerContainer(val queue: ConcurrentLinkedQueue<ListenerInvoker>) :
        ListenerInvokerContainer {
        override fun remove(invoker: ListenerInvoker): Boolean {
            return queue.remove(invoker).also {
                keyProcessableCache.removeCacheByListener(invoker.listener)
            }
        }
        
        override fun contains(invoker: ListenerInvoker): Boolean {
            return invoker in queue
        }
    }
  
    
    
    private inner class PriorityListenerInvokers(private val map: ConcurrentSkipListMap<Int, QueueListenerInvokerContainer> = ConcurrentSkipListMap()) :
        ConcurrentMap<Int, QueueListenerInvokerContainer> by map {
        private fun resolveQueue(priority: Int): QueueListenerInvokerContainer =
            map.computeIfAbsent(priority) { QueueListenerInvokerContainer(ConcurrentLinkedQueue()) }
        
        fun addListener(description: EventListenerRegistrationDescription): ListenerInvoker {
            val container = resolveQueue(description.priority)
            
            return ListenerInvoker(
                listener = description.listener,
                listenerIntercepts = listenerIntercepts,
                isAsync = description.isAsync,
                container
            ).also { container.queue.add(it) }
        }
        
        fun addListener(listener: EventListener): ListenerInvoker {
            val container = resolveQueue(EventListenerRegistrationDescription.DEFAULT_PRIORITY)
            
            return ListenerInvoker(
                listener = listener,
                listenerIntercepts = listenerIntercepts,
                isAsync = EventListenerRegistrationDescription.DEFAULT_ASYNC,
                container
            ).also { container.queue.add(it) }
        }
        
        
        fun invokerSequence(): Sequence<ListenerInvoker> = map.values.asSequence().flatMap { it.queue.asSequence() }
        
        /**
         *
         * @param block break if true.
         */
        inline fun forEach(filter: (ListenerInvoker) -> Boolean, block: (ListenerInvoker) -> Boolean) {
            h@ for (container in map.values) {
                for (invoker in container.queue) {
                    if (filter(invoker) && block(invoker)) {
                        break@h
                    }
                }
            }
        }
    }
    
    
    init {
        val simpleListenerManagerConfig: SimpleListenerManagerConfig = configuration.build()
        val context = simpleListenerManagerConfig.coroutineContext
        
        managerCoroutineContext =
            context.minusKey(Job) + CoroutineName("SimpleListenerManager#${counter.getAndIncrement()}")
        
        managerScope = CoroutineScope(managerCoroutineContext)
        
        listenerExceptionHandler = simpleListenerManagerConfig.exceptionHandler
        
        processingInterceptEntrance =
            EventInterceptEntrance.eventProcessingInterceptEntrance(simpleListenerManagerConfig.processingInterceptors.values.sortedBy { it.priority })
        
        listenerIntercepts = simpleListenerManagerConfig.listenerInterceptors.values.sortedBy { it.priority }
        
        initListener(simpleListenerManagerConfig.listeners)
    }
    
    @OptIn(ExperimentalSimbotApi::class)
    private fun initListener(listeners: List<EventListenerRegistrationDescription>) {
        listeners.forEach(::register)
    }
    
    // private fun getInvokers(type: Event.Key<*>): List<ListenerInvoker> {
    //     val cached = resolvedInvokers[type]
    //     if (cached != null) return cached
    //     synchronized(this) {
    //         val entrant = resolvedInvokers[type]
    //         if (entrant != null) return entrant
    //         // 计算缓存
    //         val compute = _listeners.values
    //             .filter { it.isTarget(type) }
    //             .map(::ListenerInvoker)
    //             .sortedWith { o1, o2 ->
    //                 if (o1.isAsync == o2.isAsync) {
    //                     o1.listener.priority.compareTo(o2.listener.priority)
    //                 } else {
    //                     if (o1.isAsync) 1 else 0
    //                 }
    //             }.ifEmpty { emptyList() }
    //         resolvedInvokers[type] = compute
    //         return compute
    //     }
    // }
    
    
    /**
     * 注册一个监听函数。
     *
     * 每次注册监听函数都会直接清空缓存。
     *
     */
    @ExperimentalSimbotApi
    override fun register(listener: EventListener): EventListenerHandle {
        return invokers.addListener(listener)
    }
    
    @ExperimentalSimbotApi
    override fun register(registrationDescription: EventListenerRegistrationDescription): EventListenerHandle {
        return invokers.addListener(registrationDescription)
    }
    
    
    @ExperimentalSimbotApi
    override val listeners: Sequence<EventListener>
        get() = invokers.invokerSequence().map { it.listener }
    
    
    /**
     * 判断指定事件类型在当前事件管理器中是否能够被执行（存在任意对应的监听函数）。
     */
    override operator fun contains(eventType: Event.Key<*>): Boolean = eventType in keyProcessableCache
    
    override fun isProcessable(eventKey: Event.Key<*>): Boolean {
        return resolver.isProcessable(eventKey) || eventKey in this
    }
    
    /**
     * 推送一个事件。
     */
    override suspend fun push(event: Event): EventProcessingResult {
        if (event.key !in this) {
            if (resolver.isProcessable(event.key)) {
                resolver.resolveEventToContext(event, 0)
            }
            return EventProcessingResult
        }
        
        return resolveToContext(event, invokers.size)?.let {
            doInvoke(it)
        } ?: EventProcessingResult
        
    }
    
    
    /**
     * 切换到当前管理器中的调度器并触发对应事件的内容。
     */
    private suspend fun doInvoke(
        context: SimpleEventProcessingContext,
        // invokers: List<ListenerInvoker>,
    ): EventProcessingResult {
        val currentBot = context.event.bot
        return withContext(managerCoroutineContext + context) {
            kotlin.runCatching {
                processingInterceptEntrance.doIntercept(context) { processingContext ->
                    // do invoke with intercept
                    this@SimpleEventListenerManagerImpl.invokers.forEach({ it.listener.isTarget(context.event.key) }) fe@{ invoker ->
                        val listenerContext = processingContext.withListener(invoker.listener, invoker)
                        val handleResult = runForEventResultWithHandler {
                            // maybe scope use bot?
                            invoker(managerScope, listenerContext)
                        }
                        val result = if (handleResult.isFailure) {
                            if (logger.isErrorEnabled) {
                                val err = handleResult.exceptionOrNull() as Throwable
                                logger.error(
                                    "Listener [{}] process failed: {}",
                                    invoker.listener,
                                    err.localizedMessage,
                                    err
                                )
                            }
                            EventResult.invalid()
                        } else {
                            handleResult.getOrNull()!!
                        }
                        
                        // append result
                        appendResult(context, result) == ListenerInvokeType.TRUNCATED
                    }
                    
                    // for (invoker in invokers) {
                    //     val listenerContext = processingContext.withListener(invoker.listener, invoker)
                    //     val handleResult = runForEventResultWithHandler {
                    //         // maybe scope use bot?
                    //         invoker(managerScope, listenerContext)
                    //     }
                    //     val result = if (handleResult.isFailure) {
                    //         if (logger.isErrorEnabled) {
                    //             val err = handleResult.exceptionOrNull() as Throwable
                    //             logger.error(
                    //                 "Listener [{}] process failed: {}",
                    //                 invoker.listener,
                    //                 err.localizedMessage,
                    //                 err
                    //             )
                    //         }
                    //         EventResult.invalid()
                    //     } else {
                    //         handleResult.getOrNull()!!
                    //     }
                    //
                    //     // append result
                    //     val type = appendResult(context, result)
                    //     if (type == ListenerInvokeType.TRUNCATED) {
                    //         break
                    //     }
                    // }
                    
                    // resolve to processing result
                    SimpleEventProcessingResult(context.results)
                }
            }.getOrElse {
                currentBot.logger.error("Event process failed.", it)
                EventProcessingResult
            }
        }
    }
    
    private inline fun runForEventResultWithHandler(block: () -> EventResult): Result<EventResult> {
        val result = runCatching(block)
        if (result.isSuccess || listenerExceptionHandler == null) return result
        
        val exception = result.exceptionOrNull()!!
        
        val result0 = runCatching {
            listenerExceptionHandler!!.invoke(exception)
        }
        if (result0.isSuccess) return result0
        val ex2 = result0.exceptionOrNull()!!
        ex2.addSuppressed(exception)
        return Result.failure(ex2)
    }
    
    
    private val resolver: SimpleEventProcessingContextResolver = SimpleEventProcessingContextResolver(managerScope)
    
    
    @ExperimentalSimbotApi
    override val globalScopeContext: ScopeContext
        get() = resolver.globalContext
    
    @ExperimentalSimbotApi
    override val continuousSessionContext: ContinuousSessionContext
        get() = resolver.continuousSessionContext
    
    /**
     * 通过 [Event] 得到一个 [EventProcessingContext].
     */
    private suspend fun resolveToContext(event: Event, listenerSize: Int): SimpleEventProcessingContext? {
        return resolver.resolveEventToContext(event, listenerSize)
    }
    
    private suspend fun appendResult(context: SimpleEventProcessingContext, result: EventResult): ListenerInvokeType {
        return resolver.appendResultIntoContext(context, result)
    }
    
    
}


private class ListenerInvoker(
    val listener: EventListener,
    listenerIntercepts: List<EventListenerInterceptor>,
    isAsync: Boolean,
    @Volatile private var container: ListenerInvokerContainer?,
) : suspend (CoroutineScope, EventListenerProcessingContext) -> EventResult, EventListenerHandle {
    private val function: suspend (CoroutineScope, EventListenerProcessingContext) -> EventResult
    
    init {
        val listenerInterceptsPointMap =
            EnumMap<EventListenerInterceptor.Point, EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>>(
                EventListenerInterceptor.Point::class.java
            )
        
        listenerIntercepts.groupBy { interceptor -> interceptor.point }
            .mapValuesTo(listenerInterceptsPointMap) { (_, listenerInterceptsInCurrentPoint) ->
                EventInterceptEntrance.eventListenerInterceptEntrance(listenerInterceptsInCurrentPoint)
            }
        
        
        suspend fun runner(
            listener: EventListener,
            context: EventListenerProcessingContext,
        ): EventResult {
            val defaultEntrance = listenerInterceptsPointMap.getOrDefault(
                EventListenerInterceptor.Point.DEFAULT,
                EventInterceptEntrance.eventListenerInterceptEntrance()
            )
            val afterMatchEntrance = listenerInterceptsPointMap.getOrDefault(
                EventListenerInterceptor.Point.AFTER_MATCH,
                EventInterceptEntrance.eventListenerInterceptEntrance()
            )
            
            return defaultEntrance.doIntercept(context) { innerContext ->
                if (listener.match(innerContext)) {
                    afterMatchEntrance.doIntercept(innerContext) { afterInnerContext ->
                        listener.invoke(afterInnerContext)
                    }
                } else {
                    EventResult.Invalid
                }
            }
        }
        
        suspend fun asyncFunctionRunner(
            listener: EventListener,
            scope: CoroutineScope,
            context: EventListenerProcessingContext,
        ): EventResult {
            val asyncDeferred = scope.async {
                runner(listener, context)
            }
            asyncDeferred.start()
            return EventResult.async(asyncDeferred)
        }
        
        
        suspend fun suspendFunctionRunner(
            listener: EventListener,
            context: EventListenerProcessingContext,
        ): EventResult {
            return runner(listener, context)
        }
        
        
        val functionRunner: suspend (CoroutineScope, EventListenerProcessingContext) -> EventResult = if (isAsync) {
            { s, c -> asyncFunctionRunner(listener, s, c) }
        } else {
            { _, c -> suspendFunctionRunner(listener, c) }
        }
        
        function = functionRunner
    }
    
    override suspend fun invoke(scope: CoroutineScope, context: EventListenerProcessingContext): EventResult {
        return try {
            function(scope, context)
        } catch (listenerEx: EventListenerProcessingException) {
            throw listenerEx
        } catch (invocationEx: InvocationTargetException) {
            throw EventListenerProcessingException(invocationEx.targetException)
        } catch (anyEx: Throwable) {
            throw EventListenerProcessingException(anyEx)
        }
    }
    
    override fun dispose(): Boolean {
        if (container == null) return false
        val c = containerUpdater.getAndSet(this, null) ?: return false
        return c.remove(this)
    }
    
    override val isExists: Boolean
        get() = container?.contains(this) ?: false
    
    companion object {
        private val containerUpdater = AtomicReferenceFieldUpdater.newUpdater(
            ListenerInvoker::class.java,
            ListenerInvokerContainer::class.java,
            "container"
        )
    }
}

private interface ListenerInvokerContainer {
    fun remove(invoker: ListenerInvoker): Boolean
    fun contains(invoker: ListenerInvoker): Boolean
}


private data class SimpleEventProcessingResult(override val results: List<EventResult>) : EventProcessingResult


/**
 * 向当前的 [EventProcessingContext] 提供一个监听函数 [listener], 使其成为 [EventListenerProcessingContext].
 *
 * @param listener 监听函数
 * @receiver 事件处理上下文
 * @return 监听函数处理上下文
 */
private fun EventProcessingContext.withListener(
    listener: EventListener,
    listenerHandle: EventListenerHandle,
): EventListenerProcessingContext =
    CoreEventListenerProcessingContext(this, listener, listenerHandle)


private class CoreEventListenerProcessingContext(
    processingContext: EventProcessingContext,
    override val listener: EventListener,
    override val listenerHandle: EventListenerHandle,
) : EventListenerProcessingContext, EventProcessingContext by processingContext {
    override var textContent: String? = with(processingContext.event) {
        if (this is MessageEvent) messageContent.plainText else null
    }
}


@OptIn(ExperimentalSimbotApi::class)
internal class SimpleEventProcessingContext(
    override val event: Event,
    override val messagesSerializersModule: SerializersModule,
    private val globalScopeContext: GlobalScopeContext,
    private val continuousSessionContext: SimpleContinuousSessionContext,
    resultInitSize: Int,
) : EventProcessingContext {
    
    private val _results = ArrayList<EventResult>(resultInitSize)
    
    @Volatile
    private var resultView: ListView<EventResult>? = null
    
    override val results: List<EventResult> // = _results.view()
        get() {
            // dont care sync
            return resultView ?: _results.view().also {
                resultView = it
            }
        }
    
    internal fun addResult(result: EventResult) {
        _results.add(result)
    }
    
    private lateinit var instantScope0: InstantScopeContext
    private val instantScope: InstantScopeContext
        get() {
            if (::instantScope0.isInitialized) {
                return instantScope0
            }
            return synchronized(this) {
                if (::instantScope0.isInitialized) {
                    instantScope0
                } else {
                    SimpleEventProcessingContextResolver.InstantScopeContextImpl(
                        AttributeMutableMap(ConcurrentHashMap())
                    ).also {
                        instantScope0 = it
                    }
                }
            }
        }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? {
        return when (attribute) {
            SimpleScope.Global -> globalScopeContext as T
            SimpleScope.ContinuousSession -> continuousSessionContext as T
            else -> instantScope[attribute]
        }
    }
    
    override fun <T : Any> get(attribute: Attribute<T>): T? {
        return getAttribute(attribute)
    }
    
    override fun <T : Any> contains(attribute: Attribute<T>): Boolean {
        return when (attribute) {
            SimpleScope.Global -> true
            SimpleScope.ContinuousSession -> true
            else -> attribute in instantScope
        }
    }
    
    override fun size(): Int {
        return instantScope.size() + 2
    }
    
    override fun <T : Any> put(attribute: Attribute<T>, value: T): T? {
        return instantScope.put(attribute, value)
    }
    
    override fun <T : Any> merge(attribute: Attribute<T>, value: T, remapping: (T, T) -> T): T {
        return instantScope.merge(attribute, value, remapping)
    }
    
    override fun <T : Any> computeIfAbsent(attribute: Attribute<T>, mappingFunction: (Attribute<T>) -> T): T {
        return instantScope.computeIfAbsent(attribute, mappingFunction)
    }
    
    override fun <T : Any> computeIfPresent(attribute: Attribute<T>, remappingFunction: (Attribute<T>, T) -> T?): T? {
        return instantScope.computeIfPresent(attribute, remappingFunction)
    }
    
    override fun <T : Any> remove(attribute: Attribute<T>): T? {
        return instantScope.remove(attribute)
    }
}

