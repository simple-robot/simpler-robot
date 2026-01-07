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

package love.forte.simbot.kook.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.util.BooleanToIntSerializer


/**
 * [服务器 Guild](https://developer.kookapp.cn/doc/objects#%E6%9C%8D%E5%8A%A1%E5%99%A8%20Guild)
 *
 * @author ForteScarlet
 */
public interface Guild {
    /** 服务器id */
    public val id: String

    /** 服务器名称 */
    public val name: String

    /** 服务器主题 */
    public val topic: String

    /** 服务器主的id */
    public val userId: String

    /** 服务器icon的地址 */
    public val icon: String

    /** 通知类型
     * - `0` 代表默认使用服务器通知设置
     * - `1` 代表接收所有通知
     * - `2` 代表仅@被提及
     * - `3` 代表不接收通知
     */
    public val notifyType: Int

    /** 服务器默认使用语音区域 */
    public val region: String

    /** 是否为公开服务器 */
    public val enableOpen: Boolean

    /** 公开服务器id */
    public val openId: String

    /** 默认频道id */
    public val defaultChannelId: String

    /** 欢迎频道id */
    public val welcomeChannelId: String
}

/**
 * 有角色列表和子频道列表的 [Guild] 类型
 *
 */
public interface GuildWithRolesAndChannels : Guild {
    /** 角色列表 */
    public val roles: List<Role>

    /** 频道列表 */
    public val channels: List<Channel>
}

/**
 * [Guild] 的基础实现类型。
 *
 * @see Guild
 */
@Serializable
public data class SimpleGuild(
    override val id: String,
    override val name: String,
    override val topic: String,
    @SerialName("user_id") override val userId: String,
    override val icon: String,
    @SerialName("notify_type") override val notifyType: Int,
    override val region: String,
    @SerialName("enable_open")
    @Serializable(BooleanToIntSerializer::class)
    override val enableOpen: Boolean,
    @SerialName("open_id") override val openId: String,
    @SerialName("default_channel_id") override val defaultChannelId: String,
    @SerialName("welcome_channel_id") override val welcomeChannelId: String
) : Guild


/**
 * [GuildWithRolesAndChannels] 的基础实现类型。
 *
 * @see GuildWithRolesAndChannels
 */
@Serializable
public data class SimpleGuildWithRolesAndChannels(
    override val id: String,
    override val name: String,
    override val topic: String,
    @SerialName("user_id") override val userId: String,
    override val icon: String,
    @SerialName("notify_type") override val notifyType: Int,
    override val region: String,
    @SerialName("enable_open")
    @Serializable(BooleanToIntSerializer::class)
    override val enableOpen: Boolean,
    @SerialName("open_id") override val openId: String,
    @SerialName("default_channel_id") override val defaultChannelId: String,
    @SerialName("welcome_channel_id") override val welcomeChannelId: String,
    override val roles: List<SimpleRole> = emptyList(),
    override val channels: List<SimpleChannel> = emptyList()
) : GuildWithRolesAndChannels
