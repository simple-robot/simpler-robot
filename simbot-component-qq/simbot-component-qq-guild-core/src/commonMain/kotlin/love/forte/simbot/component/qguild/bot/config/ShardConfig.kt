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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.event.Shard

/**
 *
 * 分别信息配置
 *
 * Note: [ShardConfig] 仅用于针对配置文件反序列化使用。
 */
@Serializable
@UsedOnlyForConfigSerialization
public sealed class ShardConfig {

    /**
     * 得到配置的 [Shard]
     */
    public abstract val shard: Shard

    /**
     * 直接使用 [Shard.FULL]。
     *
     * ```json
     * {
     *    "type": "full"
     * }
     * ```
     *
     */
    @Serializable
    @SerialName("full")
    @UsedOnlyForConfigSerialization
    public data object Full : ShardConfig() {
        override val shard: Shard
            get() = Shard.FULL
    }

    /**
     * 与直接构建 [Shard] 近似的方式。
     *
     * ```json
     * {
     *    "type": "simple",
     *    "value": 0,
     *    "total": 1
     * }
     * ```
     *
     * @see Shard
     *
     */
    @Serializable
    @SerialName("simple")
    @UsedOnlyForConfigSerialization
    public data class Simple(val value: Int, val total: Int) : ShardConfig() {
        override val shard: Shard
            get() = Shard(value, total)
    }

}
