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
 * [群成员荣誉变更](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#群成员荣誉变更)、
 * [群红包运气王](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#群红包运气王)、
 * [群内戳一戳](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#群内戳一戳)。
 *
 * 其中，红包和戳一戳的 [subType] 分别为 `lucky_king` 和 `poke`
 *
 * @property subType 提示类型。
 * 可能的值: `honor`, `lucky_king`, `poke`
 * @property groupId 群号。如果是代表私聊相关的事件（例如私聊戳一戳）则为 `null`。
 * @property honorType 荣誉类型，分别表示龙王、群聊之火、快乐源泉。
 * 可能的值: `talkative`、`performer`、`emotion`。
 * 当 [subType] 为 `honor` 时有值。
 * @property userId 成员 QQ 号。
 * @property targetId 当 [subType] 为 `lucky_king` 时代表人气王用户ID，
 * 为 `poke` 时代表被戳的人的ID，否则为 `null`。
 */
@ExpectEventType(
    postType = RawNoticeEvent.POST_TYPE,
    subType = "notify",
)
@Serializable
public data class RawNotifyEvent(
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
    public val groupId: LongID? = null,
    @SerialName("honor_type")
    public val honorType: String? = null,
    @SerialName("user_id")
    public val userId: LongID,
    @SerialName("target_id")
    public val targetId: LongID? = null
) : RawNoticeEvent {
    public companion object {
        /**
         * @see RawNotifyEvent.subType
         */
        public const val SUB_TYPE_HONOR: String = "honor"

        /**
         * @see RawNotifyEvent.subType
         */
        public const val SUB_TYPE_POKE: String = "poke"

        /**
         * @see RawNotifyEvent.subType
         */
        public const val SUB_TYPE_LUCKY_KING: String = "lucky_king"
    }

}
