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

package love.forte.simbot.component.onebot.v11.event.notice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.event.ExpectEventType

/**
 * [群管理员变动](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#群管理员变动)
 *
 * @property subType 事件子类型，分别表示设置和取消管理员。
 * 可能的值: `set`、`unset`
 * @property groupId 群号。
 * @property userId 管理员 QQ 号。
 */
@ExpectEventType(
    postType = RawNoticeEvent.POST_TYPE,
    subType = "group_admin",
)
@Serializable
public data class RawGroupAdminEvent(
    override val time: Long,
    @SerialName("self_id")
    override val selfId: LongID,
    @SerialName("post_type")
    override val postType: String,
    @SerialName("notice_type")
    override val noticeType: String,
    @SerialName("sub_type")
    public val subType: String,
    @SerialName("group_id")
    public val groupId: LongID,
    @SerialName("user_id")
    public val userId: LongID,
) : RawNoticeEvent {
    public companion object {
        /**
         * @see RawGroupAdminEvent.subType
         */
        public const val SUB_TYPE_SET: String = "set"

        /**
         * @see RawGroupAdminEvent.subType
         */
        public const val SUB_TYPE_UNSET: String = "unset"
    }
}
