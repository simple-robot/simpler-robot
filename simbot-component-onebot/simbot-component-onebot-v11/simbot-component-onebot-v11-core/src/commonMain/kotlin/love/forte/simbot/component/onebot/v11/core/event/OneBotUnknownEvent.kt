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
 * OneBot组件对一个未知事件 [UnknownEvent] 的包装。
 *
 * 与 [OneBotUnsupportedEvent] 不同的是，
 * [OneBotUnknownEvent] 的事件类型明确为 [UnknownEvent]，
 * 它是在 OneBot 协议本身上的“未知”，也就是指无法解析事件的报文。
 *
 * @author ForteScarlet
 */
@FragileSimbotAPI
@OptIn(FuzzyEventTypeImplementation::class)
public data class OneBotUnknownEvent(
    override val sourceEventRaw: String?,
    override val sourceEvent: UnknownEvent
) : OneBotEvent {
    /**
     * 一个无意义的随机ID。
     */
    override val id: ID = UUID.random()
}
