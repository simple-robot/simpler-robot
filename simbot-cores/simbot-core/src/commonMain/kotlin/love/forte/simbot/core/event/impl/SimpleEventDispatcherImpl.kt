/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("SimpleEventDispatcherImpls")
@file:JvmMultifileClass

package love.forte.simbot.core.event.impl

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.common.collection.PriorityConcurrentQueue
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.core.event.*
import love.forte.simbot.event.*
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * [SimpleEventDispatcherConfiguration] 的简单实现
 */
public open class SimpleEventDispatcherConfigurationImpl : AbstractEventDispatcherConfiguration(),
    SimpleEventDispatcherConfiguration {
    /**
     * 继承 [AbstractEventDispatcherConfiguration.interceptors] 并将其对外公开
     */
    public override val interceptors: MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>>
        get() = super.interceptors

    /**
     * 继承 [AbstractEventDispatcherConfiguration.dispatchInterceptors] 并将其对外公开
     */
    public override val dispatchInterceptors: MutableList<Pair<EventDispatchInterceptor, ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?>>
        get() = super.dispatchInterceptors
}

/**
 * [SimpleEventInterceptorRegistrationProperties] 的简单实现
 */
public open class SimpleEventInterceptorRegistrationPropertiesImpl : SimpleEventInterceptorRegistrationProperties {
    override var priority: Int = PriorityConstant.NORMAL
}

/**
 * [SimpleEventDispatchInterceptorRegistrationProperties] 的简单实现
 */
public open class SimpleEventDispatchInterceptorRegistrationPropertiesImpl :
    SimpleEventDispatchInterceptorRegistrationProperties {
    override var priority: Int = PriorityConstant.NORMAL
}

/**
 * [SimpleEventListenerRegistrationProperties] 的简单实现
 */
public class SimpleEventListenerRegistrationPropertiesImpl(private val interceptorBuilder: SimpleEventInterceptorsBuilder) :
    SimpleEventListenerRegistrationProperties {

    override var priority: Int = PriorityConstant.NORMAL

    override fun addInterceptor(
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
        interceptor: EventInterceptor
    ) {
        interceptorBuilder.addInterceptor(interceptor, propertiesConsumer)
    }
}

/**
 * 一个用于构建
 * [SimpleEventInterceptorInvoker] 和 [SimpleEventDispatchInterceptorInvoker]
 * 的简单构建器实现。
 */
public class SimpleEventInterceptorsBuilder {
    private val interceptors: MutableList<SimpleEventInterceptorInvoker> = mutableListOf()
    private val dispatchInterceptors: MutableList<SimpleEventDispatchInterceptorInvoker> = mutableListOf()

    public fun addInterceptor(
        interceptor: EventInterceptor,
        configurer: ConfigurerFunction<EventInterceptorRegistrationProperties>? = null
    ) {
        val prop = SimpleEventInterceptorRegistrationPropertiesImpl().also { configurer?.invokeWith(it) }
        val invoker = SimpleEventInterceptorInvoker(interceptor, prop.priority)
        interceptors.add(invoker)
    }

    public fun addDispatchInterceptor(
        interceptor: EventDispatchInterceptor,
        configurer: ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>? = null
    ) {
        val prop = SimpleEventDispatchInterceptorRegistrationPropertiesImpl().also { configurer?.invokeWith(it) }
        val invoker = SimpleEventDispatchInterceptorInvoker(interceptor, prop.priority)
        dispatchInterceptors.add(invoker)
    }

    public fun build(): SimpleEventInterceptors =
        SimpleEventInterceptors(interceptors.sorted(), dispatchInterceptors.sorted())
}

/**
 * 一个包装了多个
 * [SimpleEventInterceptorInvoker] 和 [SimpleEventDispatchInterceptorInvoker]
 * 的集合体。
 */
public data class SimpleEventInterceptors(
    public val interceptors: List<SimpleEventInterceptorInvoker>,
    public val dispatchInterceptors: List<SimpleEventDispatchInterceptorInvoker>,
)

/**
 * 一个携带优先级的 [EventInterceptor] 代理实现。
 */
public class SimpleEventInterceptorInvoker(
    private val interceptor: EventInterceptor,
    private val priority: Int
) : Comparable<SimpleEventInterceptorInvoker>, EventInterceptor by interceptor {
    override fun compareTo(other: SimpleEventInterceptorInvoker): Int = priority.compareTo(other.priority)
}

/**
 * 一个携带优先级的 [EventDispatchInterceptor] 代理实现。
 */
public class SimpleEventDispatchInterceptorInvoker(
    private val interceptor: EventDispatchInterceptor,
    private val priority: Int
) : Comparable<SimpleEventDispatchInterceptorInvoker>, EventDispatchInterceptor by interceptor {
    override fun compareTo(other: SimpleEventDispatchInterceptorInvoker): Int = priority.compareTo(other.priority)
}

/**
 * 一个针对 [SimpleEventInterceptorInvoker] 的简单执行器实现。
 */
public class SimpleEventInterceptorsInvoker(private val interceptors: Iterable<SimpleEventInterceptorInvoker>) {
    private class ContextImpl(
        override val eventListenerContext: EventListenerContext,
        val iterator: Iterator<EventInterceptor>,
        private val actualTarget: suspend (EventListenerContext) -> EventResult
    ) : EventInterceptor.Context {

        // TODO Java API?
        @JvmSynthetic
        @Throws(Exception::class)
        override suspend fun invoke(): EventResult {
            return if (iterator.hasNext()) {
                iterator.next().intercept(this)
            } else {
                actualTarget(eventListenerContext)
            }
        }

        // TODO Java API?
        @JvmSynthetic
        @Throws(Exception::class)
        override suspend fun invoke(eventListenerContext: EventListenerContext): EventResult {
            return if (iterator.hasNext()) {
                iterator.next().intercept(copy(eventListenerContext))
            } else {
                actualTarget(eventListenerContext)
            }
        }

        private fun copy(eventListenerContext: EventListenerContext): ContextImpl =
            ContextImpl(eventListenerContext, iterator, actualTarget)
    }

    // TODO Java API?
    @JvmSynthetic
    public suspend fun invoke(
        eventContext: EventListenerContext,
        actualTarget: suspend (EventListenerContext) -> EventResult
    ): EventResult {
        val context = ContextImpl(eventContext, interceptors.iterator(), actualTarget)
        return context.invoke()
    }
}

public class SimpleEventDispatchInterceptorsInvoker(private val interceptors: Iterable<SimpleEventDispatchInterceptorInvoker>) {
    private class ContextImpl(
        override val eventContext: EventContext,
        val iterator: Iterator<EventDispatchInterceptor>,
        private val actualTarget: (EventContext) -> Flow<EventResult>
    ) : EventDispatchInterceptor.Context {
        override fun invoke(): Flow<EventResult> {
            return if (iterator.hasNext()) {
                iterator.next().intercept(this)
            } else {
                actualTarget(eventContext)
            }
        }

        override fun invoke(eventContext: EventContext): Flow<EventResult> {
            return if (iterator.hasNext()) {
                iterator.next().intercept(copy(eventContext))
            } else {
                actualTarget(eventContext)
            }
        }

        private fun copy(eventContext: EventContext): ContextImpl =
            ContextImpl(eventContext, iterator, actualTarget)
    }

    public fun invoke(
        eventContext: EventContext,
        actualTarget: (EventContext) -> Flow<EventResult>
    ): Flow<EventResult> {
        val context = ContextImpl(eventContext, interceptors.iterator(), actualTarget)
        return context.invoke()
    }
}

/**
 * 将 [SimpleEventDispatcherConfigurationImpl] 中的部分信息解析为 [SimpleEventInterceptors]
 */
public fun SimpleEventDispatcherConfigurationImpl.resolveInterceptors(): SimpleEventInterceptors {
    return SimpleEventInterceptorsBuilder().apply {
        interceptors.forEach { (interceptor, prop) ->
            addInterceptor(interceptor, prop)
        }
        dispatchInterceptors.forEach { (interceptor, prop) ->
            addDispatchInterceptor(interceptor, prop)
        }
    }.build()
}

/**
 * [SimpleEventDispatcher] 的简单实现
 *
 * @author ForteScarlet
 */
public class SimpleEventDispatcherImpl(
    private val configuration: SimpleEventDispatcherConfiguration,
    private val interceptors: SimpleEventInterceptors,
) : SimpleEventDispatcher {

    public constructor(configuration: SimpleEventDispatcherConfigurationImpl) : this(
        configuration = configuration,
        interceptors = configuration.resolveInterceptors()
    )

    private val dispatchInterceptorsInvoker =
        interceptors.dispatchInterceptors.takeIf { it.isNotEmpty() }?.let { SimpleEventDispatchInterceptorsInvoker(it) }

    private val dispatcherContext = configuration.coroutineContext.minusKey(Job)

    //endregion

    private val listenersQueue =
        love.forte.simbot.common.collection.createPriorityConcurrentQueue<SimpleEventListenerInvoker>()

    override val listeners: Sequence<EventListener>
        get() = listenersQueue.asSequence().map { it.listener }

    override fun register(
        propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?,
        listener: EventListener
    ): EventListenerRegistrationHandle {
        val interceptorBuilder = SimpleEventInterceptorsBuilder()
        val prop = SimpleEventListenerRegistrationPropertiesImpl(interceptorBuilder).also { prop ->
            propertiesConsumer?.invokeWith(prop)
        }

        // interceptors
        val listenerScopeInterceptors = interceptorBuilder.build().interceptors
        val eachInterceptors = interceptors.interceptors
        val listenerInterceptors = (listenerScopeInterceptors + eachInterceptors).sorted()

        val priority = prop.priority
        val listenerInvoker = SimpleEventListenerInvoker(listenerInterceptors, listener)

        listenersQueue.add(priority, listenerInvoker)

        return createQueueRegistrationHandle(priority, listenersQueue, listenerInvoker)
    }

    override fun dispose(listener: EventListener) {
        listenersQueue.removeIf { it.listener == listener }
    }


    override fun push(event: Event): Flow<EventResult> {
        return pushWithInterceptor(event)
    }

    private fun pushWithInterceptor(event: Event): Flow<EventResult> {
        val context = EventContextImpl(dispatcherContext, event)

        return runCatching {
            dispatchInterceptorsInvoker?.invoke(context) { eventFlow(context) } ?: eventFlow(context)
        }.getOrElse { e ->
            return flow {
                // emit exception
                throw e
            }
        }

    }

    private fun eventFlow(context: EventContext): Flow<EventResult> {
        return if (dispatcherContext == EmptyCoroutineContext) {
            flow {
                dispatchInFlowWithoutCoroutineContext(context, this)
            }
        } else {
            flow {
                dispatchInFlow(context, dispatcherContext, this)
            }
        }
    }

    private data class EventContextImpl(
        override val coroutineContext: CoroutineContext,
        override val event: Event,
        override val attributes: MutableAttributeMap = mutableAttributeMapOf(concurrentMutableMap())
    ) : EventContext


    private suspend fun SimpleEventListenerInvoker.invokeAndCollectedOrErrorResult(context: EventListenerContext): EventResult =
        orErrorResult { invoke(context).collected() }

    private suspend fun dispatchInFlow(
        context: EventContext,
        dispatcherContext: CoroutineContext,
        collector: FlowCollector<EventResult>
    ) {
        val listenerIterator = listenersQueue.iterator()

        for (listenerInvoker in listenerIterator) {
            val lContext = EventListenerContextImpl(context, listenerInvoker.listener)
            val result = withContext(dispatcherContext) {
                listenerInvoker.invokeAndCollectedOrErrorResult(lContext)
            }

            collector.emit(result)

            if (result.isTruncated) {
                break
            }
        }
    }

    private suspend fun dispatchInFlowWithoutCoroutineContext(
        context: EventContext,
        collector: FlowCollector<EventResult>
    ) {
        val listenerIterator = listenersQueue.iterator()
        for (listenerInvoker in listenerIterator) {
            val lContext = EventListenerContextImpl(context, listenerInvoker.listener)
            val result = listenerInvoker.invokeAndCollectedOrErrorResult(lContext)

            collector.emit(result)

            if (result.isTruncated) {
                break
            }
        }
    }

    private inline fun orErrorResult(block: () -> EventResult): EventResult =
        runCatching { block() }.getOrElse { e -> EventResult.error(e) }

    override fun toString(): String {
        return "SimpleEventDispatcher"
    }
}

private data class EventListenerContextImpl(override val context: EventContext, override val listener: EventListener) : EventListenerContext {
    @Volatile
    override var plainText: String? = (context.event as? MessageEvent)?.messageContent?.plainText
}


private class SimpleEventListenerInvoker(
    /**
     * 合并了全局配置的拦截器和单独添加的拦截器
     */
    interceptors: List<SimpleEventInterceptorInvoker>,
    val listener: EventListener
) {
    private val interceptorsInvoker = interceptors.takeIf { it.isNotEmpty() }?.let { interceptorList ->
        SimpleEventInterceptorsInvoker(interceptorList)
    }

    suspend fun invoke(context: EventListenerContext): EventResult {
        return interceptorsInvoker?.invoke(context, listener::handle) ?: listener.handle(context)
    }

}

internal expect fun <T : Any> createQueueRegistrationHandle(
    priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
): EventListenerRegistrationHandle
