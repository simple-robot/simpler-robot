/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.event


/**
 * 被注册后监听函数的句柄.
 *
 *
 * @author ForteScarlet
 */
public interface EventListenerHandle {
    
    /**
     * 将当前监听函数移除于目标容器中.
     *
     * @return 是否移除成功. 如果目标容器中已经不存在当前句柄所描述的监听函数则会得到 `false`.
     */
    public fun dispose(): Boolean
    
    /**
     * 判断当前句柄所描述的监听函数是否存在于目标容器中.
     */
    public val isExists: Boolean
    
    /**
     * 此句柄所属的 [EventListenerContainer].
     */
    public val container: EventListenerContainer
    
}


