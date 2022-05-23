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
import love.forte.simbot.event.EventListener
import love.forte.simbot.utils.runWithInterruptible
import org.slf4j.Logger
import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiFunction


/**
 * 向目标 [EventListener] 外层包装 [EventFilter].
 *
 * @see withMatcher
 */
public operator fun EventListener.plus(filter: EventFilter): EventListener {
    return withMatcher(filter::test)
}

/**
 * 向目标 [EventListener] 外层包装多个 [EventFilter].
 *
 * @see withMatcher
 */
public operator fun EventListener.plus(filters: Iterable<EventFilter>): EventListener {
    val sortedFilters = filters.sortedBy { it.priority }
    return withMatcher {
        sortedFilters.all { filter -> filter.test(this) }
    }
}


/**
 * 构建一个监听函数。
 */
@JvmSynthetic
@Deprecated("Use simpleListener")
public fun <E : Event> coreListener(
    eventKey: Event.Key<E>,
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: suspend EventListenerProcessingContext.(E) -> Any?,
): EventListener {
    return if (blockNext) {
        simpleListener(eventKey, id, isAsync, logger) {
            val result = func(it)
            if (result is EventResult) result else EventResult.of(result, isTruncated = true)
        }
    } else {
        simpleListener(eventKey, id, isAsync, logger) {
            val result = func(it)
            if (result is EventResult) result else EventResult.of(result)
        }
    }
}

/**
 * 构建一个监听函数。
 *
 * ### Fragile API: [E]::class
 * 此内联函数使用了 `reified` 枚举并通过反射获取对应类型的 [Event.Key]. simbot核心模块中更建议你尽可能的减少对存在反射的API的使用。
 */
@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
@JvmSynthetic
@FragileSimbotApi
@Deprecated("Use simpleListener")
public inline fun <reified E : Event> coreListener(
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    noinline func: suspend EventListenerProcessingContext.(E) -> Any?,
): EventListener {
    return coreListener(E::class.getKey(), id, blockNext, isAsync, logger, func)
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
@Deprecated("use simpleListener")
public fun <E : Event> blockingCoreListener(
    eventKey: Event.Key<E>,
    id: ID = UUID.randomUUID().ID,
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: BiFunction<EventListenerProcessingContext, E, Any?>,
): EventListener = blockingSimpleListener(
    target = eventKey,
    id = id,
    isAsync = isAsync,
    logger = logger,
) { t, u ->
    val result = func.apply(t, u)
    
    if (result is EventResult) {
        result
    } else {
        EventResult.of(
            result,
            isTruncated = blockNext
        )
    }
}


/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 *
 */
@Suppress("UNUSED_PARAMETER")
@Api4J
@JvmOverloads
@JvmName("newCoreListener")
@Deprecated("use simpleListener")
public fun <E : Event> blockingCoreListener(
    eventKey: Event.Key<E>,
    id: ID = randomID(),
    blockNext: Boolean = false,
    isAsync: Boolean = false,
    logger: Logger = LoggerFactory.getLogger("love.forte.core.listener.$id"),
    func: BiConsumer<EventListenerProcessingContext, E>,
): EventListener = blockingSimpleListener(
    target = eventKey,
    id = id,
    isAsync = isAsync,
    logger = logger,
) { t, u ->
    func.accept(t, u)
    EventResult.defaults(blockNext)
}


/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 */
@Suppress("UNUSED_PARAMETER")
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
): EventListener = blockingSimpleListener(
    target = Event.Key.getKey(eventType),
    id = id,
    isAsync = isAsync,
    logger = logger
) { t, u ->
    val result = func.apply(t, u)
    if (result is EventResult) result else EventResult.of(result, isTruncated = blockNext)
}

/**
 * 创建一个监听函数。
 *
 * [func] 会在 [runWithInterruptible] 中以 [kotlinx.coroutines.Dispatchers.IO] 作为默认调度器被执行。
 */
@Suppress("UNUSED_PARAMETER")
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
): EventListener = blockingSimpleListener(
    target = Event.Key.getKey(eventType),
    id = id,
    isAsync = isAsync,
    logger = logger
) { t, u ->
    func.accept(t, u)
    EventResult.defaults(blockNext)
}
