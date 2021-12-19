/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("CoreListenerUtil")
package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListener
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
    func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = coreListener(eventKey, id, blockNext, isAsync, func).also(::register)

/**
 * 向 [EventListenerManager] 中注册一个监听器。
 */
@JvmSynthetic
public inline fun <reified E : Event> EventListenerRegistrar.listen(
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    noinline func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = listen(E::class.getKey(), id, blockNext, isAsync, func).also(::register)


/**
 * 构建一个监听函数。
 */
@JvmSynthetic
public fun <E : Event> coreListener(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = CoreListener(id, eventKey, blockNext, isAsync, func)

/**
 * 构建一个监听函数。
 */
@JvmSynthetic
public inline fun <reified E : Event> coreListener(
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    noinline func: suspend (EventListenerProcessingContext, E) -> Any?
): EventListener = coreListener(E::class.getKey(), id, blockNext, isAsync, func)


internal class CoreListener<E : Event>(
    override val id: ID,
    private val key: Event.Key<E>,
    private val blockNext: Boolean,
    override val isAsync: Boolean,
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
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    func: BiFunction<EventProcessingContext, E, Any?>
): EventListener = coreListener(eventKey, id, blockNext, isAsync, func::apply)


/**
 * 创建一个监听函数。
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    func: BiConsumer<EventProcessingContext, E>
): EventListener = coreListener(eventKey, id, blockNext, isAsync) { c, e -> func.accept(c, e) }



/**
 * 创建一个监听函数。
 */
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
public fun <E : Event> blockingCoreListener(
    eventType: Class<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    func: BiFunction<EventProcessingContext, E, Any?>
): EventListener = blockingCoreListener(Event.Key.getKey(eventType), id, blockNext, isAsync, func)

/**
 * 创建一个监听函数。
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
