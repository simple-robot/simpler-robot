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
 * [踢出语音频道中的用户](https://developer.kookapp.cn/doc/http/channel#踢出语音频道中的用户)
 *
 * 踢出语音频道中的用户，只能踢出在语音频道中的用户
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
public class ChannelKickoutApi private constructor(
    channelId: String,
    userId: String
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("channel", "kickout")

        /**
         * 构造 [ChannelKickoutApi].
         *
         * @param channelId 目标频道 id，需要是语音频道
         * @param userId 用户 id
         */
        @JvmStatic
        public fun create(channelId: String, userId: String): ChannelKickoutApi = 
            ChannelKickoutApi(channelId, userId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any = Body(channelId, userId)

    @Serializable
    private data class Body(
        @SerialName("channel_id") 
        val channelId: String,
        @SerialName("user_id") 
        val userId: String
    )
}
