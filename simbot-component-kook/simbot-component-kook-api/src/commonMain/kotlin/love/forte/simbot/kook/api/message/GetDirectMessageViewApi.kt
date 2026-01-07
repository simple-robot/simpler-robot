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
import love.forte.simbot.kook.messages.DirectMessageDetails
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic


/**
 * [获取私信聊天消息详情](https://developer.kookapp.cn/doc/http/direct-message#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E8%AF%A6%E6%83%85)
 *
 * @author ForteScarlet
 */
public class GetDirectMessageViewApi private constructor(
    private val chatCode: String,
    private val msgId: String,
) : KookGetApi<DirectMessageDetails>() {
    public companion object Factory {
        private val PATH = ApiPath.create("direct-message", "view")

        /**
         * 构造 [GetDirectMessageViewApi].
         *
         * @param chatCode 私信会话 Code。
         * @param msgId 私聊消息 id
         *
         */
        @JvmStatic
        public fun create(chatCode: String, msgId: String): GetDirectMessageViewApi =
            GetDirectMessageViewApi(chatCode, msgId)

    }

    override val resultDeserializationStrategy: DeserializationStrategy<DirectMessageDetails>
        get() = DirectMessageDetails.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("chat_code", chatCode)
            append("msg_id", msgId)
        }
    }

}
