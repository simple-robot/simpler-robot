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
import kotlin.jvm.JvmStatic


/**
 * [给某个消息添加回应](https://developer.kookapp.cn/doc/http/message#%E7%BB%99%E6%9F%90%E4%B8%AA%E6%B6%88%E6%81%AF%E6%B7%BB%E5%8A%A0%E5%9B%9E%E5%BA%94)
 *
 * @author ForteScarlet
 */
public class AddChannelReactionApi private constructor(
    private val msgId: String,
    private val emoji: String,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "add-reaction")

        /**
         * 创建一个 [给某个消息添加回应][AddChannelReactionApi] 实例。
         *
         * @param msgId 频道消息的 `id`
         * @param emoji emoji 的 `id`, 可以为 `GuildEmoji` 或者 `Emoji`
         */
        @JvmStatic
        public fun create(msgId: String, emoji: String): AddChannelReactionApi =
            AddChannelReactionApi(msgId, emoji)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any = Body(msgId, emoji)

    @Serializable
    private data class Body(
        @SerialName("msg_id")
        val msgId: String,
        val emoji: String,
    )

}
