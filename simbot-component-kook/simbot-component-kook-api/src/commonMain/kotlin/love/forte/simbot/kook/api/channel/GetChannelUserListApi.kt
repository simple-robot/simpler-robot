/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.objects.User
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic

/**
 * [语音频道用户列表](https://developer.kookapp.cn/doc/http/channel#%E8%AF%AD%E9%9F%B3%E9%A2%91%E9%81%93%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
public class GetChannelUserListApi private constructor(
    private val channelId: String,
) : KookGetApi<List<VoiceChannelUser>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("channel", "user-list")

        private val serializer = ListSerializer(VoiceChannelUser.serializer())

        /**
         * 构建 [GetChannelUserListApi]
         * @param channelId 频道id
         */
        @JvmStatic
        public fun create(channelId: String): GetChannelUserListApi = GetChannelUserListApi(channelId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<List<VoiceChannelUser>>
        get() = serializer

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("channel_id", channelId)
        }
    }
}

/**
 * Api [GetChannelUserListApi] 的响应体中的用户信息。
 *
 * 表示在语音频道中的用户信息，包含了用户的基本信息以及在频道中的状态信息。
 *
 * @since 4.2.0
 */
@Serializable
public data class VoiceChannelUser @ApiResultType constructor(
    /**
     * 用户id
     */
    override val id: String,
    /**
     * 用户名
     */
    override val username: String,
    /**
     * 用户的头像的url地址
     */
    override val avatar: String,
    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     */
    @SerialName("identify_num")
    override val identifyNum: String =
        username.substringAfter('#', ""),

    /**
     * 当前是否在线
     */
    @SerialName("online")
    override val isOnline: Boolean = false,
    /**
     * 用户的操作系统
     */
    public val os: String? = null,
    /**
     * 用户的状态, 0和1代表正常，10代表被封禁
     */
    override val status: Int = 0,

    /**
     * vip用户的头像的url地址，可能为gif动图
     */
    @SerialName("vip_avatar")
    override val vipAvatar: String? = null,
    /**
     * 用户在当前服务器的昵称
     */
    override val nickname: String? = null,
    /**
     * 用户在当前服务器中的角色id组成的列表
     */
    override val roles: List<Long> = emptyList(),
    /**
     * 是否为vip用户
     */
    @SerialName("is_vip")
    public val isVip: Boolean = false,
    /**
     * 是否开启了AI降噪
     */
    @SerialName("is_ai_reduce_noise")
    public val isAiReduceNoise: Boolean = false,
    /**
     * 是否有个人资料卡背景
     */
    @SerialName("is_personal_card_bg")
    public val isPersonalCardBg: Boolean = false,
    /**
     * 用户是否为机器人
     */
    @SerialName("bot")
    override val isBot: Boolean = false,
    /**
     * 是否手机号已验证
     */
    @SerialName("mobile_verified")
    override val isMobileVerified: Boolean,
    /**
     * 用户加入服务器的时间戳(毫秒)
     */
    @SerialName("joined_at")
    public val joinedAt: Long = 0L,
    /**
     * 用户最后活跃时间戳(毫秒)
     */
    @SerialName("active_time")
    public val activeTime: Long = 0L,
    /**
     * 用户的直播信息
     */
    @SerialName("live_info")
    public val liveInfo: LiveInfo? = null,
) : User

/**
 * 用户的直播信息
 *
 * @since 4.2.0
 */
@Serializable
public data class LiveInfo @ApiResultType constructor(
    /**
     * 是否在直播中
     */
    @SerialName("in_live")
    public val inLive: Boolean = false,
    /**
     * 观众数量
     */
    @SerialName("audience_count")
    public val audienceCount: Int = 0,
    /**
     * 直播缩略图
     */
    @SerialName("live_thumb")
    public val liveThumb: String = "",
    /**
     * 直播开始时间戳(毫秒)
     */
    @SerialName("live_start_time")
    public val liveStartTime: Long = 0L,
)
