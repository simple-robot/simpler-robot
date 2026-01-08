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
import love.forte.simbot.qguild.time.ZERO_ISO_INSTANT

/**
 * [成员对象(Member)](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html)
 */
public interface Member {

    /*
        在 AT MESSAGE 中不会填充user
        但是会填充 author, 可以事后补充
     */

    public val user: User

    /**
     * 用户的昵称
     */
    public val nick: String

    /**
     * 用户在频道内的身份组ID, 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
     */
    public val roles: List<String>

    /**
     * 用户加入频道的时间
     */
    public val joinedAt: String
}

/**
 * [MemberWithGuildID](https://bot.q.qq.com/wiki/develop/api/openapi/member/model.html#memberwithguildid)
 *
 */
public interface MemberWithGuildId : Member {

    /**
     * 频道id
     */
    public val guildId: String
}


/**
 * [Member] 的简单基本实现。
 *
 * @see Member
 */
@ApiModel
@Serializable
public data class SimpleMember(
    /**
     * 用户的频道基础信息，只有成员相关接口中会填充此信息
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
     * 用户加入频道的时间
     */
    @SerialName("joined_at") override val joinedAt: String
) : Member

/**
 * [MemberWithGuildId] 的简单基本实现。
 *
 * @see MemberWithGuildId
 */
@ApiModel
@Serializable
public data class SimpleMemberWithGuildId(

    /**
     * 频道id
     */
    @SerialName("guild_id") override val guildId: String,
    /**
     * 用户的频道基础信息，只有成员相关接口中会填充此信息
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
     * 用户加入频道的时间
     *
     * 如果属性缺失则会使用 [ZERO_ISO_INSTANT]
     */
    @SerialName("join_at") override val joinedAt: String = ZERO_ISO_INSTANT
) : MemberWithGuildId
