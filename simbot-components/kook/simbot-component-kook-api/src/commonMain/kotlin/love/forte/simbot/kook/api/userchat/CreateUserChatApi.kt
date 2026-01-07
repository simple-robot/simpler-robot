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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic


/**
 * [创建私信聊天会话](https://developer.kookapp.cn/doc/http/user-chat#%E5%88%9B%E5%BB%BA%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D)
 *
 * @author ForteScarlet
 */
public class CreateUserChatApi private constructor(
    private val targetId: String
) : KookPostApi<UserChatView>() {
    public companion object Factory {
        private val PATH = ApiPath.create("user-chat", "create")

        /**
         * 构造 [创建私信聊天会话][CreateUserChatApi] 请求。
         *
         * @param targetId 目标用户 id
         */
        @JvmStatic
        public fun create(targetId: String): CreateUserChatApi = CreateUserChatApi(targetId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<UserChatView>
        get() = UserChatView.serializer()

    override fun createBody(): Any = Body(targetId)

    @Serializable
    private data class Body(
        @SerialName("target_id") val targetId: String
    )
}
