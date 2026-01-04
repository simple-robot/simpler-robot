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
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.event.ExpectEventType

/**
 * [群文件上传](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md#群文件上传)
 *
 * @property groupId 群号。
 * @property userId 发送者 QQ 号。
 * @property file 文件信息。
 */
@ExpectEventType(
    postType = RawNoticeEvent.POST_TYPE,
    subType = "group_upload",
)
@Serializable
public data class RawGroupUploadEvent(
    override val time: Long,
    @SerialName("self_id")
    override val selfId: LongID,
    @SerialName("post_type")
    override val postType: String,
    @SerialName("notice_type")
    override val noticeType: String,
    @SerialName("group_id")
    public val groupId: LongID,
    @SerialName("user_id")
    public val userId: LongID,
    public val file: FileInfo,
) : RawNoticeEvent {

    /**
     * [RawGroupUploadEvent] 中的 [文件信息][RawGroupUploadEvent.file]
     *
     * @property id 文件 ID
     * @property name 文件名
     * @property size 文件大小（字节数）
     * @property busid busid（目前不清楚有什么作用）
     */
    @Serializable
    public data class FileInfo(
        val id: ID,
        val name: String,
        val size: Long = -1L,
        val busid: Long = -1L,
    )
}
