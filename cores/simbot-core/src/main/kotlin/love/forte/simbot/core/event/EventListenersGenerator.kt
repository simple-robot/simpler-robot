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

import love.forte.simbot.*
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.utils.runWithInterruptible
import org.slf4j.Logger
import java.util.function.BiConsumer
import java.util.function.BiFunction

@DslMarker
internal annotation class EventListenersGeneratorDSL


/**
 *
 * 用于构建监听函数的构建器。
 *
 * 结构示例：
 * ```kotlin
 * // 假设在 CoreListenerManagerConfiguration 中
 * listeners {
 *     // plus listener of EventListenersGenerator
 *     +coreListener(FooEvent) { /* Nothing here. */ }
 *
 *     // listener of EventListenersGenerator
 *     listener(FooEvent) {
 *         // handle of `listener`
 *         handle { event -> // this: EventListenerProcessingContext
 *             // do..
 *             delay(200) // suspend support
 *             event.friend().send("Hello!")
 *         }
 *     }
 *
 *     // use invoke handle function
 *     FooEvent { // Same as: FooEvent.Key.invoke { ... }
 *        // do...
 *     }
 * }
 * ```
 *
 *  @author ForteScarlet
 */
@EventListenersGeneratorDSL
public class EventListenersGenerator @InternalSimbotApi constructor() {
    private val listeners = mutableListOf<EventListener>()
    
    /**
     * 构建一个监听函数。
     *
     * ```kotlin
     * listener(FooEvent) {
     *      // 监听函数的处理逻辑
     *      handle { event -> // this: EventListenerProcessingContext
     *          event.friend().send("Context: $context")
     *      }
     * }
     * ```
     */
    @EventListenersGeneratorDSL
    public fun <E : Event> listener(
        eventKey: Event.Key<E>,
        block: ListenerGenerator<E>.() -> Unit,
    ): EventListenersGenerator = also {
        val listener = ListenerGenerator(eventKey).also(block).build()
        listeners.add(listener)
    }
    
    
    /**
     * 直接提供一个 [EventListener] 实例。
     *
     */
    @EventListenersGeneratorDSL
    public fun listener(listener: EventListener): EventListenersGenerator = also {
        listeners.add(listener)
    }
    
    
    /**
     * 通过 `+=` 的方式直接提供一个 [EventListener] 实例。
     *
     * ```kotlin
     * listeners {
     *    +fooListener
     * }
     * ```
     *
     */
    @EventListenersGeneratorDSL
    public operator fun EventListener.unaryPlus() {
        listeners.add(this)
    }
    
    /**
     * 监听指定的事件类型并直接进行事件处理。
     *
     * e.g.
     * ```kotlin
     * FooEvent { event -> // this: EventListenerProcessingContext
     *     // do handle
     * }
     * ```
     *
     * 相当于：
     * ```kotlin
     * listener(FooEvent) {
     *     handle { event -> // this: EventListenerProcessingContext
     *        // do handle
     *     }
     * }
     * ```
     *
     * @receiver 需要监听的 [事件类型][Event.Key] 对象实例。
     *
     */
    @EventListenersGeneratorDSL
    public operator fun <E : Event> Event.Key<E>.invoke(handle: suspend EventListenerProcessingContext.(E) -> Any?) {
        listener(this) {
            handle(handle)
        }
    }
    
    /**
     * 得到当前构建的所有 listeners。
     */
    public fun build(): List<EventListener> {
        return listeners
    }
}


// region listener generator
@DslMarker
internal annotation class ListenerGeneratorDSL


/**
 * 监听函数构建器。
 *
 * 应用于 [EventListenersGenerator] 中。
 *
 * @author ForteScarlet
 */
@ListenerGeneratorDSL
public class ListenerGenerator<E : Event> @InternalSimbotApi constructor(private val eventKey: Event.Key<E>) {
    
    
    /**
     * 设置listener的ID
     */
    @ListenerGeneratorDSL
    public var id: ID? = null
    
    
    /**
     * 使用的日志
     */
    @ListenerGeneratorDSL
    public var logger: Logger? = null
    
    /**
     * 当处理函数的响应值不是 [EventResult] 类型的时候，是否默认阻止下一个函数的执行。
     */
    @ListenerGeneratorDSL
    public var blockNext: Boolean = false
    
    /**
     * 是否标记为异步函数。
     */
    @ListenerGeneratorDSL
    public var isAsync: Boolean = false
    
    
    private var func: suspend EventListenerProcessingContext.(E) -> Any? = { null }
    
    /**
     * 监听函数。
     */
    @JvmSynthetic
    @ListenerGeneratorDSL
    public fun handle(func: suspend EventListenerProcessingContext.(E) -> Any?) {
        this.func = func
    }
    
    /**
     * 监听函数。
     */
    @Api4J
    @JvmName("handle")
    @Suppress("FunctionName")
    public fun _handle(func: BiConsumer<EventListenerProcessingContext, E>): ListenerGenerator<E> = also {
        this.func = { e ->
            runWithInterruptible { func.accept(this, e) }
            null
        }
    }
    
    /**
     * 监听函数。
     */
    @Api4J
    @JvmName("handle")
    @Suppress("FunctionName")
    public fun _handle(func: BiFunction<EventListenerProcessingContext, E, Any?>): ListenerGenerator<E> = also {
        this.func = { e -> runWithInterruptible { func.apply(this, e) } }
    }
    
    internal fun build(): EventListener {
        val id0 = id ?: randomID()
        return coreListener(
            eventKey = eventKey,
            id = id0,
            blockNext = blockNext,
            isAsync = isAsync,
            logger = logger ?: LoggerFactory.getLogger("love.forte.core.listener.$id0"),
            func = func
        )
    }
    
}
// endregion

