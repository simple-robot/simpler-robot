/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleChannel
import kotlin.jvm.JvmStatic


/**
 *
 * [获取子频道列表](https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channels.html)
 *
 * 用于获取 `guild_id` 指定的频道下的子频道列表。
 *
 * @author ForteScarlet
 */
public class GetGuildChannelListApi private constructor(guildId: String) : GetQQGuildApi<List<SimpleChannel>>() {
    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/channels"
    ) {
        private val serializer = ListSerializer(SimpleChannel.serializer())

        /**
         * 构造 [GetGuildChannelListApi]
         */
        @JvmStatic
        public fun create(guildId: String): GetGuildChannelListApi = GetGuildChannelListApi(guildId)
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "channels")

    override val resultDeserializationStrategy: DeserializationStrategy<List<SimpleChannel>>
        get() = serializer
}
