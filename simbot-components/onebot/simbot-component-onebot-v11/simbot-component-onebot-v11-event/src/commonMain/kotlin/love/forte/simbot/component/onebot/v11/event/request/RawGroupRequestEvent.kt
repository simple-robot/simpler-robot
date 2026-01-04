/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.event.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.event.ExpectEventType


/**
 * [加群请求／邀请](https://github.com/botuniverse/onebot-11/blob/master/event/request.md#加群请求邀请)
 *
 * @property subType 请求子类型，分别表示加群请求、邀请登录号入群。
 * 可能：`add`、`invite`
 * @property groupId 群号
 * @property userId 发送请求的 QQ 号
 * @property comment 验证信息
 * @property flag 请求 flag，在调用处理请求的 API 时需要传入
 *
 * @author ForteScarlet
 */
@Serializable
@ExpectEventType(postType = RawRequestEvent.POST_TYPE, subType = "group")
public data class RawGroupRequestEvent(
    override val time: Long,
    @SerialName("request_type")
    override val requestType: String,
    @SerialName("self_id")
    override val selfId: LongID,
    @SerialName("post_type")
    override val postType: String,
    @SerialName("sub_type")
    public val subType: String,
    @SerialName("group_id")
    public val groupId: LongID,
    @SerialName("user_id")
    public val userId: LongID,
    public val comment: String = "",
    public val flag: String,
) : RawRequestEvent {

    public companion object {
        /**
         * @see RawGroupRequestEvent.subType
         */
        public const val SUB_TYPE_ADD: String = "add"

        /**
         * @see RawGroupRequestEvent.subType
         */
        public const val SUB_TYPE_INVITE: String = "invite"
    }
}
