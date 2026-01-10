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

package love.forte.simbot.qguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import kotlin.jvm.JvmStatic


/**
 * [获取在线成员数](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_online_nums.html)
 *
 * 用于查询音视频/直播子频道 channel_id 的在线成员数。
 *
 * @author ForteScarlet
 */
public class GetChannelOnlineNumsApi(channelId: String) : GetQQGuildApi<OnlineNumsResult>() {
    public companion object Factory : SimpleGetApiDescription(
        "/channels/{channel_id}/online_nums"
    ) {
        /**
         * 构造 [GetChannelOnlineNumsApi]
         */
        @JvmStatic
        public fun create(channelId: String): GetChannelOnlineNumsApi = GetChannelOnlineNumsApi(channelId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "online_nums")

    override val resultDeserializationStrategy: DeserializationStrategy<OnlineNumsResult>
        get() = OnlineNumsResult.serializer()
}


/**
 * Result of [GetChannelOnlineNumsApi]
 */
@Serializable
public data class OnlineNumsResult(@SerialName("online_nums") val onlineNums: Int)
