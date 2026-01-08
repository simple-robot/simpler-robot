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
import love.forte.simbot.qguild.api.PutQQGuildApi
import love.forte.simbot.qguild.api.SimplePutApiDescription
import love.forte.simbot.qguild.model.PinsMessage
import kotlin.jvm.JvmStatic


/**
 * [添加精华消息](https://bot.q.qq.com/wiki/develop/api/openapi/pins/put_pins_message.html)
 *
 * 用于添加子频道 `channel_id` 内的精华消息。
 *
 * - 精华消息在一个子频道内最多只能创建 `20` 条。
 * - 只有可见的消息才能被设置为精华消息。
 * - 接口返回对象中 `message_ids` 为当前请求后子频道内所有精华消息 `message_id` 数组。
 *
 * @author ForteScarlet
 */
public class AddPinsMessageApi private constructor(
    channelId: String, messageId: String
) : PutQQGuildApi<PinsMessage>() {
    public companion object Factory : SimplePutApiDescription("/channels/{channel_id}/pins/{message_id}") {

        /**
         * 构造一个 [AddPinsMessageApi]
         *
         * @param channelId 目标频道ID
         * @param messageId 目标消息ID
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): AddPinsMessageApi =
            AddPinsMessageApi(channelId, messageId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "pins", messageId)

    override val resultDeserializationStrategy: DeserializationStrategy<PinsMessage> get() = PinsMessage.serializer()

    override fun createBody(): Any? = null
}
