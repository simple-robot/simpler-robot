/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.ability

/**
 * 提供一些可以获取到当前状态的感知类型。
 *
 * 通常情况下会配合 [kotlinx.coroutines.Job] 实现。
 *
 * @author ForteScarlet
 */
public interface LifecycleAware {
    /**
     * 当前是否处于活跃、运行或尚未结束的状态。
     */
    public val isActive: Boolean

    /**
     * 当前是否已经完成、已经结束。
     */
    public val isCompleted: Boolean
}
