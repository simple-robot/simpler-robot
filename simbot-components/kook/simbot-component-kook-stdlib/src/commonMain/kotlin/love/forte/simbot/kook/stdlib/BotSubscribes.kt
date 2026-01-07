/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.kook.stdlib

import love.forte.simbot.kook.event.Event
import love.forte.simbot.kook.event.EventExtra


/**
 * 订阅一个具体的 [EventExtra] 类型的事件。
 *
 * ```kotlin
 * bot.subscribe<E> { raw ->
 *      // ...
 * }
 * ```
 *
 * @param subscribeSequence 事件处理器的序列类型，默认为 [SubscribeSequence.NORMAL]
 * @param EX 事件内容 [Event.extra] 的具体类型。
 */
public inline fun <reified EX : EventExtra> Bot.subscribe(
    subscribeSequence: SubscribeSequence = SubscribeSequence.NORMAL,
    crossinline processor: suspend Event<EX>.(raw: String) -> Unit
) {
    subscribe(subscribeSequence) { raw ->
        if (extra is EX) {
            @Suppress("UNCHECKED_CAST")
            processor.invoke(this as Event<EX>, raw)
        }
    }
}

/**
 * 订阅一个 [Event.type] 目标的事件。
 *
 * ```kotlin
 * bot.subscribe(type) { raw ->
 *      // ...
 * }
 * ```
 *
 * @param type 事件类型
 * @param subscribeSequence 事件处理器的序列类型，默认为 [SubscribeSequence.NORMAL]
 */
public inline fun Bot.subscribe(
    type: Event.Type,
    subscribeSequence: SubscribeSequence = SubscribeSequence.NORMAL,
    crossinline processor: suspend Event<*>.(raw: String) -> Unit
) {
    subscribe(subscribeSequence) { raw ->
        if (this.type == type) {
            processor(raw)
        }
    }
}
