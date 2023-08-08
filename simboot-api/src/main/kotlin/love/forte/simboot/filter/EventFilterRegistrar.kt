/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.filter

import love.forte.simbot.event.EventFilter

/**
 *
 * 事件过滤器注册器.
 *
 * @author ForteScarlet
 */
@Deprecated("未完成")
public interface EventFilterRegistrar {

    /**
     * 注册一个监听函数过滤器至当前注册器中。
     */
    public fun register(filter: EventFilter)
}
