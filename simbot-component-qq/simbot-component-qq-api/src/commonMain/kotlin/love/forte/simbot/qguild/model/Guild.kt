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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities

/**
 *
 * [频道对象(Guild)](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * 频道对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID -私信场景下的 guild_id 为私信临时频道的 ID，获取私信来源频道信息请使用 `src_guild_id`
 */
public interface Guild {
    /**
     * 频道ID
     */
    public val id: String

    /**
     * 频道名称
     */
    public val name: String

    /**
     * 频道头像地址
     */
    public val icon: String

    /**
     * 创建人用户ID
     */
    public val ownerId: String

    /**
     * 当前人是否是创建人
     */
    public val isOwner: Boolean

    /**
     * 成员数
     */
    public val memberCount: Int

    /**
     * 最大成员数
     */
    public val maxMembers: Int

    /**
     * 描述
     */
    public val description: String

    /**
     * 加入时间
     */
    public val joinedAt: String
}


/**
 * [Guild] 的基础实现。
 *
 * @property id 频道 ID。
 * @property name 频道名称。
 * @property icon 频道头像地址。
 * @property ownerId 创建人用户 ID。
 * @property isOwner 当前人是否是创建人。
 * @property memberCount 成员数。
 * @property maxMembers 最大成员数。
 * @property description 描述。
 * @property joinedAt 加入时间。
 */
@ApiModel
@Serializable
public class SimpleGuild @ApiModelConstructor constructor(
    override val id: String,
    override val name: String,
    override val icon: String,
    @SerialName("owner_id") override val ownerId: String,
    @SerialName("owner") override val isOwner: Boolean,
    @SerialName("member_count") override val memberCount: Int,
    @SerialName("max_members") override val maxMembers: Int,
    override val description: String,
    @SerialName("joined_at") override val joinedAt: String
) : Guild {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleGuild) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false
        if (ownerId != other.ownerId) return false
        if (isOwner != other.isOwner) return false
        if (memberCount != other.memberCount) return false
        if (maxMembers != other.maxMembers) return false
        if (description != other.description) return false
        if (joinedAt != other.joinedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + isOwner.hashCode()
        result = 31 * result + memberCount
        result = 31 * result + maxMembers
        result = 31 * result + description.hashCode()
        result = 31 * result + joinedAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "SimpleGuild(" +
            "id='$id', " +
            "name='$name', " +
            "icon='$icon', " +
            "ownerId='$ownerId', " +
            "isOwner=$isOwner, " +
            "memberCount=$memberCount, " +
            "maxMembers=$maxMembers, " +
            "description='$description', " +
            "joinedAt='$joinedAt')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
    public operator fun component2(): String = name

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("icon"))
    public operator fun component3(): String = icon

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("ownerId"))
    public operator fun component4(): String = ownerId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("isOwner"))
    public operator fun component5(): Boolean = isOwner

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("memberCount"))
    public operator fun component6(): Int = memberCount

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("maxMembers"))
    public operator fun component7(): Int = maxMembers

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("description"))
    public operator fun component8(): String = description

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("joinedAt"))
    public operator fun component9(): String = joinedAt

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        name: String = this.name,
        icon: String = this.icon,
        ownerId: String = this.ownerId,
        isOwner: Boolean = this.isOwner,
        memberCount: Int = this.memberCount,
        maxMembers: Int = this.maxMembers,
        description: String = this.description,
        joinedAt: String = this.joinedAt,
    ): SimpleGuild = SimpleGuild(
        id, name, icon, ownerId, isOwner, memberCount, maxMembers, description, joinedAt
    )
    //endregion
}
