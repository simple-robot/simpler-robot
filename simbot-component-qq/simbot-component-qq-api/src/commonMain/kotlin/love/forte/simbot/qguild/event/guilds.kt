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
public data class GuildCreate @EventModelConstructor constructor(
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
public data class GuildUpdate @EventModelConstructor constructor(
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
public data class GuildDelete @EventModelConstructor constructor(
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
 * @property id 频道ID
 * @property name 频道名称
 * @property icon 频道头像地址
 * @property ownerId 创建人用户ID
 * @property isOwner 当前人是否是创建人
 * @property memberCount 成员数
 * @property maxMembers 最大成员数
 * @property description 描述
 * @property joinedAt 加入时间
 * @property opUserId 操作人
 */

@Serializable
public class EventGuild @EventModelConstructor constructor(
    override val id: String,
    override val name: String,
    override val icon: String,
    @SerialName("owner_id") override val ownerId: String,
    @SerialName("owner") override val isOwner: Boolean,
    @SerialName("member_count") override val memberCount: Int,
    @SerialName("max_members") override val maxMembers: Int,
    override val description: String,
    @SerialName("joined_at") override val joinedAt: String,
    @SerialName("op_user_id") public val opUserId: String
) : Guild {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventGuild) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false
        if (ownerId != other.ownerId) return false
        if (isOwner != other.isOwner) return false
        if (memberCount != other.memberCount) return false
        if (maxMembers != other.maxMembers) return false
        if (description != other.description) return false
        if (joinedAt != other.joinedAt) return false
        if (opUserId != other.opUserId) return false

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
        result = 31 * result + opUserId.hashCode()
        return result
    }

    override fun toString(): String {
        return "EventGuild(" +
            "id='$id', " +
            "name='$name', " +
            "icon='$icon', " +
            "ownerId='$ownerId', " +
            "isOwner=$isOwner, " +
            "memberCount=$memberCount, " +
            "maxMembers=$maxMembers, " +
            "description='$description', " +
            "joinedAt='$joinedAt', " +
            "opUserId='$opUserId')"
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

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("opUserId"))
    public operator fun component10(): String = opUserId

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
        opUserId: String = this.opUserId,
    ): EventGuild = EventGuild(
        id = id,
        name = name,
        icon = icon,
        ownerId = ownerId,
        isOwner = isOwner,
        memberCount = memberCount,
        maxMembers = maxMembers,
        description = description,
        joinedAt = joinedAt,
        opUserId = opUserId,
    )
    //endregion
}
