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
 * [JoinedGuildEventExtra] 事件体。
 */
@Serializable
public data class JoinedGuildEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 加入服务器的时间
     */
    @SerialName("joined_at") val joinedAt: Long
)

/**
 * [新成员加入服务器](https://developer.kookapp.cn/doc/event/guild-member#%E6%96%B0%E6%88%90%E5%91%98%E5%8A%A0%E5%85%A5%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(JoinedGuildEventExtra.TYPE)
public data class JoinedGuildEventExtra(override val body: JoinedGuildEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "joined_guild"
    }
}

/**
 * [ExitedGuildEventExtra] 事件体。
 */
@Serializable
public data class ExitedGuildEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 退出服务器的时间
     */
    @SerialName("exited_at") val exitedAt: Long
)

/**
 * [服务器成员退出](https://developer.kookapp.cn/doc/event/guild-member#%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%88%90%E5%91%98%E9%80%80%E5%87%BA)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(ExitedGuildEventExtra.TYPE)
public data class ExitedGuildEventExtra(override val body: ExitedGuildEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "exited_guild"
    }
}

/**
 * [UpdatedGuildMemberEventExtra] 事件体。
 */
@Serializable
public data class UpdatedGuildMemberEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 昵称
     */
    val nickname: String
)

/**
 * [服务器成员信息更新](https://developer.kookapp.cn/doc/event/guild-member#%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%88%90%E5%91%98%E4%BF%A1%E6%81%AF%E6%9B%B4%E6%96%B0)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdatedGuildMemberEventExtra.TYPE)
public data class UpdatedGuildMemberEventExtra(override val body: UpdatedGuildMemberEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_guild_member"
    }
}

/**
 * [GuildMemberOnlineEventExtra] 和 [GuildMemberOfflineEventExtra] 的事件体。
 */
@Serializable
public data class GuildMemberOnlineStatusChangedEventBody(
    /**
     * 用户 id
     */
    @SerialName("user_id") val userId: String,
    /**
     * 事件发生的时间
     */
    @SerialName("event_time") val eventTime: Long,
    /**
     * 服务器 id 组成的数组, 代表与该用户所在的共同的服务器
     */
    val guilds: List<String> = emptyList()
)

/**
 * [服务器成员上线](https://developer.kookapp.cn/doc/event/guild-member#%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%88%90%E5%91%98%E4%B8%8A%E7%BA%BF)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(GuildMemberOnlineEventExtra.TYPE)
public data class GuildMemberOnlineEventExtra(override val body: GuildMemberOnlineStatusChangedEventBody) :
    SystemExtra() {
    public companion object {
        public const val TYPE: String = "guild_member_online"
    }
}

/**
 * [服务器成员下线](https://developer.kookapp.cn/doc/event/guild-member#%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%88%90%E5%91%98%E4%B8%8B%E7%BA%BF)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(GuildMemberOfflineEventExtra.TYPE)
public data class GuildMemberOfflineEventExtra(override val body: GuildMemberOnlineStatusChangedEventBody) :
    SystemExtra() {
    public companion object {
        public const val TYPE: String = "guild_member_offline"
    }
}
