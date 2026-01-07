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

package love.forte.simbot.kook.api.message

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import kotlin.jvm.JvmStatic


/**
 *
 * [获取频道消息某回应的用户列表](https://developer.kookapp.cn/doc/http/message#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E6%B6%88%E6%81%AF%E6%9F%90%E5%9B%9E%E5%BA%94%E7%9A%84%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetChannelMessageReactorListApi private constructor(
    private val msgId: String,
    private val emoji: String,
) : KookGetApi<List<MessageReactor>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "reaction-list")

        private val serializer = ListSerializer(MessageReactor.serializer())

        /**
         * 创建一个 [获取频道消息某回应的用户列表][GetChannelMessageReactorListApi] 实例。
         *
         * @param msgId 频道消息的 `id`
         * @param emoji emoji 的 `id`, 可以为 `GuildEmoji` 或者 `Emoji`
         */
        @JvmStatic
        public fun create(msgId: String, emoji: String): GetChannelMessageReactorListApi =
            GetChannelMessageReactorListApi(msgId, emoji)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<List<MessageReactor>>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters.apply {
            append("msg_id", msgId)
            append("emoji", emoji)
        }
    }
}


/**
 * 某消息的回应用户信息，[GetChannelMessageReactorListApi] 的响应体。
 */
@Serializable
public data class MessageReactor @ApiResultType constructor(
    val id: String,
    val username: String,
    val nickname: String,
    @SerialName("identify_num")
    val identifyNum: String,
    val online: Boolean,
    val status: Int,
    val avatar: String,
    val bot: Boolean,
    @SerialName("reaction_time")
    val reactionTime: Long,
)
