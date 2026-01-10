/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.announces

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.Announces
import kotlin.jvm.JvmStatic


/**
 *
 * 机器人设置消息为指定子频道公告。
 *
 * [创建子频道公告](https://bot.q.qq.com/wiki/develop/api/openapi/announces/post_channel_announces.html)
 *
 *
 * @author ForteScarlet
 */
public class CreateAnnouncesApi private constructor(
    channelId: String,
    private val messageId: String,
) : PostQQGuildApi<Announces>() {

    public companion object Factory : SimplePostApiDescription(
        "/channels/{channel_id}/announces"
    ) {

        /**
         * 构建 [CreateAnnouncesApi]
         * @param channelId 频道ID
         * @param messageId 消息ID
         */
        @JvmStatic
        public fun create(channelId: String, messageId: String): CreateAnnouncesApi =
            CreateAnnouncesApi(channelId, messageId)

    }

    override val path: Array<String> = arrayOf("channels", channelId, "announces")

    override val resultDeserializationStrategy: DeserializationStrategy<Announces>
        get() = Announces.serializer()

    override fun createBody(): Any = Body(messageId)

    @Serializable
    private data class Body(
        @SerialName("message_id") val messageId: String,
    )


}
