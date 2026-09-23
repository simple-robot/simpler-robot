/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.model.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.event.GroupMessageAuthorRole
import kotlin.time.Instant

/**
 * 机器人在 QQ 群内的状态。
 *
 * @property memberOpenid 机器人的群成员 OpenID。
 * @property joinedAt 入群时间，RFC3339 格式。
 * @property allowProactiveMsg 是否接收主动推送。
 * @property recvMsgSetting 接收消息设置：all、only_mention 或 mention_and_context。
 * @property memberRole 群内角色：member、owner 或 admin。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupBotState @ApiModelConstructor internal constructor(
    @SerialName("member_openid") public val memberOpenid: String,
    @SerialName("joined_at") public val joinedAt: Instant,
    @SerialName("allow_proactive_msg") public val allowProactiveMsg: Boolean,
    @SerialName("recv_msg_setting") public val recvMsgSetting: String,
    @SerialName("member_role") public val memberRole: GroupMessageAuthorRole,
) {
    override fun toString(): String {
        return "GroupBotState(" +
            "memberOpenid='$memberOpenid', " +
            "joinedAt=$joinedAt, " +
            "allowProactiveMsg=$allowProactiveMsg, " +
            "recvMsgSetting='$recvMsgSetting', " +
            "memberRole=$memberRole)"
    }
}
