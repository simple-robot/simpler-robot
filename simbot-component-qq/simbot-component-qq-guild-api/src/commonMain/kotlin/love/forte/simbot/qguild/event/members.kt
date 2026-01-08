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
import love.forte.simbot.qguild.model.MemberWithGuildId
import love.forte.simbot.qguild.model.User
import love.forte.simbot.qguild.time.ZERO_ISO_INSTANT

/**
 * [`GUILD_MEMBER_ADD`](https://bot.q.qq.com/wiki/develop/api/gateway/guild_member.html#guild-member-add)
 * ## 发送时机
 * - 新用户加入频道
 */
@Serializable
@SerialName(EventIntents.GuildMembers.GUILD_MEMBER_ADD_TYPE)
@DispatchTypeName(EventIntents.GuildMembers.GUILD_MEMBER_ADD_TYPE)
public data class GuildMemberAdd(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventMember
) : Signal.Dispatch()

/**
 * [`GUILD_MEMBER_UPDATE`](https://bot.q.qq.com/wiki/develop/api/gateway/guild_member.html#guild-member-update)
 * ## 发送时机
 * - 用户的频道属性发生变化，如频道昵称，或者身份组
 */
@Serializable
@SerialName(EventIntents.GuildMembers.GUILD_MEMBER_UPDATE_TYPE)
@DispatchTypeName(EventIntents.GuildMembers.GUILD_MEMBER_UPDATE_TYPE)
public data class GuildMemberUpdate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventMember
) : Signal.Dispatch()

/**
 * [`GUILD_MEMBER_REMOVE`](https://bot.q.qq.com/wiki/develop/api/gateway/guild_member.html#guild-member-remove)
 * ## 发送时机
 * - 用户离开频道
 */
@Serializable
@SerialName(EventIntents.GuildMembers.GUILD_MEMBER_REMOVE_TYPE)
@DispatchTypeName(EventIntents.GuildMembers.GUILD_MEMBER_REMOVE_TYPE)
public data class GuildMemberRemove(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventMember
) : Signal.Dispatch()


/**
 * 频道成员事件体。
 *
 * 在 [MemberWithGuildId] 基础上，增加 `op_user_id` 代表操作人。
 */
@ApiModel
@Serializable
public data class EventMember(
    /**
     * 频道id
     */
    @SerialName("guild_id") override val guildId: String,
    /**
     * 用户的频道基础信息
     */
    override val user: User,
    /**
     * 用户的昵称
     */
    override val nick: String,
    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    override val roles: List<String> = emptyList(),

    /**
     * 操作人
     */
    @SerialName("op_user_id")
    public val opUserId: String,

    /**
     * 用户加入频道的时间
     *
     * 如果属性缺失则会使用 [ZERO_ISO_INSTANT]
     */
    @SerialName("join_at") override val joinedAt: String = ZERO_ISO_INSTANT
) : MemberWithGuildId
