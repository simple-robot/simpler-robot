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

import kotlinx.coroutines.withTimeout
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.core.scope.SimpleScope
import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.EventProcessingContext


/**
 * 通过 [EventProcessingContext] 获取并进入 [ContinuousSessionContext] 的作用域中, 可以在存在 [EventProcessingContext] 时搭配使用：
 *
 * ```kotlin
 * suspend fun EventProcessingContext.onEvent(event: FooMessageEvent) {
 *     inSession { // this: ContinuousSessionContext
 *         event.reply("喵~")
 *         val nextMessage = event.nextMessage(FooMessageEvent)
 *         // ...
 *     }
 * }
 * ```
 *
 * 或
 *
 * ```kotlin
 * suspend fun onEvent(context: EventProcessingContext, event: FooMessageEvent) {
 *     context.inSession { // this: ContinuousSessionContext
 *         event.reply("喵~")
 *         val nextMessage = event.nextMessage(FooMessageEvent)
 *         // ...
 *     }
 * }
 * ```
 *
 *
 * ## 超时
 * 如果想要控制整个作用域下的整体超时时间，可以直接通过 [withTimeout] 来包裹作用域：
 * ```kotlin
 * withTimeout(...) {
 *    inSession { // this: ContinuousSessionContext
 *       // ...
 *    }
 * }
 * ```
 *
 * ## 值传递
 * [inSession] 可以向外传递返回值：
 * ```
 * val value: Int = inSession {
 *     // ...
 *     114
 * }
 * ```
 *
 *
 * @throws NullPointerException 当 [EventProcessingContext] 中无法获取 [ContinuousSessionContext] 时。
 *
 */
@ExperimentalSimbotApi
public inline fun <R> EventProcessingContext.inSession(block: ContinuousSessionContext.() -> R): R {
    val session = this[SimpleScope.ContinuousSession]
        ?: throw NullPointerException("Cannot get ContinuousSessionContext from current EventProcessingContext [$this].")
    return session.block()
}
