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

package love.forte.simbot.kook.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [JoinedChannelEventExtra] 事件体。
 */
@Serializable
public data class JoinedChannelEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 加入的频道 id
     */
    @SerialName("channel_id") val channelId: String,
    /**
     * 加入时间
     */
    @SerialName("joined_at") val joinedAt: Long,
)

/**
 * [用户加入语音频道](https://developer.kookapp.cn/doc/event/user#%E7%94%A8%E6%88%B7%E5%8A%A0%E5%85%A5%E8%AF%AD%E9%9F%B3%E9%A2%91%E9%81%93)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(JoinedChannelEventExtra.TYPE)
public data class JoinedChannelEventExtra(override val body: JoinedChannelEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "joined_channel"
    }
}

/**
 * [JoinedChannelEventExtra] 事件体。
 */
@Serializable
public data class ExitedChannelEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 加入的频道 id
     */
    @SerialName("channel_id") val channelId: String,
    /**
     * 退出时间
     */
    @SerialName("exited_at") val exitedAt: Long,
)

/**
 * [用户退出语音频道](https://developer.kookapp.cn/doc/event/user#%E7%94%A8%E6%88%B7%E9%80%80%E5%87%BA%E8%AF%AD%E9%9F%B3%E9%A2%91%E9%81%93)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(ExitedChannelEventExtra.TYPE)
public data class ExitedChannelEventExtra(override val body: ExitedChannelEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "exited_channel"
    }
}

/**
 * [UserUpdatedEventExtra] 事件体
 */
@Serializable
public data class UserUpdatedEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 用户名
     */
    val username: String,
    /**
     * 头像图片地址
     */
    val avatar: String
)

/**
 * [用户信息更新](https://developer.kookapp.cn/doc/event/user#%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF%E6%9B%B4%E6%96%B0)
 *
 * 该事件与服务器无关, 遵循以下条件
 *
 * - 仅当用户的 用户名 或 头像 变更时;
 * - 仅通知与该用户存在关联的用户或 Bot: a. 存在聊天会话 b. 双方好友关系
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UserUpdatedEventExtra.TYPE)
public data class UserUpdatedEventExtra(override val body: UserUpdatedEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "user_updated"
    }
}
