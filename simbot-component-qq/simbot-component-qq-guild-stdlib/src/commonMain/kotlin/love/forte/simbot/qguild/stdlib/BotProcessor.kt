/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.stdlib

import love.forte.simbot.qguild.event.Signal

/**
 * 用当前 [Bot] 订阅一个指定类型 [E] 的事件。
 *
 * ```kotlin
 * bot.process<Signal.Dispatch> { raw ->
 *      // ...
 * }
 * ```
 *
 */
@Deprecated(
    "Use `subscribe`", ReplaceWith("this.subscribe<E>(SubscribeSequence.NORMAL, block)"),
)
public inline fun <reified E : Signal.Dispatch> Bot.process(
    crossinline block: suspend E.(raw: String) -> Unit
): DisposableHandle = subscribe<E>(SubscribeSequence.NORMAL, block)

/**
 * 用当前 [Bot] 订阅一个指定类型 [E] 的事件。
 *
 * ```kotlin
 * bot.subscribe<Signal.Dispatch> { raw ->
 *      // ...
 * }
 * ```
 *
 */
public inline fun <reified E : Signal.Dispatch> Bot.subscribe(
    sequence: SubscribeSequence = SubscribeSequence.NORMAL,
    crossinline block: suspend E.(raw: String) -> Unit
): DisposableHandle {
    return subscribe(sequence) { raw ->
        if (this is E) {
            block(raw)
        }
    }
}
