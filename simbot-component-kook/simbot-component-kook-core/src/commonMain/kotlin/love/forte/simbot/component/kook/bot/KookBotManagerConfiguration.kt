/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.bot

import kotlinx.coroutines.Job
import love.forte.simbot.kook.stdlib.BotConfiguration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 针对 [KookBotManager] 的配置信息。
 */
public class KookBotManagerConfiguration {

    /**
     * 提供给 [KookBotManager] 的协程上下文，会被分配给产生的 [KookBot]。
     * 如果其中存在 [Job], 则会作为其下各个 [KookBot] 的 parent job。
     * 如果 [KookBot.sourceBot] 中已经存在自身的独立 [Job] (通过 [BotConfiguration.coroutineContext] 配置),
     * 则此 parent Job 会与其关联，在终止的同时关闭bot的 [Job]。
     *
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext
}
