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
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.messages.ChannelMessageDetails
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic


/**
 * [获取频道聊天消息详情](https://developer.kookapp.cn/doc/http/message#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E8%AF%A6%E6%83%85)
 *
 * @author ForteScarlet
 */
public class GetChannelMessageViewApi private constructor(
    private val msgId: String,
) : KookGetApi<ChannelMessageDetails>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "view")

        /**
         * 构造一个 [获取频道聊天消息详情][GetChannelMessageViewApi] 请求。
         *
         * @param msgId 消息 id
         */
        @JvmStatic
        public fun create(msgId: String): GetChannelMessageViewApi = GetChannelMessageViewApi(msgId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ChannelMessageDetails>
        get() = ChannelMessageDetails.serializer()

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("msg_id", msgId)
        }
    }
}
