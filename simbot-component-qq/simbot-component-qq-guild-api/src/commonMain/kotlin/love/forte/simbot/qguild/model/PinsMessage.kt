/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel


/**
 * [精华消息对象](https://bot.q.qq.com/wiki/develop/api/openapi/pins/model.html)
 *
 * @author ForteScarlet
 */
@Serializable
@ApiModel
public data class PinsMessage(
    /**
     * 频道 id
     */
    @SerialName("guild_id") val guildId: String,
    /**
     * 子频道 id
     */
    @SerialName("channel_id") val channelId: String,
    /**
     * 子频道内精华消息 id 数组
     */
    @SerialName("message_ids") val messageIds: List<String> = emptyList()
)
