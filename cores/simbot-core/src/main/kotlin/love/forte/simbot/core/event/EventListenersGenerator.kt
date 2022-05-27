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
import java.util.function.BiPredicate

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
 *     +simpleListener(FooEvent) { /* Nothing here. */ }
 *
 *     // listener of EventListenersGenerator
 *     listen(FooEvent) {
 *         match { event -> // this: EventListenerProcessingContext
 *            // ...
 *            true
 *         }
 *
 *         // handle of `listener`
 *         handle { event -> // this: EventListenerProcessingContext
 *             // do..
 *             delay(200) // suspend support
 *             event.friend().send("Hello!")
 *
 *             EventResult.defaults()
 *         }
 *     }
 *
 *     // use invoke handle function
 *     FooEvent { // Same as: FooEvent.Key.invoke { ... }
 *        // do...
 *
 *        EventResult.of(...)
 *     }
 * }
 * ```
 *
 *  @author ForteScarlet
 */
@EventListenersGeneratorDSL
public class EventListenersGenerator @InternalSimbotApi constructor() {
    private val listeners = mutableListOf<() -> EventListener>()
    
    /**
     * 构建一个监听函数。
     *
     * ```kotlin
     * listen(FooEvent) {
     *      // 监听函数匹配逻辑
     *      match { event -> // this: EventListenerProcessingContext
     *         // ...
     *         true
     *      }
     *
     *      // 监听函数的处理逻辑
     *      handle { event -> // this: EventListenerProcessingContext
     *          event.friend().send("Context: $context")
     *
     *          EventResult.defaults()
     *      }
     * }
     * ```
     */
    @EventListenersGeneratorDSL
    public fun <E : Event> listen(
        eventKey: Event.Key<E>,
        block: ListenerGenerator<E>.() -> Unit,
    ): EventListenersGenerator = also {
        listeners.add { ListenerGenerator(eventKey).also(block).build() }
    }
    
    
    
    @Deprecated("Use listen(Event.Key){ ... }", ReplaceWith("listen(eventKey, block)"))
    public fun <E : Event> listener(
        eventKey: Event.Key<E>,
        block: ListenerGenerator<E>.() -> Unit,
    ): EventListenersGenerator = listen(eventKey, block)
    
    
    /**
     * 直接提供一个 [EventListener] 实例。
     *
     */
    @EventListenersGeneratorDSL
    public fun listener(listener: EventListener): EventListenersGenerator = also {
        listeners.add { listener }
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
        listener(this)
    }
    
    /**
     * 监听指定的事件类型并直接进行事件处理。
     *
     * e.g.
     * ```kotlin
     * FooEvent { event -> // this: EventListenerProcessingContext
     *     // do handle
     *
     *     EventResult.defaults() // result
     * }
     * ```
     *
     * 相当于：
     * ```kotlin
     * listen(FooEvent) {
     *     handle { event -> // this: EventListenerProcessingContext
     *        // do handle
     *
     *        EventResult.of(...) // result
     *     }
     * }
     * ```
     *
     * 可以在当前构建器上下文中配合 [onMatch] 为当前构建的监听函数提供匹配逻辑.
     *
     * e.g.
     * ```kotlin
     * FooEvent { event: FooEvent -> // this: EventListenerProcessingContext
     *   // do handle
     *
     *   EventResult.defaults()
     * } onMatch {
     *    val condition1: Boolean = ...
     *    condition1
     * } onMatch {
     *    val condition2: Boolean = ...
     *    condition2
     * }
     * ```
     *
     * @receiver 需要监听的 [事件类型][Event.Key] 对象实例。
     *
     * @see onMatch
     */
    @EventListenersGeneratorDSL
    public operator fun <E : Event> Event.Key<E>.invoke(handle: suspend EventListenerProcessingContext.(E) -> EventResult): EventHandling<E> {
        val generator = ListenerGenerator(this)
        generator.handle(handle)
        listeners.add { generator.build() }
        return EventHandling(generator)
    }
    
    /**
     * 通过 [Event.Key.invoke] 得到的 _处理过程_ 对象，用于进一步配置此事件的匹配逻辑。
     */
    @InternalSimbotApi
    @JvmInline
    public value class EventHandling<E : Event> internal constructor(@PublishedApi internal val generator: ListenerGenerator<E>)
    
    
    /**
     * 配合 [Event.Key.invoke] 为其提供对于匹配逻辑的构建。
     *
     * ```kotlin
     * FooEvent { event: FooEvent -> // this: EventListenerProcessingContext
     *   // do handle
     *
     *   EventResult.defaults()
     * } onMatch {
     *    val condition1: Boolean = ...
     *    condition1
     * } onMatch {
     *    val condition2: Boolean = ...
     *    condition2
     * }
     * ```
     *
     * 使用 [onMatch] 效果类似于使用 [ListenerGenerator.match], 当配置多层时相当于通过与(`&&`)连接。
     *
     * @see Event.Key.invoke
     * @see ListenerGenerator.match
     *
     */
    public inline infix fun <E : Event> EventHandling<E>.onMatch(crossinline matcher: suspend EventListenerProcessingContext.(E) -> Boolean): EventHandling<E> = also {
        generator.match { matcher(it) }
    }
    
    /**
     * 配合 [Event.Key.invoke] 并标记其构建的监听函数为异步的。
     *
     * ```kotlin
     * FooEvent { event: FooEvent -> // this: EventListenerProcessingContext
     *   // do handle
     *
     *   EventResult.defaults()
     * }.async(true)
     *
     * // or
     *
     * FooEvent { event ->
     *    // ...
     *    eventResult()
     * }.async() // default param: true
     * ```
     * 可以配合 [onMatch] 在其之前使用来指定当前函数的异步性。
     *
     * ```kotlin
     * FooEvent { event ->
     *    // ...
     *    eventResult()
     * }.async() onMatch {
     *    // ...
     *    true
     * }
     *
     * ```
     *
     *
     *
     * @see Event.Key.invoke
     * @see ListenerGenerator.match
     *
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline fun <E : Event> EventHandling<E>.async(isAsync: Boolean = true): EventHandling<E> = also {
        generator.isAsync = isAsync
    }
    
    /**
     * 得到当前构建的所有 listeners。
     */
    public fun build(): List<EventListener> {
        return listeners.map { it() }
    }
    
    
    
    /**
     * 在 [EventListenersGenerator] 环境中提供一个可以更简单快捷的构建 [事件结果][EventResult] 的内联函数，
     * 其效果等同于使用 [EventResult.of].
     *
     * e.g.
     * ```kotlin
     * listeners {
     *   listen(FooEvent) {
     *      handle {
     *         // do handle...
     *         eventResult() // return EventResult
     *      }
     *   }
     * }
     * ```
     *
     */
    @Suppress("NOTHING_TO_INLINE", "unused")
    public inline fun EventListenerProcessingContext.eventResult(content: Any? = null, isTruncated: Boolean = false): EventResult = EventResult.of(content, isTruncated)
    
    
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
     * 是否标记为异步函数。
     */
    @ListenerGeneratorDSL
    public var isAsync: Boolean = false
    
    
    private var matcher: (suspend EventListenerProcessingContext.(E) -> Boolean)? = null
    private fun setMatcher(m: suspend EventListenerProcessingContext.(E) -> Boolean) {
        val old = matcher
        matcher = if (old == null) {
            m
        } else {
            {
                old(it) && m(it)
            }
        }
    }
    
    /**
     * 配置当前监听函数的匹配函数。
     *
     * ```kotlin
     * listen(FooEvent) {
     *    match { condition } // return Boolean
     *    handle { ... }
     * }
     * ```
     *
     * [match] 函数允许多次使用。当执行多次 [match] 时，其效果相当于每次配置的条件之间通过与(`&&`)相连接。
     *
     * 例如：
     * ```kotlin
     * listen(FooEvent) {
     *    match { condition1 }
     *    match { condition2 }
     *    match { condition3 }
     *
     *    handle { ... }
     * }
     * ```
     * 其效果等同于：
     * ```kotlin
     * listen(FooEvent) {
     *    match { condition1 && condition2 && condition3 }
     *
     *    handle { ... }
     * }
     * ```
     *
     *
     */
    @JvmSynthetic
    @ListenerGeneratorDSL
    public fun match(matcher: suspend EventListenerProcessingContext.(E) -> Boolean) {
        setMatcher(matcher)
    }
    
    /**
     * 配置当前监听函数的匹配函数。
     *
     * @see match
     */
    @Api4J
    @JvmName("match")
    @Suppress("FunctionName")
    public fun _match(matcher: BiPredicate<EventListenerProcessingContext, E>): ListenerGenerator<E> = also {
        setMatcher { e -> runWithInterruptible { matcher.test(this, e) } }
    }
    
    
    
    private var func: (suspend EventListenerProcessingContext.(E) -> EventResult)? = null
    
    private fun setFunc(f: suspend EventListenerProcessingContext.(E) -> EventResult) {
        if (this.func != null) {
            throw SimbotIllegalStateException("handle can and can only be configured once")
        }
        
        func = f
    }
    
    /**
     * 监听函数。处理监听到的事件的具体逻辑。
     *
     * ```kotlin
     * listen(FooEvent) {
     *    handle { event: FooEvent -> // this: EventListenerProcessingContext
     *       // do handle
     *
     *       EventResult.of(...) // return
     *    }
     * }
     * ```
     *
     * 对于同一个 [ListenerGenerator], [handle] 只能且必须配置 **一次**。如果配置次数超过一次会直接引发 [SimbotIllegalStateException]；
     * 如果未进行配置则会在最终构建时引发 [SimbotIllegalStateException].
     *
     * @throws SimbotIllegalStateException 如果调用超过一次
     *
     */
    @JvmSynthetic
    @ListenerGeneratorDSL
    public fun handle(func: suspend EventListenerProcessingContext.(E) -> EventResult) {
        setFunc(func)
    }
    
  
    
    
    /**
     * 监听函数。处理监听到的事件的具体逻辑。
     *
     * @see handle
     *
     * @throws SimbotIllegalStateException 如果调用超过一次
     */
    @Api4J
    @JvmName("handle")
    @Suppress("FunctionName")
    public fun _handle(func: BiConsumer<EventListenerProcessingContext, E>): ListenerGenerator<E> = also {
        setFunc { e ->
            runWithInterruptible { func.accept(this, e) }
            EventResult.defaults()
        }
    }
    
    /**
     * 监听函数。处理监听到的事件的具体逻辑。
     *
     * @see handle
     *
     * @throws SimbotIllegalStateException 如果调用超过一次
     */
    @Api4J
    @JvmName("handle")
    @Suppress("FunctionName")
    public fun _handle(func: BiFunction<EventListenerProcessingContext, E, EventResult>): ListenerGenerator<E> = also {
        setFunc { e -> runWithInterruptible { func.apply(this, e) } }
    }
    
    internal fun build(): EventListener {
        val id0 = id ?: randomID()
        return simpleListener(
            target = eventKey,
            id = id0,
            isAsync = isAsync,
            logger = logger ?: LoggerFactory.getLogger("love.forte.core.listener.$id0"),
            matcher = matcher ?: { true },
            function = func ?: throw SimbotIllegalStateException("The handle function must be configured")
        )
    }
 
    
}
// endregion


