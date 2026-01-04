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

package love.forte.simbot.component.onebot.v11.core.event.notice

import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupUploadEvent
import love.forte.simbot.event.ChatGroupEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.suspendrunner.STP


/**
 * 群文件上传事件
 *
 * @see RawGroupUploadEvent
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupUploadEvent : OneBotNoticeEvent, ChatGroupEvent {
    override val sourceEvent: RawGroupUploadEvent

    /**
     * 群号
     */
    public val groupId: LongID
        get() = sourceEvent.groupId

    /**
     * 上传者ID
     */
    public val userId: LongID
        get() = sourceEvent.userId

    /**
     * [RawGroupUploadEvent] 原始事件中的 [文件信息][RawGroupUploadEvent.file]
     *
     * @see RawGroupUploadEvent.file
     */
    public val fileInfo: RawGroupUploadEvent.FileInfo
        get() = sourceEvent.file

    /**
     * 群
     *
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotGroup
}
