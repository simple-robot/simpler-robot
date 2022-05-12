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

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.IDMaps
import love.forte.simbot.event.*
import love.forte.simbot.idMapOf
import love.forte.simbot.mutableIDMapOf
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 *
 * @see CoreListenerManager.newInstance
 * @see coreListenerManager
 */
public class CoreListenerManagerConfiguration2 {


    /**
     * 事件管理器的上下文. 可以基于此提供调度器。
     * 但是 [CoreListenerManager] 并不是一个作用域，因此不可以提供 `Job`.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    private var processingInterceptors = mutableIDMapOf<EventProcessingInterceptor>()
    private var listenerInterceptors = mutableIDMapOf<EventListenerInterceptor>()

    // 初始监听函数
    private val listenerBuilders = mutableListOf<() -> EventListener>()
    private var listeners = Listeners()

    public inner class Listeners {
        /**
         * 配置注册一个监听函数。
         */
        public fun <E : Event> listen(eventKey: Event.Key<E>, builder: CoreListenerBuilder<E>.() -> Unit) {
            listenerBuilders.add { CoreListenerBuilder(eventKey).also(builder).build() }
        }

        /**
         * 直接注册一个 [EventListener] 实例。
         */
        public fun listener(listener: EventListener) {
            listenerBuilders.add { listener }
        }

    }

    /**
     * 自定义的监听函数异常处理器。
     *
     */
    public var listenerExceptionHandler: ((Throwable) -> EventResult)? = null


    public fun listenerExceptionHandler(handler: (Throwable) -> EventResult): CoreListenerManagerConfiguration2 = also {
        listenerExceptionHandler = handler
    }


    //region 监听函数相关

    /**
     * 配置监听函数相关内容。
     */
    public fun listeners(block: Listeners.() -> Unit) {
        listeners.block()
    }


    //endregion


    /**
     * 事件流程上下文的处理器。
     */
    // 暂时不公开
    // @CoreEventManagerConfigDSL
    public var eventProcessingContextResolver: (manager: CoreListenerManager, scope: CoroutineScope) -> EventProcessingContextResolver<*> =
        { _, scope -> CoreEventProcessingContextResolver(scope) }
        private set


    internal fun build(): CoreListenerManagerConfig2 {
        return CoreListenerManagerConfig2(
            coroutineContext,
            exceptionHandler = listenerExceptionHandler,
            processingInterceptors = idMapOf(processingInterceptors),
            listenerInterceptors = idMapOf(listenerInterceptors),
            listeners = listenerBuilders.map { it() }
        )
    }


    public companion object {
        public inline operator fun invoke(block: CoreListenerManagerConfiguration2.() -> Unit): CoreListenerManagerConfiguration2 {
            return CoreListenerManagerConfiguration2().also(block)
        }
    }

}


public data class CoreListenerManagerConfig2(
    internal val coroutineContext: CoroutineContext,
    internal val exceptionHandler: ((Throwable) -> EventResult)? = null,
    internal val processingInterceptors: IDMaps<EventProcessingInterceptor>,
    internal val listenerInterceptors: IDMaps<EventListenerInterceptor>,
    internal val listeners: List<EventListener>,

    )

