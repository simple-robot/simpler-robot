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

package love.forte.simbot.component.onebot.v11.core.event.internal.meta

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.meta.OneBotHeartbeatEvent
import love.forte.simbot.component.onebot.v11.core.event.meta.OneBotLifecycleEvent
import love.forte.simbot.component.onebot.v11.core.event.meta.OneBotMetaEvent
import love.forte.simbot.component.onebot.v11.event.meta.RawHeartbeatEvent
import love.forte.simbot.component.onebot.v11.event.meta.RawLifecycleEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation

/**
 * OneBot中的元事件类型。
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
internal abstract class OneBotMetaEventImpl : OneBotMetaEvent {
    override val id: ID = UUID.random()
}

internal class OneBotHeartbeatEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawHeartbeatEvent,
    override val bot: OneBotBot,
) : OneBotHeartbeatEvent, OneBotMetaEventImpl() {
    override fun toString(): String =
        eventToString("OneBotHeartbeatEvent")
}

internal class OneBotLifecycleEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawLifecycleEvent,
    override val bot: OneBotBot,
) : OneBotLifecycleEvent, OneBotMetaEventImpl() {
    override fun toString(): String =
        eventToString("OneBotLifecycleEvent")
}

