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
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.qguild.model.ChannelSubType
import love.forte.simbot.qguild.model.ChannelType

/**
 * channel相关的事件类型。[data] 类型为 [EventChannel]。
 *
 */
public sealed class ChannelDispatch : Signal.Dispatch() {
    /**
     * 事件中涉及到的频道类型。
     */
    abstract override val data: EventChannel
}

/**
 * 子频道事件 [CHANNEL_CREATE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-create)
 *
 * ## 发送时机
 * - 子频道被创建
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_CREATE_TYPE)
@DispatchTypeName(EventIntents.Guilds.CHANNEL_CREATE_TYPE)
public data class ChannelCreate @EventModelConstructor constructor(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventChannel
) : ChannelDispatch()

/**
 * 子频道事件 [CHANNEL_UPDATE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-update)
 *
 * ## 发送时机
 * - 子频道信息变更
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_UPDATE_TYPE)
@DispatchTypeName(EventIntents.Guilds.CHANNEL_UPDATE_TYPE)
public data class ChannelUpdate @EventModelConstructor constructor(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventChannel
) : ChannelDispatch()

/**
 * 子频道事件 [CHANNEL_DELETE](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-delete)
 *
 * ## 发送时机
 * - 子频道被删除
 */
@Serializable
@SerialName(EventIntents.Guilds.CHANNEL_DELETE_TYPE)
@DispatchTypeName(EventIntents.Guilds.CHANNEL_DELETE_TYPE)
public data class ChannelDelete @EventModelConstructor constructor(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: EventChannel
) : ChannelDispatch()


/**
 * [子频道事件](https://bot.q.qq.com/wiki/develop/api/gateway/channel.html)
 * 中接收到的 [Channel] 信息。
 *
 * ## 内容
 * 在 [Channel] 的部分字段基础上，增加 `op_user_id` 代表操作人。
 *
 * _Note: [EventChannel] 暂不实现 [Channel]，因为这个 "部分" 还不好界定。_
 *
 */
@Serializable
public class EventChannel @EventModelConstructor constructor(
    /**
     * 子频道 id
     */
    public val id: String,
    /**
     * 频道 id
     */
    @SerialName("guild_id") public val guildId: String,
    /**
     * 子频道名
     */
    public val name: String,
    /**
     * 子频道类型 [ChannelType]
     */
    public val type: ChannelType,
    /**
     * 子频道子类型 [ChannelSubType]
     */
    @SerialName("sub_type") public val subType: ChannelSubType,
    /**
     * 创建人 id
     */
    @SerialName("owner_id") public val ownerId: String,
    /**
     * 操作人
     */
    @SerialName("op_user_id") public val opUserId: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventChannel) return false

        if (id != other.id) return false
        if (guildId != other.guildId) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (subType != other.subType) return false
        if (ownerId != other.ownerId) return false
        if (opUserId != other.opUserId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + subType.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + opUserId.hashCode()
        return result
    }

    override fun toString(): String {
        return "EventChannel(" +
            "id='$id', " +
            "guildId='$guildId', " +
            "name='$name', " +
            "type=$type, " +
            "subType=$subType, " +
            "ownerId='$ownerId', " +
            "opUserId='$opUserId')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component2(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
    public operator fun component3(): String = name

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("type"))
    public operator fun component4(): ChannelType = type

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("subType"))
    public operator fun component5(): ChannelSubType = subType

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("ownerId"))
    public operator fun component6(): String = ownerId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("opUserId"))
    public operator fun component7(): String = opUserId

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        guildId: String = this.guildId,
        name: String = this.name,
        type: ChannelType = this.type,
        subType: ChannelSubType = this.subType,
        ownerId: String = this.ownerId,
        opUserId: String = this.opUserId,
    ): EventChannel = EventChannel(id, guildId, name, type, subType, ownerId, opUserId)
    //endregion
}

