/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.event

import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager


/**
 * 与 Bot 相关的阶段性事件。
 * 例如bot被注册了、bot被启动了。
 *
 * @author ForteScarlet
 */
public interface BotStageEvent : BotEvent {
    /**
     * 相关的 bot.
     */
    override val bot: Bot
}

/**
 * 当一个 Bot 已经在某个 [BotManager] 中被注册后的事件。
 *
 * @author ForteScarlet
 */
public interface BotRegisteredEvent : BotStageEvent

/**
 * 当一个 Bot **首次** 启动成功后的事件。
 *
 * @author ForteScarlet
 */
public interface BotStartedEvent : BotStageEvent
