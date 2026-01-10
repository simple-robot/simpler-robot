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

package love.forte.simbot.qguild.api.channel.pins

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.PinsMessage
import kotlin.jvm.JvmStatic


/**
 * [获取精华消息](https://bot.q.qq.com/wiki/develop/api/openapi/pins/get_pins_message.html)
 *
 * 用于获取子频道 `channel_id` 内的精华消息。
 *
 * @author ForteScarlet
 */
public class GetPinsMessageApi private constructor(
    channelId: String
) : GetQQGuildApi<PinsMessage>() {
    public companion object Factory : SimpleGetApiDescription("/channels/{channel_id}/pins") {

        /**
         * 构造一个 [GetPinsMessageApi]
         *
         * @param channelId 目标频道ID
         */
        @JvmStatic
        public fun create(channelId: String): GetPinsMessageApi = GetPinsMessageApi(channelId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "pins")

    override val resultDeserializationStrategy: DeserializationStrategy<PinsMessage> get() = PinsMessage.serializer()
}
