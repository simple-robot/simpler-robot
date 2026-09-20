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
 * [精华消息对象](https://bot.q.qq.com/wiki/develop/api/openapi/pins/model.html)
 *
 * @property guildId 频道 id
 * @property channelId 子频道 id
 * @property messageIds 子频道内精华消息 id 数组
 * @author ForteScarlet
 */
@Serializable
@ApiModel
public class PinsMessage @ApiModelConstructor constructor(
    @SerialName("guild_id") public val guildId: String,
    @SerialName("channel_id") public val channelId: String,
    @SerialName("message_ids") public val messageIds: List<String> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PinsMessage) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (messageIds != other.messageIds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + messageIds.hashCode()
        return result
    }

    override fun toString(): String =
        "PinsMessage(guildId='$guildId', channelId='$channelId', messageIds=$messageIds)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("messageIds"))
    public operator fun component3(): List<String> = messageIds

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        messageIds: List<String> = this.messageIds,
    ): PinsMessage = PinsMessage(guildId, channelId, messageIds)
    //endregion
}
