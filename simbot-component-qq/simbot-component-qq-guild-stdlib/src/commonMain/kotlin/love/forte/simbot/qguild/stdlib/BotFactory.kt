/*
 * Copyright (c) 2024. ForteScarlet.
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

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.qguild.stdlib.internal.BotImpl
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 用于构建 [Bot] 的工厂类型，提供一些工厂函数。
 */
public object BotFactory {
    /**
     * 构造一个 [Bot].
     */
    @JvmStatic
    @JvmOverloads
    public fun create(appId: String, secret: String, token: String, config: ConfigurableBotConfiguration? = null): Bot =
        create(Bot.Ticket(appId, secret, token), config)

    /**
     * 构造一个 [Bot].
     */
    @JvmStatic
    @JvmOverloads
    public fun create(ticket: Bot.Ticket, config: ConfigurableBotConfiguration? = null): Bot {
        return BotImpl(ticket, (config ?: ConfigurableBotConfiguration()).release())
    }

    /**
     * 构造一个 [Bot].
     */
    @JvmStatic
    public fun create(
        appId: String,
        secret: String,
        token: String,
        config: ConfigurerFunction<ConfigurableBotConfiguration>
    ): Bot =
        create(Bot.Ticket(appId, secret, token), ConfigurableBotConfiguration().invokeBy(config))

    /**
     * 构造一个 [Bot].
     */
    @JvmStatic
    public fun create(ticket: Bot.Ticket, config: ConfigurerFunction<ConfigurableBotConfiguration>): Bot =
        create(ticket, ConfigurableBotConfiguration().invokeBy(config))
}
