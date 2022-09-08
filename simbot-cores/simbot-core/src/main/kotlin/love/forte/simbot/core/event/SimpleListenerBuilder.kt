/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
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
 *
 */

package love.forte.simbot.core.event

import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.message.At
import love.forte.simbot.message.Message
import love.forte.simbot.utils.randomIdStr
import love.forte.simbot.utils.runWithInterruptible
import org.slf4j.Logger
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
 * new SimpleListenerBuilder<>(FooEvent.Key)
 *     .id(...)
 *     .async(...)
 *     .logger(...)
 *     .match((context, event) -> {...})
 *     .handle((context, event) -> { ... })
 *     .build();
 * ```
 *
 * 对于 Kotlin 开发者，可以通过 [buildSimpleListener] 来使用DSL函数构建 [EventListener] 实例。
 * ```kotlin
 * buildSimpleListener(FooEvent.Key) {
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
 *
 *
 * @author ForteScarlet
 */
public class SimpleListenerBuilder<E : Event>(public val target: Event.Key<E>) : EventListenerBuilder {
    private var _id: String? = null
    
    /**
     * 设置listener的ID
     */
    @SimpleListenerBuilderDSL
    override var id: String
        get() = _id ?: ""
        set(value) {
            _id = value
        }
    
    
    /**
     * 使用的日志
     */
    @SimpleListenerBuilderDSL
    @Deprecated("Will be remove", ReplaceWith("this"))
    public var logger: Logger? = null
    
    /**
     * 是否标记为异步函数。
     */
    @SimpleListenerBuilderDSL
    override var isAsync: Boolean = false
    
    /**
     * 当前监听函数的优先级。
     */
    @SimpleListenerBuilderDSL
    override var priority: Int = PriorityConstant.NORMAL
    
    /**
     * 配置当前id。
     *
     * 如果不配置则id随机。
     *
     * @see EventListener.id
     */
    public fun id(id: String): SimpleListenerBuilder<E> = also {
        this.id = id
    }
    
    /**
     * 配置是否为异步函数。
     *
     * @see EventListener.isAsync
     */
    @JvmOverloads
    public fun async(isAsync: Boolean = true): SimpleListenerBuilder<E> = also {
        this.isAsync = isAsync
    }
    
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
            throw SimbotIllegalStateException("Event handle can and can only be configured once")
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
     * 对于同一个 [ListenerGenerator], [handle] 只能且必须配置 **一次**。如果配置次数超过一次会直接引发 [SimbotIllegalStateException]；
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
     *       EventResult.defaults() // return default
     *    }
     * }
     * ```
     *
     * 对于同一个 [ListenerGenerator], [process] 或者 [handle] 只能且必须配置 **一次**。
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
            EventResult.defaults()
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
     * 对于同一个 [ListenerGenerator], [process] 或者 [handle] 只能且必须配置 **一次**。
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
     * 配置 [EventListener.logger].
     *
     * @see EventListener.logger
     */
    @Suppress("UNUSED_PARAMETER")
    @Deprecated("Will be remove", ReplaceWith("this"))
    public fun logger(logger: Logger): SimpleListenerBuilder<E> = this
    
    
    /**
     * 构建并得到目标结果。
     */
    override fun build(): EventListener {
        val id0 = _id ?: randomIdStr()
        return simpleListener(
            target = target,
            id = id0,
            isAsync = isAsync,
            priority = priority,
            matcher = matcher ?: { true },
            function = func ?: throw SimbotIllegalStateException("The handle function must be configured")
        )
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

public fun a() {
    buildSimpleListener(MessageEvent) {
        matchMessage(At) { e, m, i ->
            m.target.literal != "123"
            true
        }
    }
}