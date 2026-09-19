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

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.EventModelConstructor
import love.forte.simbot.qguild.common.QQ
import love.forte.simbot.qguild.model.MemberWithGuildId
import love.forte.simbot.qguild.model.User

/**
 * [`GUILD_MEMBER_ADD`](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/role/guild_member.html)
 * ## 发送时机
 * - 新用户加入频道
 */
@Serializable
@SerialName(EventIntents.GuildMembers.GUILD_MEMBER_ADD_TYPE)
@DispatchTypeName(EventIntents.GuildMembers.GUILD_MEMBER_ADD_TYPE)
public data class GuildMemberAdd @EventModelConstructor constructor(
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
public data class GuildMemberUpdate @EventModelConstructor constructor(
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
public data class GuildMemberRemove @EventModelConstructor constructor(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventMember
) : Signal.Dispatch()


/**
 * 频道成员事件体。
 *
 * 在 [MemberWithGuildId] 基础上，增加 `op_user_id` 代表操作人。
 *
 * @property guildId 频道id
 * @property user 用户的频道基础信息
 * @property nick 用户的昵称
 * @property roles 用户在频道内的身份组ID,
 * 默认值可参考 [DefaultRoles](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html#DefaultRoles)
 * 或 [love.forte.simbot.qguild.model.Role.Companion] 的部分常量值。
 * @property opUserId 操作人
 * @property joinedAt 用户加入频道的时间。如果属性缺失则会使用 [QQ.ZERO_ISO_INSTANT]。
 *
 */
@Serializable
public class EventMember @EventModelConstructor constructor(
    @SerialName("guild_id")
    override val guildId: String,
    override val user: User,
    override val nick: String,
    override val roles: List<String> = emptyList(),
    @SerialName("op_user_id")
    public val opUserId: String,
    @SerialName("join_at")
    override val joinedAt: String = QQ.ZERO_ISO_INSTANT
) : MemberWithGuildId {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventMember) return false

        if (guildId != other.guildId) return false
        if (user != other.user) return false
        if (nick != other.nick) return false
        if (roles != other.roles) return false
        if (opUserId != other.opUserId) return false
        if (joinedAt != other.joinedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + nick.hashCode()
        result = 31 * result + roles.hashCode()
        result = 31 * result + opUserId.hashCode()
        result = 31 * result + joinedAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "EventMember(" +
            "guildId='$guildId', " +
            "user=$user, " +
            "nick='$nick', " +
            "roles=$roles, " +
            "opUserId='$opUserId', " +
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

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("opUserId"))
    public operator fun component5(): String = opUserId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("joinedAt"))
    public operator fun component6(): String = joinedAt

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        user: User = this.user,
        nick: String = this.nick,
        roles: List<String> = this.roles,
        opUserId: String = this.opUserId,
        joinedAt: String = this.joinedAt
    ): EventMember = EventMember(guildId, user, nick, roles, opUserId, joinedAt)
    //endregion
}
