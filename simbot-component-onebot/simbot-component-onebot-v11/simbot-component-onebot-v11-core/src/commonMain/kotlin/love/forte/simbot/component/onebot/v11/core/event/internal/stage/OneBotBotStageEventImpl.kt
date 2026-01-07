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

package love.forte.simbot.component.onebot.v11.core.event.internal.stage

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.stage.OneBotBotRegisteredEvent
import love.forte.simbot.component.onebot.v11.core.event.stage.OneBotBotStartedEvent

internal class OneBotBotRegisteredEventImpl(
    override val bot: OneBotBot
) : OneBotBotRegisteredEvent {
    override val id: ID = UUID.random()

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    override fun toString(): String =
        "OneBotBotRegisteredEvent(bot=$bot)"
}

internal class OneBotBotStartedEventImpl(
    override val bot: OneBotBot
) : OneBotBotStartedEvent {
    override val id: ID = UUID.random()

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    override fun toString(): String =
        "OneBotBotStartedEvent(bot=$bot)"
}
