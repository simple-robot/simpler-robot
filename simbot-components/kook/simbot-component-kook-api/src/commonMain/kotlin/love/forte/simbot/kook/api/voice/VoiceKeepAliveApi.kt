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

package love.forte.simbot.kook.api.voice

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic

/**
 * [保持语音连接活跃](https://developer.kookapp.cn/doc/http/voice#保持语音连接活跃)
 *
 * 保持语音连接活跃。正常如果长时间断流，系统会回收端口等资源，如果不希望系统回收，
 * 可以每隔45s，调用该接口，保持端口活跃，这样系统不会回收该端口资源
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
public class VoiceKeepAliveApi private constructor(
    channelId: String
) : KookPostApi<Unit>() {

    public companion object Factory {
        private val PATH = ApiPath.create("voice", "keep-alive")

        /**
         * 构造 [VoiceKeepAliveApi].
         *
         * @param channelId 需要保持活跃的语音频道 id
         */
        @JvmStatic
        public fun create(channelId: String): VoiceKeepAliveApi = VoiceKeepAliveApi(channelId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any = Body(channelId)

    @Serializable
    private data class Body(
        @SerialName("channel_id")
        val channelId: String
    )
}
