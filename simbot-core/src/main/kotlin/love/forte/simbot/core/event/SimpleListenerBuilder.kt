/*
 * Copyright (c) 2022-2023 ForteScarlet.
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

import love.forte.simbot.Api4J
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription
import love.forte.simbot.message.Message
import love.forte.simbot.utils.runWithInterruptible
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.BiPredicate

@DslMarker
internal annotation class SimpleListenerBuilderDSL

/**
 * 用于构建一个 [SimpleListener] 监听函数。
 *
 * [SimpleListenerBuilder] 只能用于配置生成一个具体的事件目标，即只能指定一个具体的 [Event.Key].
 *
 * ```java
 * new SimpleListenerBuilder<>(FooEvent)
 *     .match((context, event) -> {...})
 *     .handle((context, event) -> { ... })
 *     .build();
 * ```
 *
 * 对于 Kotlin 开发者，可以通过 [buildSimpleListener] 来使用DSL函数构建 [EventListener] 实例。
 * ```kotlin
 * buildSimpleListener(FooEvent) {
 *    match { ... }
 *    match { ... }
 *    handle {
 *       ...
 *       ...
 *       EventResult.of(...)
 *    }
 * }
 * ```
 *
 * @see buildSimpleListener
 * @see SimpleListenerRegistrationDescriptionBuilder
 *
 * @author ForteScarlet
 */
public open class SimpleListenerBuilder<E : Event>(public val target: Event.Key<E>) : EventListenerBuilder {
    
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
    @SimpleListenerBuilderDSL
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
    public fun _match(matcher: BiPredicate<EventListenerProcessingContext, E>): SimpleListenerBuilder<E> = also {
        setMatcher { e -> runWithInterruptible { matcher.test(this, e) } }
    }
    
    private var func: (suspend EventListenerProcessingContext.(E) -> EventResult)? = null
    
    private fun setFunc(f: suspend EventListenerProcessingContext.(E) -> EventResult) {
        if (this.func != null) {
            throw SimbotIllegalStateException("Event handle must and must only be configured once")
        }
        
        func = f
    }
    
    /**
     * 监听函数。处理监听到的事件的具体逻辑。
     *
     * ```kotlin
     * listen(FooEvent) {
     *    handle { event: FooEvent -> // this: EventListenerProcessingContext
     *       // handle
     *
     *       EventResult.of(...) // return
     *    }
     * }
     * ```
     *
     * 对于同一个 [SimpleListenerBuilder], [handle] 只能且必须配置 **一次**。如果配置次数超过一次会直接引发 [SimbotIllegalStateException]；
     * 如果未进行配置则会在最终构建时引发 [SimbotIllegalStateException].
     *
     * 如果你不希望总是手动为每个监听函数提供返回值，请参考 [process]。
     *
     * @throws SimbotIllegalStateException 如果调用超过一次
     *
     * @see process
     */
    @JvmSynthetic
    @SimpleListenerBuilderDSL
    public fun handle(func: suspend EventListenerProcessingContext.(E) -> EventResult) {
        setFunc(func)
    }
    
    /**
     * 监听函数。处理监听到的事件的具体逻辑。
     *
     *
     * ```kotlin
     * listen(FooEvent) {
     *    process { event: FooEvent -> // this: EventListenerProcessingContext
     *       // process
     *
     *      // no need to return
     *    }
     * }
     * ```
     * 与 [handle] 不同的是，[process] 函数体内不需要提供返回值。
     * 通过 [process] 注册的逻辑会在最终返回监听默认值 [EventResult.Default]。
     *
     * 上述使用 [process] 的示例代码等同于：
     * ```kotlin
     * listen(FooEvent) {
     *    handle { event: FooEvent -> // this: EventListenerProcessingContext
     *       process() // process function
     *
     *       EventResult.invalid() // return default
     *    }
     * }
     * ```
     *
     * 对于同一个 [SimpleListenerBuilder], [process] 或者 [handle] 只能且必须配置 **一次**。
     * 如果配置次数超过一次会直接引发 [SimbotIllegalStateException]；
     * 如果未进行配置则会在最终构建时引发 [SimbotIllegalStateException].
     *
     * @throws SimbotIllegalStateException 如果调用超过一次
     *
     * @see handle
     *
     */
    @JvmSynthetic
    @SimpleListenerBuilderDSL
    public inline fun process(crossinline func: suspend EventListenerProcessingContext.(E) -> Unit) {
        handle {
            func(it)
            EventResult.invalid()
        }
    }
    
    /**
     * 监听函数。处理监听到的事件的具体逻辑。
     *
     *
     * ```java
     * listen(FooEvent.Key, (builder) -> {
     *     builder.process((context, event) -> {
     *        // process..
     *     });
     * });
     * ```
     * 与 [handle] 不同的是，[process] 函数体内不需要提供返回值。
     * 通过 [process] 注册的逻辑会在最终返回监听默认值 [EventResult.Default]。
     *
     *
     * 对于同一个 [SimpleListenerBuilder], [process] 或者 [handle] 只能且必须配置 **一次**。
     * 如果配置次数超过一次会直接引发 [SimbotIllegalStateException]；
     * 如果未进行配置则会在最终构建时引发 [SimbotIllegalStateException].
     *
     * @throws SimbotIllegalStateException 如果调用超过一次
     *
     * @see handle
     *
     */
    @Api4J
    @JvmName("process")
    public fun process4J(processFunction: BiConsumer<EventListenerProcessingContext, E>): SimpleListenerBuilder<E> =
        apply {
            process { event ->
                runWithInterruptible { processFunction.accept(this, event) }
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
    public fun handle4J(handleFunction: BiFunction<EventListenerProcessingContext, E, EventResult>): SimpleListenerBuilder<E> =
        apply {
            setFunc { e -> runWithInterruptible { handleFunction.apply(this, e) } }
        }
    
    /**
     * 构建并得到目标结果。
     */
    override fun build(): EventListener {
        return simpleListener(
            target = target,
            matcher = matcher ?: { true },
            function = func ?: throw SimbotIllegalStateException("The handle function must be configured")
        )
    }
}


/**
 * 基于 [SimpleListenerBuilder] 构建一个 [SimpleEventListenerRegistrationDescription].
 *
 * @see SimpleListenerBuilder
 *
 */
public class SimpleListenerRegistrationDescriptionBuilder<E : Event>(target: Event.Key<E>) :
    SimpleListenerBuilder<E>(target), EventListenerRegistrationDescriptionBuilder {
    
    /**
     * 监听函数注册时的优先级.
     */
    @SimpleListenerBuilderDSL
    public var priority: Int = EventListenerRegistrationDescription.DEFAULT_PRIORITY
    
    /**
     * 监听函数注册时的优先级.
     */
    @SimpleListenerBuilderDSL
    public fun priority(priority: Int): SimpleListenerRegistrationDescriptionBuilder<E> = apply {
        this.priority = priority
    }
    
    /**
     * 监听函数注册时的异步标记.
     */
    @SimpleListenerBuilderDSL
    public var isAsync: Boolean = EventListenerRegistrationDescription.DEFAULT_ASYNC
    
    /**
     * 监听函数注册时的优先级.
     */
    @SimpleListenerBuilderDSL
    @JvmOverloads
    public fun async(async: Boolean = true): SimpleListenerRegistrationDescriptionBuilder<E> = apply {
        this.isAsync = async
    }
    
    override fun buildDescription(): EventListenerRegistrationDescription {
        return build().toRegistrationDescription(priority = priority, isAsync = isAsync)
    }
    
}

/**
 * 通过 [SimpleListenerBuilder] 构建一个 [EventListener] 实例。
 * ```kotlin
 * buildSimpleListener(FooEvent) {
 *     match { true }
 *     match { true }
 *     handle {
 *         // handle..
 *
 *         EventResult.defaults()
 *     }
 * }
 * ```
 *
 * 或
 *
 *
 * ```kotlin
 * buildSimpleListener(FooEvent) {
 *     match { true }
 *     match { true }
 *     process {
 *         // handle..
 *     }
 * }
 * ```
 *
 *
 */
public inline fun <E : Event> buildSimpleListener(
    target: Event.Key<E>,
    block: SimpleListenerBuilder<E>.() -> Unit,
): EventListener {
    return SimpleListenerBuilder(target).also(block).build()
}

/**
 * 通过 [SimpleListenerRegistrationDescriptionBuilder] 构建一个 [EventListenerRegistrationDescription] 实例。
 * ```kotlin
 * buildSimpleListenerRegistrationDescription(FooEvent) {
 *     isAsync = true
 *     priority = PriorityContact.FIRST
 *     match { true }
 *     match { true }
 *     handle {
 *         // handle..
 *
 *         EventResult.defaults()
 *     }
 * }
 * ```
 *
 * 或
 *
 *
 * ```kotlin
 * buildSimpleListenerRegistrationDescription(FooEvent) {
 *     isAsync = true
 *     priority = PriorityContact.FIRST
 *     match { true }
 *     match { true }
 *     process {
 *         // handle..
 *     }
 * }
 * ```
 *
 *
 */
public inline fun <E : Event> buildSimpleListenerRegistrationDescription(
    target: Event.Key<E>,
    block: SimpleListenerRegistrationDescriptionBuilder<E>.() -> Unit,
): EventListenerRegistrationDescription {
    return SimpleListenerRegistrationDescriptionBuilder(target).also(block).buildDescription()
}


/**
 * 通过 [SimpleListenerBuilder] 构建一个 [EventListener] 实例。
 * ```kotlin
 * buildSimpleListener<FooEvent> {
 *     match { true }
 *     match { true }
 *     handle {
 *         // handle..
 *
 *         EventResult.defaults()
 *     }
 * }
 * ```
 *
 * 更建议使用 [`buildSimpleListener(FooEvent) { ... }`][buildSimpleListener] 这种显示指定 `target key` 的形式。
 *
 * @see buildSimpleListener
 */
@ExperimentalSimbotApi
public inline fun <reified E : Event> buildSimpleListener(block: SimpleListenerBuilder<E>.() -> Unit): EventListener {
    return buildSimpleListener(E::class.getKey(), block)
}

/**
 * 通过 [SimpleListenerRegistrationDescriptionBuilder] 构建一个 [EventListenerRegistrationDescription] 实例。
 * ```kotlin
 * buildSimpleListenerRegistrationDescription<FooEvent> {
 *     isAsync = true
 *     priority = PriorityContact.FIRST
 *     match { true }
 *     match { true }
 *     handle {
 *         // handle..
 *
 *         EventResult.defaults()
 *     }
 * }
 * ```
 *
 * 更建议使用 [`buildSimpleListener(FooEvent) { ... }`][buildSimpleListener] 这种显示指定 `target key` 的形式。
 *
 * @see buildSimpleListenerRegistrationDescription
 */
@ExperimentalSimbotApi
public inline fun <reified E : Event> buildSimpleListenerRegistrationDescription(block: SimpleListenerRegistrationDescriptionBuilder<E>.() -> Unit): EventListenerRegistrationDescription {
    return buildSimpleListenerRegistrationDescription(E::class.getKey(), block)
}

// extra

/**
 * 对一个消息事件 [E] 的 [文本内容][love.forte.simbot.message.MessageContent.plainText] 进行匹配。
 *
 * ```kotlin
 * buildSimpleListener(FooMessageEvent) {
 *    matchText { event: FooMessageEvent -> // this: EventListenerProcessingContext
 *        // ...
 *
 *        "MATCH-TEXT" // 需要被匹配的文本
 *    }
 * }
 * ```
 *
 */
public inline fun <E : MessageEvent> SimpleListenerBuilder<E>.matchText(crossinline textProvider: suspend EventListenerProcessingContext.(E) -> String) {
    match { event ->
        event.messageContent.plainText == textProvider(event)
    }
}


/**
 * 对一个消息事件 [E] 的 [消息元素][love.forte.simbot.message.MessageContent.messages] 中指定的类型进行校验。
 *
 * ```kotlin
 * buildSimpleListener(FooMessageEvent) {
 *   matchMessage(At, require = false) { event: FooMessageEvent, at: At, index: Int -> // this: EventListenerProcessingContext
 *      // ...
 *
 *      at.target.literal != "123"
 *   }
 *  }
 * ```
 *
 */
public inline fun <E : MessageEvent, reified M : Message.Element<M>> SimpleListenerBuilder<E>.matchMessage(
    messageKey: Message.Key<M>,
    require: Boolean = true,
    crossinline matcher: suspend EventListenerProcessingContext.(E, M, index: Int) -> Boolean,
) {
    match { event ->
        var index = -1
        event.messageContent.messages.forEach { m ->
            messageKey.safeCast(m)?.also { targetMsg ->
                index += 1
                if (!matcher(event, targetMsg, index)) {
                    return@match false
                }
            }
        }
        
        !require || index >= 0
    }
}
