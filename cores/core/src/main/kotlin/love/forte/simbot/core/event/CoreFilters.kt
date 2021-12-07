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
@file:JvmName("CoreFilterUtil")
package love.forte.simbot.core.event

import love.forte.simbot.ID
import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventProcessingContext
import java.util.*

/**
 * 构建一个 [EventFilter].
 */
@JvmSynthetic
public fun coreFilter(id: ID = UUID.randomUUID().ID, tester: suspend (context: EventProcessingContext) -> Boolean): EventFilter =
    CoreFilter(id, tester)


private class CoreFilter(
    override val id: ID,
    private val func: suspend (EventProcessingContext) -> Boolean
    ) : EventFilter {

    override suspend fun test(context: EventProcessingContext): Boolean = func(context)
}
