/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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
import love.forte.simbot.LoggerFactory
import love.forte.simbot.event.*
import love.forte.simbot.utils.*
import org.slf4j.*
import java.util.function.*

@DslMarker
internal annotation class EventListenersGeneratorDSL


/**
 *
 * 用于构建监听函数的构建器，可以同时搭配过滤器使用。
 *
 * 结构示例：
 * ```kotlin
 * // 假设在 CoreListenerManagerConfiguration 中
 * listeners {
 *     // global filters of EventListenersGenerator
 *     filters {
 *         // filter of filters
 *         filter {
 *             println("A!")
 *             true
 *         }
 *         // filter of filters
 *         filter {
 *             println("B!")
 *             true
 *         }
 *     }
 *     // plus listener of EventListenersGenerator
 *     +coreListener(FriendMessageEvent) { _, _ -> /* Nothing here. */ }
 *     // listener of EventListenersGenerator
 *     listener(FriendMessageEvent) {
 *         // multi filters of `listener`
 *         filters {
 *             // generate filter of `filters`
 *             generateFilter {
 *                 // priority of `generateFilter`
 *                 priority = PriorityConstant.LAST
 *                 // test of `generateFilter`
 *                 test { true }
 *             }
 *         }
 *         // filter of `listener`
 *         filter { true }
 *         // generate filter under `listener`
 *         generateFilter {
 *             // filter priority of `generateFilter`
 *             priority = PriorityConstant.FIRST
 *             // test of `generateFilter`
 *             test { true }
 *         }
 *         // handle of `listener`
 *         handle { context, friendMessageEvent ->
 *             // do..
 *             delay(200)
 *             friendMessageEvent.friend().send("Hi! context: $context")
 *         }
 *     }
 * }
 * ```
 *
 *  @author ForteScarlet
 */
@EventListenersGeneratorDSL
public class EventListenersGenerator @InternalSimbotApi constructor(private val end: (List<EventListener>) -> CoreListenerManagerConfiguration) {
    private val listeners = mutableListOf<EventListener>()
    private val globalFilters = mutableListOf<EventFilter>()

    /**
     * 配置全局过滤器。[filters] 之后配置的所有监听函数都会受到其中的过滤器的影响。
     */
    @EventListenersGeneratorDSL
    public fun filters(block: FiltersGenerator<EventListenersGenerator>.() -> Unit): EventListenersGenerator =
        filters().also(block).end()

    /**
     * 配置全局过滤器。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun filters(): FiltersGenerator<EventListenersGenerator> = FiltersGenerator(this, globalFilters)


    @EventListenersGeneratorDSL
    public fun <E : Event> listener(
        eventKey: Event.Key<E>,
        block: ListenerGenerator<E>.() -> Unit
    ): EventListenersGenerator =
        listener(eventKey).also(block).end()


    @Suppress("MemberVisibilityCanBePrivate")
    public fun <E : Event> listener(eventKey: Event.Key<E>): ListenerGenerator<E> =
        ListenerGenerator(eventKey, globalFilters.toMutableList()) {
            listeners.add(it)
            this
        }

    @EventListenersGeneratorDSL
    public fun listener(listener: EventListener): EventListenersGenerator = also {
        listeners.add(listener)
    }

    @EventListenersGeneratorDSL
    public operator fun EventListener.unaryPlus() {
        listeners.add(this)
    }


    /**
     * 回到配置主类.
     */
    public fun end(): CoreListenerManagerConfiguration = end(listeners)

}

//region filter generator
@DslMarker
internal annotation class FiltersGeneratorDSL

/**
 * 过滤器构建器。
 */
@FiltersGeneratorDSL
public class FiltersGenerator<B> @InternalSimbotApi constructor(
    private val backTo: B, private val filters: MutableList<EventFilter>
) {

    /**
     * 使用过滤函数 [tester] 通过 [coreFilter] 构建并添加一个过滤器。
     */
    @FiltersGeneratorForListenersDSL
    @JvmSynthetic
    public fun filter(
        priority: Int = PriorityConstant.NORMAL, tester: suspend (context: EventListenerProcessingContext) -> Boolean
    ) {
        filters.add(coreFilter(priority, tester))
    }


    /**
     * 使用过滤函数 [tester] 通过 [blockingCoreFilter] 构建并添加一个过滤器。
     */
    @Api4J
    @JvmOverloads
    public fun filter(
        priority: Int = PriorityConstant.NORMAL, tester: Predicate<EventListenerProcessingContext>
    ): FiltersGenerator<B> = also {
        filters.add(blockingCoreFilter(priority, tester))
    }


    /**
     * 返回上级配置域
     */
    public fun end(): B = backTo
}
//endregion


//region listener generator
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
public class ListenerGenerator<E : Event> @InternalSimbotApi constructor(
    private val eventKey: Event.Key<E>,
    private val filters: MutableList<EventFilter>,
    private val end: (EventListener) -> EventListenersGenerator
) {


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

    private var func: suspend (EventListenerProcessingContext, E) -> Any? = { _, _ -> null }

    /**
     * 为当前监听函数提供其独立过滤器。
     */
    @ListenerGeneratorDSL
    public fun filters(block: FiltersGeneratorForListeners.() -> Unit): ListenerGenerator<E> = also {
        filters().also(block)
    }

    /**
     * 配置此监听函数的过滤器。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun filters(): FiltersGeneratorForListeners = FiltersGeneratorForListeners(filters)

    /**
     * 构建filter
     */
    @JvmSynthetic
    @ListenerGeneratorDSL
    public fun filter(
        priority: Int = PriorityConstant.NORMAL,
        tester: suspend (context: EventListenerProcessingContext) -> Boolean
    ): ListenerGenerator<E> = also {
        filters.add(coreFilter(priority, tester))
    }

    /**
     * 构建filter
     */
    @Api4J
    @JvmOverloads
    @JvmName("filter")
    @Suppress("FunctionName")
    public fun _filter(
        priority: Int = PriorityConstant.NORMAL,
        tester: Predicate<EventListenerProcessingContext>
    ): ListenerGenerator<E> = also {
        filters.add(coreFilter(priority) { runWithInterruptible { tester.test(it) } })
    }

    /**
     * 构建filter
     */
    @ListenerGeneratorDSL
    public fun generateFilter(block: FilterGenerator<ListenerGenerator<E>>.() -> Unit): ListenerGenerator<E> = also {
        FilterGenerator {
            filters.add(it)
            this
        }.also(block).end()
    }

    /**
     * 监听函数。
     */
    @JvmSynthetic
    @ListenerGeneratorDSL
    public fun handle(func: suspend (EventListenerProcessingContext, E) -> Any?) {
        this.func = func
    }

    /**
     * 监听函数。
     */
    @Api4J
    @JvmName("handle")
    @Suppress("FunctionName")
    public fun _handle(func: BiConsumer<EventListenerProcessingContext, E>): ListenerGenerator<E> = also {
        this.func = { c, e ->
            runWithInterruptible { func.accept(c, e) }
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
        this.func = { c, e -> runWithInterruptible { func.apply(c, e) } }
    }

    private fun build(): EventListener {
        val id0 = id ?: randomID()
        return coreListener(
            eventKey = eventKey,
            id = id0,
            blockNext = blockNext,
            isAsync = isAsync,
            logger = logger ?: LoggerFactory.getLogger("love.forte.core.listener.$id0"),
            func = func
        ) + filters
    }

    /**
     * 返回上级配置域
     */
    public fun end(): EventListenersGenerator = end(build())

}
//endregion


//region filter for listener
@DslMarker
internal annotation class FiltersGeneratorForListenersDSL


/**
 * 在 [ListenerGenerator] 为此监听函数构建过滤器的生成器。
 *
 */
@FiltersGeneratorForListenersDSL
public class FiltersGeneratorForListeners @InternalSimbotApi constructor(
    private val filters: MutableList<EventFilter>
) {


    /**
     * 使用过滤函数 [tester] 通过 [coreFilter] 构建并添加一个过滤器。
     */
    @FiltersGeneratorForListenersDSL
    @JvmSynthetic
    public fun filter(
        priority: Int = PriorityConstant.NORMAL, tester: suspend (context: EventListenerProcessingContext) -> Boolean
    ) {
        filters.add(coreFilter(priority, tester))
    }


    /**
     * 使用过滤函数 [tester] 通过 [blockingCoreFilter] 构建并添加一个过滤器。
     */
    @Api4J
    @JvmOverloads
    @JvmName("filter")
    @Suppress("FunctionName")
    public fun filter(
        priority: Int = PriorityConstant.NORMAL, tester: Predicate<EventListenerProcessingContext>
    ): FiltersGeneratorForListeners = also {
        filters.add(blockingCoreFilter(priority, tester))
    }

    /**
     * 构建filter
     */
    @FiltersGeneratorForListenersDSL
    public fun generateFilter(block: FilterGenerator<FiltersGeneratorForListeners>.() -> Unit): FiltersGeneratorForListeners =
        also {
            FilterGenerator {
                filters.add(it)
                this
            }.also(block).end()
        }
}


//endregion

@DslMarker
internal annotation class FilterGeneratorDSL

/**
 * Filter 生成器
 */
@FilterGeneratorDSL
public class FilterGenerator<B> @InternalSimbotApi constructor(private val end: (EventFilter) -> B) {

    /**
     * 优先级
     */
    @FilterGeneratorDSL
    public var priority: Int = PriorityConstant.NORMAL

    private var tester: suspend (context: EventListenerProcessingContext) -> Boolean = { true }

    /**
     * 匹配规则函数
     */
    @JvmSynthetic
    @FilterGeneratorDSL
    public fun test(tester: suspend (context: EventListenerProcessingContext) -> Boolean) {
        this.tester = tester
    }


    @Suppress("FunctionName")
    @Api4J
    @JvmName("test")
    public fun _test(tester: Predicate<EventListenerProcessingContext>) {
        this.tester = { c -> runWithInterruptible { tester.test(c) } }
    }


    private fun build(): EventFilter {
        return coreFilter(priority, tester)
    }

    public fun end(): B = end(build())
}
