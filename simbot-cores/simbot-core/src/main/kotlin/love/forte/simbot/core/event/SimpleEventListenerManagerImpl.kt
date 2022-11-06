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
import love.forte.simbot.logger.LoggerFactory
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
import java.util.concurrent.atomic.LongAdder
import kotlin.coroutines.CoroutineContext

internal class SimpleEventListenerManagerImpl internal constructor(
    configuration: SimpleListenerManagerConfiguration,
) : SimpleEventListenerManager {
    private companion object {
        private val logger: Logger =
            LoggerFactory.getLogger("love.forte.simbot.core.event.SimpleEventListenerManagerImpl")
        
        private const val EVENT_KEY_PROCESSABLE_COUNTER_CLEAR_THRESHOLD = 83
    
        /**
         * 是否禁用产生内部使用的默认调度器。
         */
        private const val DISABLE_DEFAULT_DISPATCHER = "love.forte.simbot.core.SimpleListenerManager.disableDefaultDispatcher"
        
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
    
    /**
     * 当前被注册的监听函数集.
     */
    private val invokers = PriorityListenerInvokers()
    
    // @get:TestOnly
    // internal val handlesInternal: Sequence<EventListenerHandle> get() = invokers.invokerSequence()
    
    /**
     * 事件可用性计数器.
     */
    private val keyProcessableCounter = EventKeyProcessableCounter()
    
    /**
     * 事件可用性计数器.
     * 计数器只能提供一个粗略的统计数字, 无法保证绝对的强关联准确性, 用于为初始化context提供一个预期的容量或验证是否存在某个指定类型的事件.
     */
    private inner class EventKeyProcessableCounter {
        private val cache = ConcurrentHashMap<Event.Key<*>, LongAdder>()
        private val clearCount = AtomicInteger()
        
        /**
         * 计算或获取某个事件类型在当前监听函数管理器中的命中数量. 如果当前所记录的命中数量不足1,
         * 则本次计算会清理此条数据并等待下一次重新计算.
         *
         * 如果当前容器中没有此类型事件的已记录信息, 则会进行一次初始化计算.
         *
         */
        fun count(key: Event.Key<*>): Long {
            return cache.compute(key) { k, curr ->
                computeAdder(k, curr)
            }?.sum() ?: 0
        }
        
        private fun computeAdder(key: Event.Key<*>, current: LongAdder?): LongAdder? {
            if (current != null && current.sum() > 0) {
                return current
            }
            var count = 0L
            invokers.values.forEach { v ->
                v.queue.forEach {
                    if (it.listener.isTarget(key)) {
                        count++
                    }
                }
            }
            return if (count > 0L) return LongAdder().apply { add(count) } else null
        }
        
        private fun clearEmptyAdders() {
            cache.values.removeIf { it.sum() <= 0 }
        }
        
        private fun clearEmptyAddersDetection() {
            val value =
                clearCount.getAndUpdate { v -> if (v > EVENT_KEY_PROCESSABLE_COUNTER_CLEAR_THRESHOLD) 0 else v + 1 }
            if (value > EVENT_KEY_PROCESSABLE_COUNTER_CLEAR_THRESHOLD) {
                clearEmptyAdders()
            }
        }
        
        /**
         * 当移除某个监听函数时, 为所有可命中的计数器递减.
         * 不会检测递减后的结果是否已经不足1, 这会交给 [count] 进行.
         */
        fun removeCacheByListener(listener: EventListener) {
            cache.forEach { (k, adder) ->
                if (listener.isTarget(k)) {
                    adder.decrement()
                }
            }
            clearEmptyAddersDetection()
        }
    }
    
    private inner class QueueListenerInvokerContainer(val queue: ConcurrentLinkedQueue<ListenerInvoker>) :
        ListenerInvokerContainer {
        override fun remove(invoker: ListenerInvoker): Boolean {
            return queue.remove(invoker).also {
                keyProcessableCounter.removeCacheByListener(invoker.listener)
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
        
        private operator fun QueueListenerInvokerContainer.plusAssign(invoker: ListenerInvoker) {
            queue.add(invoker)
        }
        
        fun addListener(description: EventListenerRegistrationDescription): ListenerInvoker {
            val container = resolveQueue(description.priority)
            
            return ListenerInvoker(
                listener = description.listener,
                isAsync = description.isAsync,
                listenerIntercepts,
                container,
                this@SimpleEventListenerManagerImpl
            ).also { container += it }
        }
        
        fun addListener(listener: EventListener): ListenerInvoker {
            val container = resolveQueue(EventListenerRegistrationDescription.DEFAULT_PRIORITY)
            
            return ListenerInvoker(
                listener = listener,
                isAsync = EventListenerRegistrationDescription.DEFAULT_ASYNC,
                listenerIntercepts,
                container,
                this@SimpleEventListenerManagerImpl
            ).also { container += it }
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
        var context = simpleListenerManagerConfig.coroutineContext.minusKey(Job) + CoroutineName("SimpleListenerManager@${hashCode()}")
    
        @OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
        if (context[CoroutineDispatcher] == null) {
            if (System.getProperty(DISABLE_DEFAULT_DISPATCHER).toBoolean()) {
                // todo configurable
                context += Dispatchers.Default.limitedParallelism(Runtime.getRuntime().availableProcessors().coerceAtLeast(16))
            } else {
                logger.debug("No dispatcher for current simple listener manager, and default dispatcher is disabled.")
            }
        }
        
        managerCoroutineContext = context
        
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
    override operator fun contains(eventType: Event.Key<*>): Boolean = count(eventType) > 0
    
    internal fun count(eventType: Event.Key<*>): Long = keyProcessableCounter.count(eventType)
    
    override fun isProcessable(eventKey: Event.Key<*>): Boolean {
        return resolver.isProcessable(eventKey) || eventKey in this
    }
    
    /**
     * 推送一个事件。
     */
    override suspend fun push(event: Event): EventProcessingResult {
        val count = keyProcessableCounter.count(event.key)
        if (count <= 0) {
            if (resolver.isProcessable(event.key)) {
                resolver.resolveEventToContext(event, 0)
            }
            return EventProcessingResult
        }
        
        return resolveToContext(event, count.toInt())?.let {
            doInvoke(it)
        } ?: EventProcessingResult
        
    }
    
    
    /**
     * 切换到当前管理器中的调度器并触发对应事件的内容。
     */
    private suspend fun doInvoke(
        context: SimpleEventProcessingContext,
    ): EventProcessingResult {
        val currentBot = context.event.bot
        return withContext(managerCoroutineContext + context) {
            kotlin.runCatching {
                processingInterceptEntrance.doIntercept(context) { processingContext ->
                    // do invoke with intercept
                    this@SimpleEventListenerManagerImpl.invokers.forEach({ it.listener.isTarget(context.event.key) }) { invoker ->
                        val listenerContext = processingContext.withListener(invoker.listener, invoker)
                        val handleResult = runForEventResultWithHandler {
                            // maybe scope use bot?
                            invoker(managerScope, listenerContext)
                        }
                        val result = if (handleResult.isFailure) {
                            if (logger.isErrorEnabled) {
                                val err = handleResult.exceptionOrNull() as Throwable
                                logger.error(
                                    "Listener [{}] process failed: {}", invoker.listener, err.localizedMessage, err
                                )
                            }
                            EventResult.invalid()
                        } else {
                            handleResult.getOrNull()!!
                        }
                        
                        // append result
                        appendResult(context, result) == ListenerInvokeType.TRUNCATED
                    }
                    
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
    isAsync: Boolean,
    listenerIntercepts: List<EventListenerInterceptor>,
    @Volatile var invokerContainer: ListenerInvokerContainer?,
    override val container: EventListenerContainer,
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
        
        
        val functionRunner: suspend (CoroutineScope, EventListenerProcessingContext) -> EventResult = if (isAsync) {
            AsyncFunctionRunner(listenerInterceptsPointMap, listener)
        } else {
            SuspendFunctionRunner(listenerInterceptsPointMap, listener)
        }
        
        function = functionRunner
    }
    
    private abstract inner class AbstractFunctionRunner :
        suspend (CoroutineScope, EventListenerProcessingContext) -> EventResult {
        protected abstract val listenerInterceptsPointMap: EnumMap<EventListenerInterceptor.Point, EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>>
        protected suspend fun runner(
            listener: EventListener,
            context: EventListenerProcessingContext,
        ): EventResult {
            val defaultEntrance = listenerInterceptsPointMap.getOrDefault(
                EventListenerInterceptor.Point.DEFAULT, EventInterceptEntrance.eventListenerInterceptEntrance()
            )
            val afterMatchEntrance = listenerInterceptsPointMap.getOrDefault(
                EventListenerInterceptor.Point.AFTER_MATCH, EventInterceptEntrance.eventListenerInterceptEntrance()
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
    }
    
    private inner class AsyncFunctionRunner(
        override val listenerInterceptsPointMap: EnumMap<EventListenerInterceptor.Point, EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>>,
        private val listener: EventListener,
    ) : AbstractFunctionRunner() {
        override suspend fun invoke(scope: CoroutineScope, context: EventListenerProcessingContext): EventResult {
            return EventResult.async(scope.async(start = CoroutineStart.DEFAULT) {
                runner(listener, context)
            })
        }
    }
    
    private inner class SuspendFunctionRunner(
        override val listenerInterceptsPointMap: EnumMap<EventListenerInterceptor.Point, EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>>,
        private val listener: EventListener,
    ) : AbstractFunctionRunner() {
        override suspend fun invoke(scope: CoroutineScope, context: EventListenerProcessingContext): EventResult {
            return runner(listener, context)
        }
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
        return invokerContainer?.let { c ->
            if (invokerContainerUpdater.compareAndSet(this, c, null)) c.remove(this)
            else false
        } ?: false
    }
    
    override val isExists: Boolean
        get() = invokerContainer?.contains(this) ?: false
    
    companion object {
        private val invokerContainerUpdater: AtomicReferenceFieldUpdater<ListenerInvoker, ListenerInvokerContainer> =
            AtomicReferenceFieldUpdater.newUpdater(
                ListenerInvoker::class.java, ListenerInvokerContainer::class.java, "invokerContainer"
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
): EventListenerProcessingContext = CoreEventListenerProcessingContext(this, listener, listenerHandle)


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
    
    private val _results = ArrayList<EventResult>(resultInitSize.coerceAtLeast(1))
    
    private var resultView: ListView<EventResult>? = null
    
    override val results: List<EventResult>
        get() {
            // sync is not necessary, probably.
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
    
    override fun <T : Any> computeIfPresent(
        attribute: Attribute<T>,
        remappingFunction: (Attribute<T>, T) -> T?,
    ): T? {
        return instantScope.computeIfPresent(attribute, remappingFunction)
    }
    
    override fun <T : Any> remove(attribute: Attribute<T>): T? {
        return instantScope.remove(attribute)
    }
}

