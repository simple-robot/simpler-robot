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

import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import love.forte.simbot.qguild.api.channel.pins.DeletePinsMessageApi.Factory.DELETE_ALL_MESSAGE_ID
import kotlin.jvm.JvmStatic


/**
 * [删除精华消息](https://bot.q.qq.com/wiki/develop/api/openapi/pins/delete_pins_message.html)
 *
 * 用于删除子频道 `channel_id` 下指定 `message_id` 的精华消息。
 *
 * - 删除子频道内全部精华消息，请将 `message_id` 设置为 [`all`][DELETE_ALL_MESSAGE_ID]。
 *
 * @author ForteScarlet
 */
public class DeletePinsMessageApi private constructor(
    channelId: String, messageId: String
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    public companion object Factory : SimpleDeleteApiDescription("/channels/{channel_id}/pins/{message_id}") {

        /**
         * 当要删除的目标为全部时使用的 `message_id` 。
         */
        public const val DELETE_ALL_MESSAGE_ID: String = "all"

        /**
         * 构造一个 [DeletePinsMessageApi]
         *
         * @param channelId 目标频道ID
         * @param messageId 目标消息ID，如果要删除全部则设置为 [`all`][DELETE_ALL_MESSAGE_ID]
         *
         * @see createForDeleteAll
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): DeletePinsMessageApi =
            DeletePinsMessageApi(channelId, messageId)

        /**
         * 构造一个用于删除全部精华消息的 [DeletePinsMessageApi]。
         *
         * @param channelId 目标频道ID
         *
         * @see create
         */
        @JvmStatic
        public fun createForDeleteAll(channelId: String): DeletePinsMessageApi =
            DeletePinsMessageApi(channelId, DELETE_ALL_MESSAGE_ID)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "pins", messageId)
}
