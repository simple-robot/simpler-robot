package love.forte.simbot.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListener
import love.forte.simbot.utils.view
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

internal class CoreListenerManagerImpl internal constructor(
    configuration: CoreListenerManagerConfiguration,
) : CoreListenerManager {
    private companion object {
        private val counter: AtomicInteger = AtomicInteger(0)
    }
    
    private val managerCoroutineContext: CoroutineContext
    private val managerScope: CoroutineScope
    
    /**
     * 异常处理器。
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
     * 监听函数列表。ID唯一
     */
    private val listeners: MutableMap<CharSequenceID, EventListener>
    
    
    /**
     * 完成缓存与处理的监听函数队列.
     */
    private val resolvedInvokers: MutableMap<Event.Key<*>, List<ListenerInvoker>> = LinkedHashMap()
    
    
    init {
        val coreListenerManagerConfig: CoreListenerManagerConfig = configuration.build()
        val context = coreListenerManagerConfig.coroutineContext
        // TODO Job?
        // context.minusKey(Job) + CoroutineName("CoreListenerManager#${counter.getAndIncrement()}")
        
        managerCoroutineContext =
            context.minusKey(Job) + CoroutineName("CoreListenerManager#${counter.getAndIncrement()}")
        
        managerScope = CoroutineScope(managerCoroutineContext)
        
        listenerExceptionHandler = coreListenerManagerConfig.exceptionHandler
        
        processingInterceptEntrance =
            EventInterceptEntrance.eventProcessingInterceptEntrance(coreListenerManagerConfig.processingInterceptors.values.sortedBy { it.priority })
        
        listenerIntercepts = coreListenerManagerConfig.listenerInterceptors.values.sortedBy { it.priority }
        
        listeners = coreListenerManagerConfig.listeners.associateByTo(mutableMapOf()) { it.id.toCharSequenceID() }
    }
    
    
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
    @FragileSimbotApi
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
    override operator fun contains(eventType: Event.Key<*>): Boolean {
        return getInvokers(eventType).isNotEmpty()
    }
    
    override fun isProcessable(eventKey: Event.Key<*>): Boolean {
        return resolver.isProcessable(eventKey) || getInvokers(eventKey).isNotEmpty()
    }
    
    /**
     * 推送一个事件。
     */
    override suspend fun push(event: Event): EventProcessingResult {
        val invokers = getInvokers(event.key)
        if (invokers.isEmpty()) {
            if (resolver.isProcessable(event.key)) {
                resolver.resolveEventToContext(event, 0)
            }
            return EventProcessingResult
        }
        
        return doInvoke(resolveToContext(event, invokers.size), invokers)
    }
    
    @Api4J
    override fun pushAsync(event: Event): CompletableFuture<EventProcessingResult> {
        val invokers = getInvokers(event.key)
        if (invokers.isEmpty()) {
            managerScope.launch {
                if (resolver.isProcessable(event.key)) {
                    resolver.resolveEventToContext(event, 0)
                }
            }
            return CompletableFuture<EventProcessingResult>().also {
                it.complete(EventProcessingResult)
            }
        }
        
        
        val deferred = managerScope.async { doInvoke(resolveToContext(event, invokers.size), invokers) }
        return deferred.asCompletableFuture()
    }
    
    
    /**
     * 切换到当前管理器中的调度器并触发对应事件的内容。
     */
    private suspend fun doInvoke(
        context: CoreEventProcessingContext,
        invokers: List<ListenerInvoker>,
    ): EventProcessingResult {
        val currentBot = context.event.bot
        // val dispatchContext = currentBot.coroutineContext + managerCoroutineContext
        
        return withContext(managerCoroutineContext + context) {
            kotlin.runCatching {
                processingInterceptEntrance.doIntercept(context) { processingContext ->
                    // do invoke with intercept
                    for (invoker in invokers) {
                        val listenerContext = processingContext.withListener(invoker.listener)
                        val handleResult = runForEventResultWithHandler {
                            // maybe scope use bot?
                            invoker(managerScope, listenerContext)
                        }
                        val result = if (handleResult.isFailure) {
                            val err = handleResult.exceptionOrNull()
                            invoker.listener.logger.error(
                                "Listener process failed: $err",
                                err!!
                            )
                            EventResult.invalid()
                        } else {
                            handleResult.getOrNull()!!
                        }
                
                        // append result
                        val type = appendResult(context, result)
                        if (type == ListenerInvokeType.TRUNCATED) {
                            break
                        }
                    }
            
                    // resolve to processing result
                    CoreEventProcessingResult(context.results)
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
    
    
    private val resolver: CoreEventProcessingContextResolver = CoreEventProcessingContextResolver(managerScope)
    
    
    @ExperimentalSimbotApi
    override val globalScopeContext: ScopeContext
        get() = resolver.globalContext
    
    @ExperimentalSimbotApi
    override val continuousSessionContext: ContinuousSessionContext
        get() = resolver.continuousSessionContext
    
    /**
     * 通过 [Event] 得到一个 [EventProcessingContext].
     */
    private suspend fun resolveToContext(event: Event, listenerSize: Int): CoreEventProcessingContext {
        return resolver.resolveEventToContext(event, listenerSize)
    }
    
    private suspend fun appendResult(context: CoreEventProcessingContext, result: EventResult): ListenerInvokeType {
        return resolver.appendResultIntoContext(context, result)
    }
    
    
    internal inner class ListenerInvoker(
        val listener: EventListener,
    ) : suspend (CoroutineScope, EventListenerProcessingContext) -> EventResult {
        val isAsync = listener.isAsync
    
        // private val listenerInterceptEntranceWithPoint: Map<EventListenerInterceptor.Point, EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>>
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
        
    }
    
}



private data class CoreEventProcessingResult(override val results: List<EventResult>) : EventProcessingResult


/**
 * 向当前的 [EventProcessingContext] 提供一个监听函数 [listener], 使其成为 [EventListenerProcessingContext].
 *
 * @param listener 监听函数
 * @receiver 事件处理上下文
 * @return 监听函数处理上下文
 */
private fun EventProcessingContext.withListener(listener: EventListener): EventListenerProcessingContext =
    CoreEventListenerProcessingContext(this, listener)


private class CoreEventListenerProcessingContext(
    processingContext: EventProcessingContext,
    override val listener: EventListener,
) : EventListenerProcessingContext, EventProcessingContext by processingContext {
    override var textContent: String? = with(processingContext.event) {
        if (this is MessageEvent) messageContent.plainText else null
    }
}




@OptIn(ExperimentalSimbotApi::class)
internal class CoreEventProcessingContext(
    override val event: Event,
    override val messagesSerializersModule: SerializersModule,
    private val globalScopeContext: GlobalScopeContext,
    private val continuousSessionContext: SimpleContinuousSessionContext,
    private val instantScopeContextInitializer: () -> InstantScopeContext,
    private val attributeMap: AttributeMap,
    resultInitSize: Int,
) : EventProcessingContext {
    
    
    @Suppress("PropertyName")
    @JvmSynthetic
    val _results = ArrayList<EventResult>(resultInitSize)
    
    override val results: List<EventResult> = _results.view()
    
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
                    instantScopeContextInitializer().also {
                        instantScope0 = it
                    }
                }
            }
        }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? {
        return when (attribute) {
            @Suppress("DEPRECATION")
            EventProcessingContext.Scope.Instant,
            -> this as T
            EventProcessingContext.Scope.Global -> globalScopeContext as T
            EventProcessingContext.Scope.ContinuousSession -> continuousSessionContext as T
            else -> attributeMap[attribute]
        }
    }
    
    override fun <T : Any> get(attribute: Attribute<T>): T? {
        return getAttribute(attribute)
    }
    
    override fun <T : Any> contains(attribute: Attribute<T>): Boolean {
        return when (attribute) {
            @Suppress("DEPRECATION")
            EventProcessingContext.Scope.Instant,
            -> true
            EventProcessingContext.Scope.Global -> true
            EventProcessingContext.Scope.ContinuousSession -> true
            else -> attribute in attributeMap
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

