/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("CoreListenerUtil")

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListener
import love.forte.simbot.randomID
import love.forte.simbot.utils.runWithInterruptible
import org.slf4j.Logger
import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiFunction


/**
 * 向一个 [EventListener] 中拼接一个 [EventFilter].
 */
public operator fun EventListener.plus(filter: EventFilter): EventListener {
    return if (this is EventListenerWithFilter) {
        EventListenerWithFilter(filters + filter, delegate)
    } else {
        EventListenerWithFilter(listOf(filter), this)
    }
}

/**
 * 向一个 [EventListener] 中拼接多个 [EventFilter].
 */
public operator fun EventListener.plus(filters: Iterable<EventFilter>): EventListener {
    return if (this is EventListenerWithFilter) {
        EventListenerWithFilter((this.filters + filters).sortedBy { it.priority }, delegate)
    } else {
        EventListenerWithFilter(filters.sortedBy { it.priority }, this)
    }
}


/**
 * 通过提供一组过滤器来得到一个 [Listener][EventListener].
 */
internal class EventListenerWithFilter(
    internal val filters: List<EventFilter>,
    internal val delegate: EventListener
) : EventListener by delegate {
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        for (filter in filters) {
            if (!filter.test(context)) return filter.defaultResult(context)
        }

        return delegate.invoke(context)
    }

    override fun toString(): String {
        return "$delegate with filter(s)(${filters.size})"
    }
}

/**
 * 向 [EventListenerManager] 中注册一个监听器。
 */
@JvmSynthetic
public fun <E : Event> EventListenerRegistrar.listen(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = coreListener(eventKey, id, blockNext, isAsync, logger, func).also(::register)

/**
 * 向 [EventListenerManager] 中注册一个监听器。
 */
@JvmSynthetic
public inline fun <reified E : Event> EventListenerRegistrar.listen(
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    noinline func: suspend (EventListenerProcessingContext, E) -> Any?
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
    func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = CoreListener(id, eventKey, blockNext, isAsync, logger, func)

/**
 * 构建一个监听函数。
 */
@JvmSynthetic
public inline fun <reified E : Event> coreListener(
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    noinline func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = coreListener(E::class.getKey(), id, blockNext, isAsync, logger, func)


internal class CoreListener<E : Event>(
    override val id: ID,
    private val key: Event.Key<E>,
    private val blockNext: Boolean,
    override val isAsync: Boolean,
    override val logger: Logger,
    private val func: suspend (EventListenerProcessingContext, E) -> Any?
) : EventListener {

    override fun isTarget(eventType: Event.Key<*>): Boolean = eventType.isSubFrom(key)

    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return EventResult.of(func(context, key.safeCast(context.event)!!), blockNext)
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
    func: BiFunction<EventProcessingContext, E, Any?>
): EventListener = coreListener(eventKey, id, blockNext, isAsync, logger) { context, event ->
    runWithInterruptible { func.apply(context, event) }
}


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
    func: BiConsumer<EventProcessingContext, E>
): EventListener = coreListener(eventKey, id, blockNext, isAsync) { c, e ->
    runWithInterruptible { func.accept(c, e) }
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
    func: BiFunction<EventProcessingContext, E, Any?>
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
    func: BiConsumer<EventProcessingContext, E>
): EventListener = blockingCoreListener(Event.Key.getKey(eventType), id, blockNext, isAsync, func)
