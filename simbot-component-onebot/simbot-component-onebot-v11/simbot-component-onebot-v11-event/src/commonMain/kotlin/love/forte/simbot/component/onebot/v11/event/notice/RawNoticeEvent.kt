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

import love.forte.simbot.component.onebot.v11.event.ExpectEventSubTypeProperty
import love.forte.simbot.component.onebot.v11.event.RawEvent


/**
 * [通知事件](https://github.com/botuniverse/onebot-11/blob/master/event/notice.md)
 *
 * @author ForteScarlet
 */
@ExpectEventSubTypeProperty(value = "noticeType", postType = RawNoticeEvent.POST_TYPE, name = "notice_type")
public interface RawNoticeEvent : RawEvent {
    /**
     * 通知类型
     */
    public val noticeType: String

    public companion object {
        public const val POST_TYPE: String = "notice"
    }
}
