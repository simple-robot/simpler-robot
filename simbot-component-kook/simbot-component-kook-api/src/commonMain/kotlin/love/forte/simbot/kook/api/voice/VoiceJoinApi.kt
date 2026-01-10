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
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [加入语音频道](https://developer.kookapp.cn/doc/http/voice#加入语音频道)
 *
 * 加入某个语音频道，获取推流地址和相关配置信息
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
public class VoiceJoinApi private constructor(
    channelId: String,
    audioSsrc: String?,
    audioPt: String?,
    rtcpMux: Boolean?,
    password: String?
) : KookPostApi<VoiceJoinApi.Result>() {

    public companion object Factory {
        private val PATH = ApiPath.create("voice", "join")

        /**
         * 构造 [VoiceJoinApi].
         *
         * @param channelId 需要加入的语音频道 id
         * @param audioSsrc 传输的语音数据的ssrc，默认为1111。不熟悉慎改。
         * 参见 [RFC 3550](https://www.rfc-editor.org/rfc/rfc3550)
         * @param audioPt 传输的语音数据的payload_type，默认为111。不熟悉慎改。
         * 参见 [RFC 3551](https://www.rfc-editor.org/rfc/rfc3551)
         * @param rtcpMux 将rtcp与rtp使用同一个端口进行传输，默认为true。
         * 参见 [RFC 5761](https://www.rfc-editor.org/rfc/rfc5761)
         * @param password 有些房间需要密码才能进入，可以传入密码
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            channelId: String,
            audioSsrc: String? = null,
            audioPt: String? = null,
            rtcpMux: Boolean? = null,
            password: String? = null
        ): VoiceJoinApi = VoiceJoinApi(channelId, audioSsrc, audioPt, rtcpMux, password)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<Result>
        get() = Result.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any = Body(channelId, audioSsrc, audioPt, rtcpMux, password)

    @Serializable
    private data class Body(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("audio_ssrc")
        val audioSsrc: String? = null,
        @SerialName("audio_pt")
        val audioPt: String? = null,
        @SerialName("rtcp_mux")
        val rtcpMux: Boolean? = null,
        val password: String? = null
    )

    /**
     * 加入语音频道的响应结果
     */
    @Serializable
    public data class Result @ApiResultType constructor(
        /**
         * 媒体服务器的推流ip
         */
        val ip: String,
        /**
         * 媒体服务器的推流端口
         */
        val port: String,
        /**
         * 是否将rtcp与rtp使用同一个端口进行传输，如果为true，推流地址应该为 `rtp://ip:port`
         */
        @SerialName("rtcp_mux")
        val rtcpMux: Boolean = false,
        /**
         * 媒体服务器的rtcp推流端口。如果使用rtcp_mux，请忽略该参数。
         * 如果不使用rtcp_mux，则推流地址中需要写上rtcpport，如: `rtp://xxx:port?rtcpport=rtcpPort`
         */
        @SerialName("rtcp_port")
        val rtcpPort: Int = 0,
        /**
         * 当前语音房间要求的比特率，系统会检测机器人的推流码率，
         * 如果超速（正常推流由于各种原因会在码率上下浮动，请不要超过120%）会被关闭，甚至处罚，请遵照比特率进行推流
         */
        val bitrate: Int = 0,
        /**
         * 最终的ssrc
         */
        @SerialName("audio_ssrc")
        val audioSsrc: String,
        /**
         * 最终的payload_type
         */
        @SerialName("audio_pt")
        val audioPt: String
    )
}
