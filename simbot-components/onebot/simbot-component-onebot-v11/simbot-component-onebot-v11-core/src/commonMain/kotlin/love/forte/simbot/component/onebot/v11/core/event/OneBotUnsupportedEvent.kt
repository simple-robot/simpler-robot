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

package love.forte.simbot.component.onebot.v11.core.event

import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.onebot.v11.event.UnknownEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation

/**
 * 一个尚未被支持的事件类型。
 *
 * 与 [OneBotUnknownEvent] 不同的是，
 * [OneBotUnknownEvent] 是一个原始事件类型明确的事件，
 * 而 [OneBotUnsupportedEvent] 则不明确。
 * [OneBotUnsupportedEvent] 代表的是那些能够被解析为具体的事件类型，
 * 只不过作为一个 simbot 组件，尚未提供对这种类型的针对性实现。
 *
 * 也因此，[sourceEvent] 的类型不会是 [UnknownEvent]。
 *
 * ### FragileSimbotAPI
 * [OneBotUnsupportedEvent] 被标记为 FragileSimbotAPI,
 * 因为它是一种用于“兜底”的事件类型。
 * 对于某个具体的事件，尚未提供对这种类型的针对性实现，
 * 那么它便会被**临时**包装至 [OneBotUnsupportedEvent] 中。
 * 而随着版本的推进与完善，曾经不被支持的具体事件可能会随后被支持，
 * 而此时，再使用 [OneBotUnsupportedEvent] 将会永远无法接收到此事件。
 *
 * 举个例子，假设如下代码：
 *
 * ```Kotlin
 * suspend fun processEvent(event: OneBotUnsupportedEvent) {
 *     if (event.sourceEvent is FriendAddEvent) {
 *         // 使用 '好友添加申请事件'
 *     }
 * }
 * ```
 * 假设在某个版本 `A` 中，我们尚未提供对 `FriendAddEvent` 这个原始事件的包装体，
 * 那么它便会出现在 [OneBotUnsupportedEvent] 中。为了处理这个事件，
 * 就需要通过类型判断后直接使用原始的事件对象。
 *
 * 而日后，某个版本 `B` 更新，我们提供了一个针对此事件的包装体 `OneBotFriendAddEvent`,
 * 那么自此以后，[OneBotUnsupportedEvent] 中将不会再出现 `FriendAddEvent`，
 * 上述代码的 `if` 判断将始终为 `false` 而导致你的逻辑失效。
 * 而这个过程不会有任何警告或错误，只可能会在更新日志中以新特性的形式提及。
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
@FragileSimbotAPI
public data class OneBotUnsupportedEvent(
    override val sourceEventRaw: String?,
    override val sourceEvent: OBSourceEvent
) : OneBotEvent {
    /**
     * 一个无意义的随机ID。
     */
    override val id: ID = UUID.random()
}
