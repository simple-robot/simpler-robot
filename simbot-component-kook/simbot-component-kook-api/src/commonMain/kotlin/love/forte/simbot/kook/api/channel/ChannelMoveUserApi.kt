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

package love.forte.simbot.kook.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic

/**
 * [语音频道之间移动用户](https://developer.kookapp.cn/doc/http/channel#语音频道之间移动用户)
 *
 * 语音频道之间移动用户，只能在语音频道之间移动，用户也必须在其他语音频道在线才能够移动到目标频道。
 *
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
public class ChannelMoveUserApi private constructor(
    targetId: String,
    userIds: List<String>
) : KookPostApi<Unit>() {

    public companion object Factory {
        private val PATH = ApiPath.create("channel", "move-user")

        /**
         * 构造 [ChannelMoveUserApi].
         *
         * @param targetId 目标频道 id，需要是语音频道
         * @param userIds 用户 id 的数组
         */
        @JvmStatic
        public fun create(targetId: String, userIds: List<String>): ChannelMoveUserApi = 
            ChannelMoveUserApi(targetId, userIds)

        /**
         * 构造 [ChannelMoveUserApi].
         *
         * @param targetId 目标频道 id，需要是语音频道
         * @param userIds 用户 id 的数组
         */
        @JvmStatic
        public fun create(targetId: String, vararg userIds: String): ChannelMoveUserApi = 
            ChannelMoveUserApi(targetId, userIds.asList())
    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any = Body(targetId, userIds)

    @Serializable
    private data class Body(
        @SerialName("target_id") 
        val targetId: String,
        @SerialName("user_ids") 
        val userIds: List<String>
    )
}
