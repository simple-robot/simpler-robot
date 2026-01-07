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

package love.forte.simbot.kook.stdlib

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.kook.stdlib.internal.BotImpl
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 构建 [Bot] 的工厂。
 *
 * @see Bot
 */
public object BotFactory {

    /**
     * 构建一个尚未启动的 [Bot] 对象。
     *
     * @param ticket bot启动所需的票据信息
     */
    @JvmOverloads
    @JvmStatic
    public fun create(ticket: Ticket, configuration: BotConfiguration = BotConfiguration()): Bot =
        BotImpl(ticket, configuration)

    /**
     * 构建一个尚未启动的 [Bot] 对象。
     *
     * @param ticket bot启动所需的票据信息
     */
    @JvmStatic
    public fun BotFactory.create(ticket: Ticket, configurer: ConfigurerFunction<BotConfiguration>? = null): Bot =
        create(ticket, BotConfiguration().invokeBy(configurer))
}


