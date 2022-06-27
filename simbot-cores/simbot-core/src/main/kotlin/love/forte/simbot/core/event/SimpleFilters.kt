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
@file:JvmName("SimpleFilterUtil")

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.utils.runWithInterruptible
import java.util.function.Predicate

@Deprecated("Just use SimpleFilterUtil for java")
public object CoreFilterUtil

/**
 * 构建一个 [EventFilter].
 */
@JvmSynthetic
public fun simpleFilter(
    priority: Int = PriorityConstant.NORMAL,
    tester: suspend (context: EventListenerProcessingContext) -> Boolean,
): EventFilter =
    SimpleFilter(priority, tester)


@Deprecated("Just use simpleFilter", ReplaceWith("simpleFilter(priority, tester)"))
public fun coreFilter(
    priority: Int = PriorityConstant.NORMAL,
    tester: suspend (context: EventListenerProcessingContext) -> Boolean,
): EventFilter =
    simpleFilter(priority, tester)


/**
 * 使用阻塞的逻辑构建一个 [EventFilter].
 *
 * [tester] 会在 [runWithInterruptible] 中默认以 [kotlinx.coroutines.Dispatchers.IO] 作为调度器执行。
 *
 * @see simpleFilter
 * @see EventFilter
 */
@Api4J
@JvmOverloads
@JvmName("simpleFilter")
public fun blockingSimpleFilter(
    priority: Int = PriorityConstant.NORMAL,
    tester: Predicate<EventListenerProcessingContext>,
): EventFilter =
    simpleFilter(priority) {
        runWithInterruptible { tester.test(it) }
    }


private class SimpleFilter(
    override val priority: Int,
    private val func: suspend (EventListenerProcessingContext) -> Boolean,
) : EventFilter {
    
    override suspend fun test(context: EventListenerProcessingContext): Boolean = func(context)
}

