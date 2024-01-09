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

package love.forte.simbot.definition

import love.forte.simbot.common.id.ID
import love.forte.simbot.suspendrunner.STP


/**
 * 一个分组。分组主要可能存在于频道服务器中，
 * 并用于对各子频道进行分组。
 *
 * 分组只提供用于获取唯一标识 [id] 的属性。因为有些情况下，
 * 一个“分组”的其他属性可能都需要查询，或至少不会伴随着事件被提供。
 *
 * @author ForteScarlet
 */
public interface Category {
    /**
     * 分组的ID。
     */
    public val id: ID

    /**
     * 获取分组的名称。如果分组不存在名称或无法获取则会得到 `null`。
     */
    @STP
    public suspend fun name(): String?
}
