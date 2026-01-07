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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType


/**
 * 私信聊天会话信息。
 */
@Serializable
public data class UserChatView @ApiResultType constructor(
    /**
     * 私信会话 Code
     */
    @SerialName("code")
    val code: String,
    /**
     * 上次阅读消息的时间 (毫秒)
     */
    @SerialName("last_read_time")
    val lastReadTime: Long,
    /**
     * 最新消息时间 (毫秒)
     */
    @SerialName("latest_msg_time")
    val latestMsgTime: Long,
    /**
     * 未读消息数
     */
    @SerialName("unread_count")
    val unreadCount: Int,
    /**
     * 是否是好友
     */
    @SerialName("is_friend")
    val isFriend: Boolean,
    /**
     * 是否已屏蔽对方
     */
    @SerialName("is_blocked")
    val isBlocked: Boolean,
    /**
     * 是否已被对方屏蔽
     */
    @SerialName("is_target_blocked")
    val isTargetBlocked: Boolean,
    /**
     * 目标用户信息
     */
    @SerialName("target_info")
    val targetInfo: TargetInfo,
)


/**
 * 目标用户信息
 */
@Serializable
public data class TargetInfo @ApiResultType constructor(
    /**
     * 目标用户 ID
     */
    @SerialName("id")
    val id: String,
    /**
     * 目标用户名
     */
    @SerialName("username")
    val username: String,
    /**
     * 是否在线
     */
    @SerialName("online")
    val online: Boolean,
    /**
     * 头像图片链接
     */
    @SerialName("avatar")
    val avatar: String,
)
