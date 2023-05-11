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

package love.forte.simbot.ability


/**
 *
 * 可以感知到某个目标的完成。
 *
 * 是一个功能性接口。
 *
 * @author ForteScarlet
 */
public interface CompletionPerceivable<T> {
    
    /**
     * 当目标完成时执行注册的回调函数。
     */
    public fun onCompletion(handle: suspend (application: T) -> Unit)
    
}
