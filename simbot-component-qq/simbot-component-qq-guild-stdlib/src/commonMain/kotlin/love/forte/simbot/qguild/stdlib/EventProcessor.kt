/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("EventProcessors")
@file:JvmMultifileClass

package love.forte.simbot.qguild.stdlib

import love.forte.simbot.qguild.event.Signal
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 用于处理事件的函数接口。
 *
 * 在Java中可以使用 `JBlockEventProcessor`、`JAsyncEventProcessor`
 * 或者 `EventProcessors` 中提供的静态工厂函数，
 * 例如
 * ```java
 * bot.registerProcessor(EventProcessors.block((event, raw) -> { ... }))
 * ```
 *
 */
public fun interface EventProcessor {
    /**
     * 事件处理函数
     */
    @JvmSynthetic
    public suspend operator fun Signal.Dispatch.invoke(raw: String)
}


internal suspend fun EventProcessor.doInvoke(d: Signal.Dispatch, r: String) {
    d.apply { invoke(r) }
}
