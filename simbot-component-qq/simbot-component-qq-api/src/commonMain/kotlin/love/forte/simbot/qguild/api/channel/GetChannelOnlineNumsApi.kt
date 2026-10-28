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

package love.forte.simbot.qguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
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
 *
 * @property onlineNums 在线成员数
 */
@Serializable
public class OnlineNumsResult @ApiModelConstructor public constructor(
    @SerialName("online_nums") public val onlineNums: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OnlineNumsResult) return false
        if (onlineNums != other.onlineNums) return false
        return true
    }

    override fun hashCode(): Int = onlineNums

    override fun toString(): String = "OnlineNumsResult(onlineNums=$onlineNums)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("onlineNums"))
    public operator fun component1(): Int = onlineNums

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(onlineNums: Int = this.onlineNums): OnlineNumsResult = OnlineNumsResult(onlineNums)
}
