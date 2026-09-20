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
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.EventModelConstructor

/**
 * [消息审核对象(MessageAudited)](https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#%E6%B6%88%E6%81%AF%E5%AE%A1%E6%A0%B8%E5%AF%B9%E8%B1%A1-messageaudited)
 *
 * @property auditId 消息审核 id
 * @property messageId 消息 id，只有审核通过事件才会有值
 * @property guildId 频道 id
 * @property channelId 子频道 id
 * @property auditTime 消息审核时间
 * @property createTime 消息创建时间
 * @property seqInChannel 子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序
 * @author ForteScarlet
 */
@Serializable
public class MessageAudited @EventModelConstructor constructor(
    @SerialName("audit_id") public val auditId: String,
    @SerialName("message_id") public val messageId: String? = null,
    @SerialName("guild_id") public val guildId: String,
    @SerialName("channel_id") public val channelId: String,
    @SerialName("audit_time") public val auditTime: String,
    @SerialName("create_time") public val createTime: String,
    @SerialName("seq_in_channel") public val seqInChannel: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageAudited) return false

        if (auditId != other.auditId) return false
        if (messageId != other.messageId) return false
        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (auditTime != other.auditTime) return false
        if (createTime != other.createTime) return false
        if (seqInChannel != other.seqInChannel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = auditId.hashCode()
        result = 31 * result + (messageId?.hashCode() ?: 0)
        result = 31 * result + guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + auditTime.hashCode()
        result = 31 * result + createTime.hashCode()
        result = 31 * result + seqInChannel.hashCode()
        return result
    }

    override fun toString(): String {
        return "MessageAudited(" +
            "auditId='$auditId', " +
            "messageId=$messageId, " +
            "guildId='$guildId', " +
            "channelId='$channelId', " +
            "auditTime='$auditTime', " +
            "createTime='$createTime', " +
            "seqInChannel='$seqInChannel')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("auditId"))
    public operator fun component1(): String = auditId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("messageId"))
    public operator fun component2(): String? = messageId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component3(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component4(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("auditTime"))
    public operator fun component5(): String = auditTime

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("createTime"))
    public operator fun component6(): String = createTime

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("seqInChannel"))
    public operator fun component7(): String = seqInChannel

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        auditId: String = this.auditId,
        messageId: String? = this.messageId,
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        auditTime: String = this.auditTime,
        createTime: String = this.createTime,
        seqInChannel: String = this.seqInChannel,
    ): MessageAudited = MessageAudited(
        auditId,
        messageId,
        guildId,
        channelId,
        auditTime,
        createTime,
        seqInChannel
    )
    //endregion
}
