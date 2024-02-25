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


/**
 * 一个 [组织][Organization] 中的角色或权限的描述。
 *
 * [Role] 可能代表一个组织内的通用角色，
 * 也可能是一个具体到某个用户的详细角色。
 *
 * @author ForteScarlet
 */
public interface Role {
    /**
     * 这个角色的ID
     */
    public val id: ID

    /**
     * 这个角色的名称。
     */
    public val name: String

    /**
     * 此角色是否拥有 _管理权限_ 。
     *
     * _管理权限_ 以其拥有者是否能操作或影响组织或其他用户为标准，
     * 例如能够修改他人昵称、修改频道名称等。
     *
     */
    public val isAdmin: Boolean
}
