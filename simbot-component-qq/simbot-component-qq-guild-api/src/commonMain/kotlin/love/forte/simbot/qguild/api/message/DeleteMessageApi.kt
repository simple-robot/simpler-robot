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

package love.forte.simbot.qguild.api.message

import io.ktor.http.*
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [撤回消息](https://bot.q.qq.com/wiki/develop/api/openapi/message/delete_message.html)
 *
 * 用于撤回子频道 `channel_id` 下的消息 `message_id`。
 *
 * - 管理员可以撤回普通成员的消息。
 * - 频道主可以撤回所有人的消息。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteMessageApi private constructor(
    channelId: String, messageId: String, private val hidetip: Boolean? = null
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    public companion object Factory : SimpleDeleteApiDescription(
        "/channels/{channel_id}/messages/{message_id}"
    ) {

        /**
         * 构建 [DeleteMessageApi]
         *
         * @param hidetip 选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认为fals
         */
        @JvmStatic
        @JvmOverloads
        public fun create(channelId: String, messageId: String, hidetip: Boolean? = null): DeleteMessageApi =
            DeleteMessageApi(channelId, messageId, hidetip)
    }

    // /channels/{channel_id}/messages/{message_id}?hidetip=false
    override val path: Array<String> = arrayOf("channels", channelId, "messages", messageId)

    override fun URLBuilder.buildUrl() {
        hidetip?.also { parameters.append("hidetip", it.toString()) }
    }
}

