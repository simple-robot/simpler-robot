/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.core.event

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription
import java.util.function.Function
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@DslMarker
internal annotation class SimpleEventManagerConfigDSL

/**
 * [SimpleEventListenerManager] 的配置文件.
 * 当配置文件作为构建参数的时候，他会被立即使用。
 *
 *
 * @see SimpleEventListenerManager.newInstance
 * @see simpleListenerManager
 */
@SimpleEventManagerConfigDSL
public open class SimpleListenerManagerConfiguration {
    
    /**
     * 事件管理器的上下文. 可以基于此提供调度器。
     * 但是 [SimpleEventListenerManager] 并不是一个作用域，因此不可以提供 [Job][kotlinx.coroutines.Job].
     *
     * 默认情况下，如果 [coroutineContext] 中不存在调度器，则在使用时会提供一个默认的调度器。
     */
    @SimpleEventManagerConfigDSL
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext
    
    /**
     * 事件流程拦截器的列表。
     */
    private var processingInterceptors = mutableIDMapOf<EventProcessingInterceptor>()
    
    /**
     * 监听函数拦截器的列表。
     */
    private var listenerInterceptors = mutableIDMapOf<EventListenerInterceptor>()
    
    /**
     * 监听函数注册信息列表。
     */
    private var listenerRegistrationDescriptions = mutableListOf<EventListenerRegistrationDescription>()
    
    /**
     * 自定义的监听函数异常处理器。
     *
     */
    @Volatile
    @JvmSynthetic
    private var listenerExceptionHandler: ((Throwable) -> EventResult)? = null
    
    /**
     * 自定义的监听函数异常处理器。
     *
     */
    @JvmSynthetic
    @ExperimentalSimbotApi
    @SimpleEventManagerConfigDSL
    public fun listenerExceptionHandler(handler: (Throwable) -> EventResult): SimpleListenerManagerConfiguration =
        also {
            listenerExceptionHandler = handler
        }
    
    /**
     * 自定义的监听函数异常处理器。
     *
     */
    @Api4J
    @ExperimentalSimbotApi
    public fun listenerExceptionHandler(handler: Function<Throwable, EventResult>): SimpleListenerManagerConfiguration =
        also {
            listenerExceptionHandler = handler::apply
        }
    
    
    // region 拦截器相关
    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @SimpleEventManagerConfigDSL
    public fun addProcessingInterceptors(interceptors: Map<ID, EventProcessingInterceptor>): SimpleListenerManagerConfiguration =
        also {
            if (interceptors.isEmpty()) return this
            
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
    @SimpleEventManagerConfigDSL
    public fun addListenerInterceptors(interceptors: Map<ID, EventListenerInterceptor>): SimpleListenerManagerConfiguration =
        also {
            if (interceptors.isEmpty()) return this
            
            val listenerInterceptorsCopy = listenerInterceptors.toMutableMap()
            
            for ((id, interceptor) in interceptors) {
                listenerInterceptorsCopy.merge(id, interceptor) { _, _ ->
                    throw IllegalStateException("Duplicate ID: $id")
                }
            }
            
            listenerInterceptors = mutableIDMapOf(listenerInterceptorsCopy)
        }
    
    /**
     * 进入到拦截器配置域。
     * ```kotlin
     * interceptors {
     *    processingIntercept { ... }
     *    listenerIntercept { ... }
     * }
     * ```
     */
    @SimpleEventManagerConfigDSL
    public fun interceptors(block: EventInterceptorsGenerator.() -> Unit): SimpleListenerManagerConfiguration =
        also {
            interceptors().also(block).build({ ls ->
                addListenerInterceptors(ls)
            }) { ps ->
                addProcessingInterceptors(ps)
            }
        }
    
    /**
     * 进入到拦截器配置域。
     */
    @OptIn(InternalSimbotApi::class)
    @Suppress("MemberVisibilityCanBePrivate")
    public fun interceptors(): EventInterceptorsGenerator = EventInterceptorsGenerator()
    
    // endregion
    
    
    // region 监听函数相关
    
    /**
     * 直接添加一个监听函数。
     * ```kotlin
     * addListener(simpleListener(FriendMessageEvent) { event -> // this: EventListenerProcessingContext
     *     delay(200)
     *     event.friend().send("Hi! context: $context")
     *
     *     EventResult.defaults() // result
     * })
     * ```
     */
    public fun addListener(listener: EventListener): SimpleListenerManagerConfiguration = also {
        listenerRegistrationDescriptions.add(listener.toRegistrationDescription { })
    }
    
    /**
     * 添接加多个监听函数。
     */
    public fun addListeners(listeners: Collection<EventListener>): SimpleListenerManagerConfiguration = also {
        listeners.mapTo(this.listenerRegistrationDescriptions) { it.toRegistrationDescription() }
    }
    
    /**
     * 直接添加多个监听函数。
     */
    public fun addListeners(vararg listeners: EventListener): SimpleListenerManagerConfiguration = also {
        addListeners(listeners.asList())
    }
    
    /**
     * 直接添加一个监听函数。
     * ```kotlin
     * addListenerRegistrationDescription(simpleListener(FriendMessageEvent) { event -> // this: EventListenerProcessingContext
     *     delay(200)
     *     event.friend().send("Hi! context: $context")
     *
     *     EventResult.defaults() // result
     * }.toRegistrationDescription())
     * ```
     */
    public fun addListenerRegistrationDescription(description: EventListenerRegistrationDescription): SimpleListenerManagerConfiguration =
        also {
            listenerRegistrationDescriptions.add(description)
        }
    
    /**
     * 添接加多个监听函数。
     */
    public fun addListenerRegistrationDescriptions(descriptions: Collection<EventListenerRegistrationDescription>): SimpleListenerManagerConfiguration =
        also {
            listenerRegistrationDescriptions.addAll(descriptions)
        }
    
    /**
     * 直接添加多个监听函数。
     */
    public fun addListenerRegistrationDescriptions(vararg descriptions: EventListenerRegistrationDescription): SimpleListenerManagerConfiguration =
        also {
            listenerRegistrationDescriptions.addAll(descriptions)
        }
    
    /**
     * 进入到拦截器配置域.
     *
     *  @see EventListenerRegistrationDescriptionsGenerator
     */
    @EventListenersGeneratorDSL
    public fun listeners(block: EventListenerRegistrationDescriptionsGenerator.() -> Unit): SimpleListenerManagerConfiguration =
        also {
            val descriptions = listeners().also(block).build()
            addListenerRegistrationDescriptions(descriptions)
        }
    
    
    /**
     * 进入到监听函数配置域。
     */
    @OptIn(InternalSimbotApi::class)
    private fun listeners(): EventListenerRegistrationDescriptionsGenerator =
        EventListenerRegistrationDescriptionsGenerator()
    
    // endregion
    
    
    internal fun build(serializersModule: SerializersModule = EmptySerializersModule()): SimpleListenerManagerConfig {
        return SimpleListenerManagerConfig(
            coroutineContext,
            exceptionHandler = listenerExceptionHandler,
            processingInterceptors = idMapOf(processingInterceptors),
            listenerInterceptors = idMapOf(listenerInterceptors),
            listeners = listenerRegistrationDescriptions.toList(),
            serializersModule // TODO
        )
    }
    
    
    public companion object {
        public inline operator fun invoke(block: SimpleListenerManagerConfiguration.() -> Unit): SimpleListenerManagerConfiguration {
            return SimpleListenerManagerConfiguration().also(block)
        }
    }
    
}


public data class SimpleListenerManagerConfig(
    internal val coroutineContext: CoroutineContext,
    internal val exceptionHandler: ((Throwable) -> EventResult)? = null,
    internal val processingInterceptors: IDMaps<EventProcessingInterceptor>,
    internal val listenerInterceptors: IDMaps<EventListenerInterceptor>,
    internal val listeners: List<EventListenerRegistrationDescription>,
    internal val messageSerializersModule: SerializersModule,
    
    )

