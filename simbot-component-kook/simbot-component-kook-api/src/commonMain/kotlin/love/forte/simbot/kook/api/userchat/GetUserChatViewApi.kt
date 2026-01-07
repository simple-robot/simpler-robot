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

package love.forte.simbot.kook.api.userchat

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import kotlin.jvm.JvmStatic

/*
/api/v3/user-chat/view	GET
参数列表
参数名	位置	类型	必需	说明
chat_code	query	string	true	私聊会话 Code
 */

/**
 * [获取私信聊天会话详情](https://developer.kookapp.cn/doc/http/user-chat#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D%E8%AF%A6%E6%83%85)
 * @author ForteScarlet
 */
public class GetUserChatViewApi private constructor(
    private val chatCode: String,
) : KookGetApi<UserChatView>() {
    public companion object Factory {
        private val PATH = ApiPath.create("user-chat", "view")

        /**
         * 构造 [获取私信聊天会话详情][GetUserChatViewApi] 请求。
         *
         * @param chatCode 私聊会话 Code
         */
        @JvmStatic
        public fun create(chatCode: String): GetUserChatViewApi = GetUserChatViewApi(chatCode)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<UserChatView>
        get() = UserChatView.serializer()

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters.append("chat_code", chatCode)
    }
}
