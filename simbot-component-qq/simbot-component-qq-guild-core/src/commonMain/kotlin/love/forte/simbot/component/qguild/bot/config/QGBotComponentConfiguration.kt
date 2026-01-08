/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.bot.config

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.BotConfiguration
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration

/**
 *
 * 在 [BotConfiguration] 的基础上提供更多额外的、针对于组件实现的配置信息。
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class QGBotComponentConfiguration {
    /**
     * 得到 [QQGuild Bot][Bot] 使用的配置。
     *
     * 可直接覆盖，或通过 [`botConfig { ... }`][botConfig] 组合配置。
     *
     */
    public var botConfigure: ConfigurerFunction<ConfigurableBotConfiguration>? = null

    /**
     * 使用 [block] 与当前 [botConfigure] 组合。
     *
     */
    public fun botConfig(block: ConfigurerFunction<ConfigurableBotConfiguration>) {
        val bc = botConfigure
        if (bc == null) {
            botConfigure = block
            return
        }

        botConfigure = ConfigurerFunction {
            bc.invokeWith(this)
            block.invokeWith(this)
        }
    }

    /**
     * 缓存相关配置。
     * 如果设置为 `null` 或设置 [CacheConfig.enable] = `false` 则代表禁用所有缓存。
     *
     * 默认会启用 [TransmitCacheConfig]。
     */
    public var cacheConfig: CacheConfig? = CacheConfig(enable = true, TransmitCacheConfig(enable = true))
}
