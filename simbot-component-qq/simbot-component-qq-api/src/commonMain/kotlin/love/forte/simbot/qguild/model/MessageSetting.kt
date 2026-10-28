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

/**
 *
 * [频道消息频率设置对象 (MessageSetting)](https://bot.q.qq.com/wiki/develop/api/openapi/setting/model.html#messagesetting)
 *
 * @property disableCreateDm 是否允许创建私信
 * @property disablePushMsg 是否允许发主动消息
 * @property channelIds 子频道 id 数组
 * @property channelPushMaxNum 每个子频道允许主动推送消息最大消息条数
 * @author ForteScarlet
 */
@Serializable
public class MessageSetting(
    @SerialName("disable_create_dm") public val disableCreateDm: String,
    @SerialName("disable_push_msg") public val disablePushMsg: String,
    @SerialName("channel_ids") public val channelIds: List<String>,
    @SerialName("channel_push_max_num") public val channelPushMaxNum: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageSetting) return false

        if (disableCreateDm != other.disableCreateDm) return false
        if (disablePushMsg != other.disablePushMsg) return false
        if (channelIds != other.channelIds) return false
        if (channelPushMaxNum != other.channelPushMaxNum) return false

        return true
    }

    override fun hashCode(): Int {
        var result = disableCreateDm.hashCode()
        result = 31 * result + disablePushMsg.hashCode()
        result = 31 * result + channelIds.hashCode()
        result = 31 * result + channelPushMaxNum
        return result
    }

    override fun toString(): String {
        return "MessageSetting(" +
            "disableCreateDm='$disableCreateDm', " +
            "disablePushMsg='$disablePushMsg', " +
            "channelIds=$channelIds, " +
            "channelPushMaxNum=$channelPushMaxNum)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("disableCreateDm"))
    public operator fun component1(): String = disableCreateDm

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("disablePushMsg"))
    public operator fun component2(): String = disablePushMsg

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelIds"))
    public operator fun component3(): List<String> = channelIds

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelPushMaxNum"))
    public operator fun component4(): Int = channelPushMaxNum

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        disableCreateDm: String = this.disableCreateDm,
        disablePushMsg: String = this.disablePushMsg,
        channelIds: List<String> = this.channelIds,
        channelPushMaxNum: Int = this.channelPushMaxNum,
    ): MessageSetting = MessageSetting(disableCreateDm, disablePushMsg, channelIds, channelPushMaxNum)
    //endregion
}
