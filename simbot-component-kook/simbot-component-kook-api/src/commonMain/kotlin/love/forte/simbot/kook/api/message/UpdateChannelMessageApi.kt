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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [更新频道聊天消息](https://developer.kookapp.cn/doc/http/message#%E6%9B%B4%E6%96%B0%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public class UpdateChannelMessageApi private constructor(
    private val msgId: String,
    private val content: String,
    private val quote: String? = null,
    private val tempTargetId: String? = null,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "update")

        /**
         * 创建一个 [更新频道聊天消息][UpdateChannelMessageApi] 实例。
         *
         * @param msgId 消息id
         * @param content 消息内容
         * @param quote 回复某条消息的 msgId。如果为空，则代表删除回复，不传则无影响。
         * @param tempTargetId 用户 id，针对特定用户临时更新消息，必须是正常消息才能更新。与发送临时消息概念不同，但同样不保存数据库。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            msgId: String,
            content: String,
            quote: String? = null,
            tempTargetId: String? = null,
        ): UpdateChannelMessageApi = UpdateChannelMessageApi(msgId, content, quote, tempTargetId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any = Body(msgId, content, quote, tempTargetId)

    @Serializable
    private data class Body(
        @SerialName("msg_id")
        val msgId: String,
        val content: String,
        val quote: String? = null,
        @SerialName("temp_target_id")
        val tempTargetId: String? = null,
    )
}


