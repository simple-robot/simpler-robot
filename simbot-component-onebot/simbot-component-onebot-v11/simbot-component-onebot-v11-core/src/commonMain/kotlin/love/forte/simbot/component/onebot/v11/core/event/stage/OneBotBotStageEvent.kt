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

package love.forte.simbot.component.onebot.v11.core.event.stage

import love.forte.simbot.bot.BotManager
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.OneBotInternalEvent
import love.forte.simbot.event.BotRegisteredEvent
import love.forte.simbot.event.BotStageEvent
import love.forte.simbot.event.BotStartedEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation


/**
 * OneBot组件中对 [BotStageEvent] 事件的实现类型。
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public interface OneBotBotStageEvent : BotStageEvent, OneBotInternalEvent {
    override val bot: OneBotBot
}

/**
 * 当一个 Bot 已经在某个 [BotManager] 中被注册后的事件。
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotBotRegisteredEvent : OneBotBotStageEvent, BotRegisteredEvent

/**
 * 当一个 Bot **首次** 启动成功后的事件。
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotBotStartedEvent : OneBotBotStageEvent, BotStartedEvent
