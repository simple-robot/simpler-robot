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

import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription

@DslMarker
internal annotation class EventListenersGeneratorDSL

/**
 * 用于构建监听函数的构建器。
 *
 * @see EventListenerRegistrationDescriptionsGenerator
 */
public interface EventListenersGenerator {
    
    /**
     * 直接提供一个 [EventListener] 实例。
     *
     */
    @EventListenersGeneratorDSL
    public fun listener(listener: EventListener): EventListenersGenerator
    
    
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
    public operator fun EventListener.unaryPlus()
}

/**
 *
 * 用于构建监听函数的构建器。
 *
 * 结构示例：
 * ```kotlin
 * // 假设在 SimpleListenerManagerConfiguration 中
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
 *     // use handle function
 *     FooEvent.handle {
 *        // do...
 *        EventResult.of(...)
 *     }
 *
 *     // use process function
 *     FooEvent.process {
 *        // do...
 *     }
 *
 *     // use invoke handle function
 *     // same as FooEvent.process { ... }
 *     FooEvent { // Same as: FooEvent.Key.process { ... }
 *        // do...
 *     }
 * }
 * ```
 *
 *  @author ForteScarlet
 */
@EventListenersGeneratorDSL
public class EventListenerRegistrationDescriptionsGenerator @InternalSimbotApi constructor() : EventListenersGenerator {
    private val listenerRegistrationDescriptions = mutableListOf<() -> EventListenerRegistrationDescription>()
    
    private inline fun add(crossinline block: () -> EventListenerRegistrationDescription): Boolean =
        listenerRegistrationDescriptions.add {
            block()
        }
    
    private fun add(builder: SimpleListenerRegistrationDescriptionBuilder<*>): Boolean =
        listenerRegistrationDescriptions.add {
            builder.buildDescription()
        }
    
    private fun add(description: EventListenerRegistrationDescription): Boolean =
        listenerRegistrationDescriptions.add { description }
    
    
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
        block: SimpleListenerBuilderDslFunction<E>,
    ): EventListenerRegistrationDescriptionsGenerator = apply {
        add(
            SimpleListenerRegistrationDescriptionBuilder(eventKey).apply {
                apply { block.apply { invoke() } }
            }
        )
    }
    
    
    /**
     * 直接提供一个 [EventListener] 实例。
     *
     */
    @EventListenersGeneratorDSL
    public override fun listener(listener: EventListener): EventListenerRegistrationDescriptionsGenerator = apply {
        add { listener.toRegistrationDescription() }
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
    public override operator fun EventListener.unaryPlus() {
        listener(this)
    }
    
    /**
     * 直接提供一个 [EventListenerRegistrationDescription] 实例。
     *
     */
    @EventListenersGeneratorDSL
    public fun listener(listener: EventListenerRegistrationDescription): EventListenerRegistrationDescriptionsGenerator =
        apply {
            add(listener)
        }
    
    
    /**
     * 通过 `+=` 的方式直接提供一个 [EventListenerRegistrationDescription] 实例。
     *
     * ```kotlin
     * listeners {
     *    +fooListenerRegistrationDescription
     * }
     * ```
     *
     */
    @EventListenersGeneratorDSL
    public operator fun EventListenerRegistrationDescription.unaryPlus() {
        listener(this)
    }
    
    
    /**
     * 监听指定的事件类型并直接进行事件处理。
     *
     * 等同于使用 [Event.Key.process]。
     *
     * e.g.
     * ```kotlin
     * FooEvent { event -> // this: EventListenerProcessingContext
     *     // process
     * }
     * ```
     *
     * 相当于：
     * ```kotlin
     * listen(FooEvent) {
     *     process { event -> // this: EventListenerProcessingContext
     *        // process
     *     }
     * }
     * ```
     *
     * 可以在当前构建器上下文中配合 [onMatch] 为当前构建的监听函数提供匹配逻辑.
     *
     * e.g.
     * ```kotlin
     * FooEvent { event: FooEvent -> // this: EventListenerProcessingContext
     *   // process
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
    public inline operator fun <E : Event> Event.Key<E>.invoke(crossinline processFunction: suspend EventListenerProcessingContext.(E) -> Unit): EventHandling<E> {
        return process { event ->
            processFunction(event)
        }
    }
    
    /**
     * 监听指定的事件类型并直接进行事件处理。
     *
     * e.g.
     * ```kotlin
     * FooEvent.handle { event -> // this: EventListenerProcessingContext
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
     * FooEvent.handle { event: FooEvent -> // this: EventListenerProcessingContext
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
    @OptIn(InternalSimbotApi::class)
    @EventListenersGeneratorDSL
    @JvmSynthetic
    public fun <E : Event> Event.Key<E>.handle(handle: suspend EventListenerProcessingContext.(E) -> EventResult): EventHandling<E> {
        val builder = SimpleListenerRegistrationDescriptionBuilder(this)
        builder.handle(handle)
        add(builder)
        return EventHandling(builder)
    }
    
    /**
     * 监听指定的事件类型并直接进行事件处理。
     *
     * e.g.
     * ```kotlin
     * FooEvent.process { event -> // this: EventListenerProcessingContext
     *     // process
     *
     * }
     * ```
     *
     * 相当于：
     * ```kotlin
     * listen(FooEvent) {
     *     process { event -> // this: EventListenerProcessingContext
     *        // process
     *
     *     }
     * }
     * ```
     *
     * 可以在当前构建器上下文中配合 [onMatch] 为当前构建的监听函数提供匹配逻辑.
     *
     * e.g.
     * ```kotlin
     * FooEvent.process { event: FooEvent -> // this: EventListenerProcessingContext
     *   // process
     *
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
    @OptIn(InternalSimbotApi::class)
    @EventListenersGeneratorDSL
    @JvmSynthetic
    public fun <E : Event> Event.Key<E>.process(handle: suspend EventListenerProcessingContext.(E) -> Unit): EventHandling<E> {
        val builder = SimpleListenerRegistrationDescriptionBuilder(this)
        builder.process(handle)
        add(builder)
        return EventHandling(builder)
    }
    
    /**
     * 通过 [Event.Key.invoke] 得到的 _处理过程_ 对象，用于进一步配置此事件的匹配逻辑。
     */
    @JvmInline
    public value class EventHandling<E : Event> @InternalSimbotApi internal constructor(@PublishedApi internal val generator: SimpleListenerRegistrationDescriptionBuilder<E>)
    
    
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
     * 使用 [onMatch] 效果类似于使用 [SimpleListenerRegistrationDescriptionBuilder.match], 当配置多层时相当于通过与(`&&`)连接。
     *
     * @see Event.Key.invoke
     * @see SimpleListenerRegistrationDescriptionBuilder.match
     *
     */
    public inline infix fun <E : Event> EventHandling<E>.onMatch(crossinline matcher: suspend EventListenerProcessingContext.(E) -> Boolean): EventHandling<E> =
        also {
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
     * @see SimpleListenerRegistrationDescriptionBuilder.match
     *
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline fun <E : Event> EventHandling<E>.async(isAsync: Boolean = true): EventHandling<E> = also {
        generator.isAsync = isAsync
    }
    
    /**
     * 得到当前构建的所有 listeners。
     */
    public fun build(): List<EventListenerRegistrationDescription> {
        return listenerRegistrationDescriptions.map { it() }
    }
    
    
    /**
     * 在 [EventListenerRegistrationDescriptionsGenerator] 环境中提供一个可以更简单快捷的构建 [事件结果][EventResult] 的内联函数，
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
    @Suppress("NOTHING_TO_INLINE", "unused", "UnusedReceiverParameter")
    public inline fun EventListenerProcessingContext.eventResult(
        content: Any? = null,
        isTruncated: Boolean = false,
    ): EventResult = EventResult.of(content, isTruncated)
    
    
}


/**
 * 使用于 [EventListenerRegistrationDescriptionsGenerator.listen], 用于兼容Kotlin和Java的函数接口差异。
 */
public fun interface SimpleListenerBuilderDslFunction<E : Event> {
    public operator fun SimpleListenerRegistrationDescriptionBuilder<E>.invoke()
}
