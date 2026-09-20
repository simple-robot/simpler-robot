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
import love.forte.simbot.qguild.common.QQ

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
 * @property user 用户的频道基础信息，只有成员相关接口中会填充此信息。
 * @property nick 用户的昵称。
 * @property roles 用户在频道内的身份组 ID。
 * @property joinedAt 用户加入频道的时间。
 */
@ApiModel
@Serializable
public class SimpleMember @ApiModelConstructor constructor(
    override val user: User,
    override val nick: String,
    override val roles: List<String> = emptyList(),
    @SerialName("joined_at") override val joinedAt: String
) : Member {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleMember) return false

        if (user != other.user) return false
        if (nick != other.nick) return false
        if (roles != other.roles) return false
        if (joinedAt != other.joinedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + nick.hashCode()
        result = 31 * result + roles.hashCode()
        result = 31 * result + joinedAt.hashCode()
        return result
    }

    override fun toString(): String = "SimpleMember(user=$user, nick='$nick', roles=$roles, joinedAt='$joinedAt')"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("user"))
    public operator fun component1(): User = user

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("nick"))
    public operator fun component2(): String = nick

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roles"))
    public operator fun component3(): List<String> = roles

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("joinedAt"))
    public operator fun component4(): String = joinedAt

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        user: User = this.user,
        nick: String = this.nick,
        roles: List<String> = this.roles,
        joinedAt: String = this.joinedAt,
    ): SimpleMember = SimpleMember(user, nick, roles, joinedAt)
    //endregion
}

/**
 * [MemberWithGuildId] 的简单基本实现。
 *
 * @see MemberWithGuildId
 * @property guildId 频道 ID。
 * @property user 用户的频道基础信息，只有成员相关接口中会填充此信息。
 * @property nick 用户的昵称。
 * @property roles 用户在频道内的身份组 ID。
 * @property joinedAt 用户加入频道的时间。
 */
@ApiModel
@Serializable
public class SimpleMemberWithGuildId @ApiModelConstructor constructor(
    @SerialName("guild_id") override val guildId: String,
    override val user: User,
    override val nick: String,
    override val roles: List<String> = emptyList(),
    @SerialName("join_at") override val joinedAt: String = QQ.ZERO_ISO_INSTANT
) : MemberWithGuildId {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleMemberWithGuildId) return false

        if (guildId != other.guildId) return false
        if (user != other.user) return false
        if (nick != other.nick) return false
        if (roles != other.roles) return false
        if (joinedAt != other.joinedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + nick.hashCode()
        result = 31 * result + roles.hashCode()
        result = 31 * result + joinedAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "SimpleMemberWithGuildId(" +
            "guildId='$guildId', " +
            "user=$user, " +
            "nick='$nick', " +
            "roles=$roles, " +
            "joinedAt='$joinedAt')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("user"))
    public operator fun component2(): User = user

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("nick"))
    public operator fun component3(): String = nick

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roles"))
    public operator fun component4(): List<String> = roles

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("joinedAt"))
    public operator fun component5(): String = joinedAt

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        user: User = this.user,
        nick: String = this.nick,
        roles: List<String> = this.roles,
        joinedAt: String = this.joinedAt,
    ): SimpleMemberWithGuildId = SimpleMemberWithGuildId(guildId, user, nick, roles, joinedAt)
    //endregion
}
