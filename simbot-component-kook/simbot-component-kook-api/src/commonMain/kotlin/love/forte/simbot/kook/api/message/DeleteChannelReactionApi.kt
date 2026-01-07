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
 * [删除消息的某个回应](https://developer.kookapp.cn/doc/http/message#%E5%88%A0%E9%99%A4%E6%B6%88%E6%81%AF%E7%9A%84%E6%9F%90%E4%B8%AA%E5%9B%9E%E5%BA%94)
 *
 * @author ForteScarlet
 */
public class DeleteChannelReactionApi private constructor(
    private val msgId: String,
    private val emoji: String,
    private val userId: String? = null
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "delete-reaction")

        /**
         * 创建一个 [删除消息的某个回应][DeleteChannelReactionApi] 实例。
         *
         * @param msgId 频道消息的 `id`
         * @param emoji emoji 的 `id`, 可以为 `GuildEmoji` 或者 `Emoji`
         * @param userId 用户的 id, 如果不填则为自己的 id。删除别人的 reaction 需要有管理频道消息的权限
         */
        @JvmStatic
        @JvmOverloads
        public fun create(msgId: String, emoji: String, userId: String? = null): DeleteChannelReactionApi =
            DeleteChannelReactionApi(msgId, emoji, userId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any = Body(msgId, emoji, userId)

    @Serializable
    private data class Body(
        @SerialName("msg_id")
        val msgId: String,
        val emoji: String,
        @SerialName("user_id")
        val userId: String?,
    )

}
