/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel

/**
 *
 * [频道对象(Guild)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * 频道对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID -私信场景下的 guild_id 为私信临时频道的 ID，获取私信来源频道信息请使用 `src_guild_id`
 */
public interface Guild {
    /** 频道ID */
    public val id: String

    /** 频道名称 */
    public val name: String

    /** 频道头像地址 */
    public val icon: String

    /** 创建人用户ID */
    public val ownerId: String

    /** 当前人是否是创建人 */
    public val isOwner: Boolean

    /** 成员数 */
    public val memberCount: Int

    /** 最大成员数 */
    public val maxMembers: Int

    /** 描述 */
    public val description: String

    /** 加入时间 */
    public val joinedAt: String
}


/**
 * [Guild] 的基础实现。
 */
@ApiModel
@Serializable
public data class SimpleGuild(
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
    @SerialName("joined_at") override val joinedAt: String
) : Guild

