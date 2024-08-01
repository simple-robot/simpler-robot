/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.event

import love.forte.simbot.common.streamable.Streamable
import love.forte.simbot.common.streamable.Streamable.Companion.asStreamable


/**
 * 事件监听器的“容器”接口，提供用于获取其中的所有 [EventListener] 的API。
 *
 * @author ForteScarlet
 */
public interface EventListenerContainer {

    /**
     * 得到当前容器内所有的 [EventListener] 的序列。
     * 如无特殊说明则会按照优先级顺序获取。
     */
    public val listeners: Sequence<EventListener>

    /**
     * 获取到 [listeners] 并转化为 [Streamable]
     *
     * @see listeners
     */
    public fun listenersToStreamable(): Streamable<EventListener> =
        listeners.asStreamable()
}
