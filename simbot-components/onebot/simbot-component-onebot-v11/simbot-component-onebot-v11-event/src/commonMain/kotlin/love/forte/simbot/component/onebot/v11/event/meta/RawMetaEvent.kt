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

package love.forte.simbot.component.onebot.v11.event.meta

import love.forte.simbot.component.onebot.v11.event.ExpectEventSubTypeProperty
import love.forte.simbot.component.onebot.v11.event.RawEvent


/**
 *
 * [元事件](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md)
 *
 * > 消息、通知、请求三大类事件是与聊天软件直接相关的、机器人真实接收到的事件，
 * 除了这些，OneBot 自己还会产生一类事件，这里称之为「元事件」，
 * 例如生命周期事件、心跳事件等，这类事件与 OneBot 本身的运行状态有关，
 * 而与聊天软件无关。元事件的上报方式和普通事件完全一样。
 *
 * @author ForteScarlet
 */
@ExpectEventSubTypeProperty(value = "metaEventType", postType = RawMetaEvent.POST_TYPE, name = "meta_event_type")
public interface RawMetaEvent : RawEvent {
    /**
     * 元事件类型
     */
    public val metaEventType: String

    public companion object {
        public const val POST_TYPE: String = "meta_event"
    }
}

