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

@file:JvmName("CoreListeners")

package love.forte.simbot.core.event

import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.event.Event.Key.Companion.isSub
import love.forte.simbot.event.EventListener
import love.forte.simbot.utils.runWithInterruptible
import org.slf4j.Logger
import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiFunction


/**
 * 向目标 [EventListener] 外层包装 [EventFilter].
 */
public operator fun EventListener.plus(filter: EventFilter): EventListener {
    return proxy { listener ->
        if (!filter.test(this)) return@proxy filter.defaultResult(this)
        listener(this)
    }
}

/**
 * 向目标 [EventListener] 外层包装多个 [EventFilter].
 */
public operator fun EventListener.plus(filters: Iterable<EventFilter>): EventListener {
    val sorted = filters.sortedBy { it.priority }
    return proxy { listener ->
        for (filter in sorted) {
            if (!filter.test(this)) return@proxy filter.defaultResult(this)
        }

        listener(this)
    }
}

/**
 * 提供一层包装来代理目标监听函数。
 */
public fun EventListener.proxy(delegate: suspend EventListenerProcessingContext.(EventListener) -> EventResult): EventListener {
    return EventListenerHandle(this, delegate)
}


/**
 * 通过提供一组过滤器来得到一个 [Listener][EventListener].
 */
internal class EventListenerHandle(
    internal val listener: EventListener,
    internal val handle: suspend EventListenerProcessingContext.(EventListener) -> EventResult,
) : EventListener by listener {
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return context.handle(listener)
        // for (filter in filters) {
        //     if (!filter.test(context)) return filter.defaultResult(context)
        // }
        //
        // return listener.invoke(context)
    }


    override fun toString(): String {
        return "Delegate($listener)"
    }
}

/**
 * 向 [EventListenerManager] 中注册一个监听器。
 *
 * ### Fragile API: [EventListenerRegistrar.register]
 */
@JvmSynthetic
@FragileSimbotApi
public fun <E : Event> EventListenerRegistrar.listen(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: suspend EventListenerProcessingContext.(E) -> Any?,
): EventListener = coreListener(eventKey, id, blockNext, isAsync, logger, func).also(::register)

/**
 * 向 [EventListenerManager] 中注册一个监听器。
 *
 * ### Fragile API: [E]::class
 * 此内联函数使用了 `reified` 枚举并通过反射获取对应类型的 [Event.Key]. simbot核心模块中更建议你尽可能的减少对存在反射的API的使用。
 *
 * ### Fragile API: [EventListenerRegistrar.register]
 */
@JvmSynthetic
@FragileSimbotApi
public inline fun <reified E : Event> EventListenerRegistrar.listen(
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    noinline func: suspend EventListenerProcessingContext.(E) -> Any?,
): EventListener = listen(E::class.getKey(), id, blockNext, isAsync, logger, func)


/**
 * 构建一个监听函数。
 */
@JvmSynthetic
public fun <E : Event> coreListener(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: suspend EventListenerProcessingContext.(E) -> Any?,
): EventListener = CoreListener(id, eventKey, blockNext, isAsync, logger, func)

/**
 * 构建一个监听函数。
 *
 * ### Fragile API: [E]::class
 * 此内联函数使用了 `reified` 枚举并通过反射获取对应类型的 [Event.Key]. simbot核心模块中更建议你尽可能的减少对存在反射的API的使用。
 */
@JvmSynthetic
@FragileSimbotApi
public inline fun <reified E : Event> coreListener(
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    noinline func: suspend (EventListenerProcessingContext, E) -> Any?,
): EventListener = coreListener(E::class.getKey(), id, blockNext, isAsync, logger, func)


private class CoreListener<E : Event>(
    override val id: ID,
    private val key: Event.Key<E>,
    private val blockNext: Boolean,
    override val isAsync: Boolean,
    override val logger: Logger,
    private val func: suspend (EventListenerProcessingContext, E) -> Any?,
) : EventListener {

    override fun isTarget(eventType: Event.Key<*>): Boolean = eventType isSub key

    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        val result = func(context, key.safeCast(context.event)!!)
        return if (result is EventResult) result else EventResult.of(result, blockNext)
    }

}

private class BlockingCoreListener<E : Event>(
    override val id: ID,
    private val key: Event.Key<E>,
    private val blockNext: Boolean,
    override val isAsync: Boolean,
    override val logger: Logger,
    private val func: BiFunction<EventListenerProcessingContext, E, Any?>, // (EventListenerProcessingContext, E) -> Any?
) : EventListener {

    override fun isTarget(eventType: Event.Key<*>): Boolean = eventType isSub key

    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        val result = runWithInterruptible { func.apply(context, key.safeCast(context.event)!!) }
        return if (result is EventResult) result else EventResult.of(result, blockNext)
    }

}


////// create for java

/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 *
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: BiFunction<EventListenerProcessingContext, E, Any?>,
): EventListener =
    BlockingCoreListener(id, eventKey, blockNext, isAsync, logger, func)


/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 *
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventKey: Event.Key<E>,
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: BiConsumer<EventListenerProcessingContext, E>,
): EventListener =
    BlockingCoreListener(
        id, eventKey, blockNext, isAsync, logger
    ) { c, e ->
        func.accept(c, e)
        null
    }


/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventType: Class<E>,
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: BiFunction<EventListenerProcessingContext, E, Any?>,
): EventListener = blockingCoreListener(Event.Key.getKey(eventType), id, blockNext, isAsync, logger, func)

/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventType: Class<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: BiConsumer<EventListenerProcessingContext, E>,
): EventListener = blockingCoreListener(Event.Key.getKey(eventType), id, blockNext, isAsync, logger, func)
