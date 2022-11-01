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

@file:JvmName("SimpleListeners")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.utils.runWithInterruptible
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.BiPredicate

/*
 * 消除编译异常:
 * Caused by: java.lang.IllegalArgumentException: suspend default lambda love/forte/simbot/core/event/CoreListeners__SimpleListenerCreateKt$simpleListener$1 cannot be inlined; use a function reference instead
 *
 */

@PublishedApi
@Suppress("RedundantSuspendModifier", "unused", "UNUSED_PARAMETER", "UnusedReceiverParameter")
internal suspend fun <E : Event> EventListenerProcessingContext.defaultMatcher(event: E): Boolean = true


/**
 * 构建一个监听指定的类型的监听函数。
 *
 * e.g.
 *
 * ```kotlin
 * simpleListener(FooEvent) { event: FooEvent -> // this: EventListenerProcessingContext
 *     // do something...
 *
 *     EventResult.defaults() // return EventResult
 * }
 * ```
 *
 */
@JvmSynthetic
public inline fun <E : Event> simpleListener(
    target: Event.Key<E>,
    attributes: AttributeContainer? = null,
    crossinline matcher: suspend EventListenerProcessingContext.(E) -> Boolean = EventListenerProcessingContext::defaultMatcher, // 必须使用函数引用方式。
    crossinline function: suspend EventListenerProcessingContext.(E) -> EventResult,
): EventListener {
    return SimpleListener(setOf(target), attributes, matcher = {
        val event: E = target.safeCast(event)
            ?: throw SimbotIllegalArgumentException("The event type [${event.key}] is not in the target of the current listener function ({id}): [$target]")
        matcher(event)
    }) {
        val event = target.safeCast(event)
            ?: throw SimbotIllegalArgumentException("The event type [${event.key}] is not in the target of the current listener function ({id}): [$target]")
        function(event)
    }
}

/**
 * 构建一个监听多个类型的监听函数。
 *
 * e.g.
 *
 * ```kotlin
 * simpleListener(listOf(FooEvent, BarEvent)) { // this: EventListenerProcessingContext
 *      // do something...
 *
 *      EventResult.defaults() // return type: EventResult
 * }
 * ```
 *
 */
@JvmSynthetic
public inline fun simpleListener(
    targets: Collection<Event.Key<*>>,
    attributes: AttributeContainer? = null,
    crossinline matcher: suspend EventListenerProcessingContext.() -> Boolean = { true },
    crossinline function: suspend EventListenerProcessingContext.() -> EventResult,
): EventListener {
    return SimpleListener(targets.toSet(), attributes, matcher = {
        matcher()
    }) {
        function()
    }
}

/**
 * 构建一个监听指定类型的监听函数。
 *
 * e.g.
 *
 * ```kotlin
 * simpleListener<FooEvent> { event -> // this: EventListenerProcessingContext
 *      // do something...
 *
 *      EventResult.defaults() // return type: EventResult
 * }
 * ```
 *
 * ## FragileSimbotApi
 * 更建议使用
 *
 * ```kotlin
 * simpleListener(FooEvent) {
 *    // ...
 *    EventResult.defaults()
 * }
 * ```
 *
 *
 *
 */
@JvmSynthetic
@FragileSimbotApi
public inline fun <reified E : Event> simpleListener(
    attributes: AttributeContainer? = null,
    crossinline matcher: suspend EventListenerProcessingContext.(E) -> Boolean = { true },
    crossinline function: suspend EventListenerProcessingContext.(E) -> EventResult,
): EventListener {
    return simpleListener(target = E::class.getKey(), attributes, { e ->
        matcher(e)
    }) { e ->
        function(e)
    }
}


/**
 * 向 [EventListenerManager] 中注册一个监听器。
 *
 * ## Fragile API: [EventListenerRegistrar.register]
 */
@JvmSynthetic
@ExperimentalSimbotApi
public inline fun <E : Event> EventListenerRegistrar.listen(
    eventKey: Event.Key<E>,
    attributes: AttributeContainer? = null,
    crossinline matcher: suspend EventListenerProcessingContext.(E) -> Boolean = { true },
    crossinline func: suspend EventListenerProcessingContext.(E) -> EventResult,
): EventListener = simpleListener(target = eventKey, attributes, matcher, func).also(::register)


/**
 * 向 [EventListenerManager] 中注册一个监听器。
 *
 * e.g.
 * ```kotlin
 * registrar.listen<FooEvent> {
 *    // ...
 *    EventResult.defaults()
 * }
 *
 * ```
 *
 * Fragile API: [E]::class
 * 此函数会通过反射获取对应类型的 [Event.Key]. simbot核心模块中更建议你尽可能的减少对存在反射的API的使用。
 *
 * Fragile API: [EventListenerRegistrar.register]
 */
@JvmSynthetic
@ExperimentalSimbotApi
public inline fun <reified E : Event> EventListenerRegistrar.listen(
    attributes: AttributeContainer? = null,
    crossinline matcher: suspend EventListenerProcessingContext.(E) -> Boolean = { true },
    crossinline func: suspend EventListenerProcessingContext.(E) -> EventResult,
): EventListener = listen(E::class.getKey(), attributes, matcher, func)


// region blocking listener
/**
 * 构建一个 [EventListener] 实例。
 * 为不支持挂起函数的使用者准备，例如 Java 。
 *
 * ```java
 * EventListener listener = SimpleListeners.listener(FooEvent.Key, /* 其他参数, */ (context, event) -> {
 *      // ...
 *      return EventResult.defaults();
 * });
 * ```
 *
 */
@Api4J
@JvmName("listener")
@JvmOverloads
public fun <E : Event> blockingSimpleListener(
    target: Event.Key<E>,
    matcher: BiPredicate<EventListenerProcessingContext, E>? = null, // = BiPredicate { _, _ -> true }, // EventListenerProcessingContext.(E) -> Boolean = EventListenerProcessingContext::defaultMatcher, // 必须使用函数引用方式。
    function: BiFunction<EventListenerProcessingContext, E, EventResult>,
): EventListener {
    return simpleListener(target = target,
        matcher = if (matcher == null) {
            { true }
        } else {
            { e -> runWithInterruptible { matcher.test(this, e) } }
        }) { e ->
        runWithInterruptible { function.apply(this, e) }
    }
}


/**
 * 构建一个 [EventListener] 实例。
 * 为不支持挂起函数的使用者准备，例如 Java 。
 *
 * ```java
 * EventListener listener = SimpleListeners.listener(FooEvent.Key, /* 其他参数, */ (context, event) -> {
 *      // ...
 * });
 * ```
 *
 */
@Api4J
@JvmName("listener")
@JvmOverloads
public fun <E : Event> blockingSimpleListenerWithoutResult(
    target: Event.Key<E>,
    matcher: BiPredicate<EventListenerProcessingContext, E>? = null, // = BiPredicate { _, _ -> true }, // EventListenerProcessingContext.(E) -> Boolean = EventListenerProcessingContext::defaultMatcher, // 必须使用函数引用方式。
    function: BiConsumer<EventListenerProcessingContext, E>,
): EventListener {
    return simpleListener(target = target,
        matcher = if (matcher == null) {
            { true }
        } else {
            { e -> runWithInterruptible { matcher.test(this, e) } }
        }) { e ->
        runWithInterruptible { function.accept(this, e) }
        EventResult.invalid()
    }
}
// endregion

