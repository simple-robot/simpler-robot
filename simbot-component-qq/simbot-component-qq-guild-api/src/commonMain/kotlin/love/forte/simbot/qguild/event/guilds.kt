/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.model.Guild

/**
 * Guild相关事件类型。[data] 类型为 [EventGuild]。
 */
public sealed class EventGuildDispatch : Signal.Dispatch() {
    /**
     * 事件中的频道服务器实例。
     */
    abstract override val data: EventGuild
}

/**
 *
 * [GUILD_CREATE](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#guild-create)
 *
 * ## 发送时机
 * - 机器人被加入到某个频道的时候
 */
@Serializable
@SerialName(EventIntents.Guilds.GUILD_CREATE_TYPE)
@DispatchTypeName(EventIntents.Guilds.GUILD_CREATE_TYPE)
public data class GuildCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventGuild
) : EventGuildDispatch()

/**
 *
 * [GUILD_UPDATE](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#guild-update)
 *
 * ## 发送时机
 * - 频道信息变更
 * - 事件内容为变更后的数据
 */
@Serializable
@SerialName(EventIntents.Guilds.GUILD_UPDATE_TYPE)
@DispatchTypeName(EventIntents.Guilds.GUILD_UPDATE_TYPE)
public data class GuildUpdate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventGuild
) : EventGuildDispatch()

/**
 *
 * [GUILD_DELETE](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#guild-delete)
 *
 * ## 发送时机
 * - 频道被解散
 * - 机器人被移除
 * - 事件内容为变更前的数据
 */
@Serializable
@SerialName(EventIntents.Guilds.GUILD_DELETE_TYPE)
@DispatchTypeName(EventIntents.Guilds.GUILD_DELETE_TYPE)
public data class GuildDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventGuild
) : EventGuildDispatch()


/**
 * [频道事件](https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#%E4%BA%8B%E4%BB%B6%E5%86%85%E5%AE%B9)
 *
 * [Guild] 在作为事件推送时的实现，与 [SimpleGuild][love.forte.simbot.qguild.model.SimpleGuild]
 * 相比多了 `op_user_id` 字段。
 *
 */
@ApiModel
@Serializable
public data class EventGuild(
    /** 频道ID */
    override val id: String,
    /** 频道名称 */
    override val name: String,
    /** 频道头像地址 */
    override val icon: String,
    /** 创建人用户ID */
    @SerialName("owner_id") override val ownerId: String,
    /** 当前人是否是创建人 */
    @SerialName("owner") override val isOwner: Boolean,
    /** 成员数 */
    @SerialName("member_count") override val memberCount: Int,
    /** 最大成员数 */
    @SerialName("max_members") override val maxMembers: Int,
    /** 描述 */
    override val description: String,
    /** 加入时间 */
    @SerialName("joined_at") override val joinedAt: String,
    /** 操作人 */
    @SerialName("op_user_id") val opUserId: String
) : Guild
