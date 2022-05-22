/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.*
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventResult
import java.util.function.Function
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@DslMarker
internal annotation class CoreEventManagerConfigDSL

/**
 * [CoreListenerManager] 的配置文件.
 * 当配置文件作为构建参数的时候，他会被立即使用。
 *
 * ### 拦截器
 * 通过 [interceptors] [addListenerInterceptors] [addListenerInterceptors] 等相关函数来向配置中追加对应作用域的拦截器。
 * ```kotlin
 * coreListenerManager {
 *
 * }
 * ```
 * ### 监听函数
 *
 *
 * @see CoreListenerManager.newInstance
 * @see coreListenerManager
 */
@CoreEventManagerConfigDSL
public class CoreListenerManagerConfiguration {
    
    
    /**
     * 事件管理器的上下文. 可以基于此提供调度器。
     * 但是 [CoreListenerManager] 并不是一个作用域，因此不可以提供 `Job`.
     */
    @CoreEventManagerConfigDSL
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
     * 监听函数的列表。
     */
    private var listeners = mutableListOf<EventListener>()
    
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
    @CoreEventManagerConfigDSL
    public fun listenerExceptionHandler(handler: (Throwable) -> EventResult): CoreListenerManagerConfiguration = also {
        listenerExceptionHandler = handler
    }
    
    /**
     * 自定义的监听函数异常处理器。
     *
     */
    @Api4J
    @ExperimentalSimbotApi
    public fun listenerExceptionHandler(handler: Function<Throwable, EventResult>): CoreListenerManagerConfiguration =
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
    @CoreEventManagerConfigDSL
    public fun addProcessingInterceptors(interceptors: Map<ID, EventProcessingInterceptor>): CoreListenerManagerConfiguration =
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
    @CoreEventManagerConfigDSL
    public fun addListenerInterceptors(interceptors: Map<ID, EventListenerInterceptor>): CoreListenerManagerConfiguration =
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
    @CoreEventManagerConfigDSL
    public fun interceptors(block: EventInterceptorsGenerator.() -> Unit): CoreListenerManagerConfiguration =
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
    public fun addListener(listener: EventListener): CoreListenerManagerConfiguration = also {
        listeners.add(listener)
    }
    
    /**
     * 添接加多个监听函数。
     */
    public fun addListeners(listeners: Collection<EventListener>): CoreListenerManagerConfiguration = also {
        this.listeners.addAll(listeners)
    }
    
    /**
     * 直接添加多个监听函数。
     */
    public fun addListeners(vararg listeners: EventListener): CoreListenerManagerConfiguration = also {
        this.listeners.addAll(listeners)
    }
    
    /**
     * 进入到拦截器配置域.
     *
     *  @see EventListenersGenerator
     */
    @EventListenersGeneratorDSL
    public fun listeners(block: EventListenersGenerator.() -> Unit): CoreListenerManagerConfiguration =
        also {
            val listeners = listeners().also(block).build()
            addListeners(listeners)
        }
    
    
    /**
     * 进入到监听函数配置域。
     */
    private fun listeners(): EventListenersGenerator = EventListenersGenerator()
    
    // endregion
    
    
    @OptIn(ExperimentalSerializationApi::class)
    internal fun build(serializersModule: SerializersModule = EmptySerializersModule): CoreListenerManagerConfig {
        return CoreListenerManagerConfig(
            coroutineContext,
            exceptionHandler = listenerExceptionHandler,
            processingInterceptors = idMapOf(processingInterceptors),
            listenerInterceptors = idMapOf(listenerInterceptors),
            listeners = listeners.toList(),
            serializersModule // TODO
        )
    }
    
    
    public companion object {
        public inline operator fun invoke(block: CoreListenerManagerConfiguration.() -> Unit): CoreListenerManagerConfiguration {
            return CoreListenerManagerConfiguration().also(block)
        }
    }
    
}


public data class CoreListenerManagerConfig(
    internal val coroutineContext: CoroutineContext,
    internal val exceptionHandler: ((Throwable) -> EventResult)? = null,
    internal val processingInterceptors: IDMaps<EventProcessingInterceptor>,
    internal val listenerInterceptors: IDMaps<EventListenerInterceptor>,
    internal val listeners: List<EventListener>,
    internal val messageSerializersModule: SerializersModule,
    
    )

