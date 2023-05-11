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

package love.forte.simbot.definition

import love.forte.simbot.ID


/**
 * 一个组织中的成员"角色"（或称“权限组”、“职责”等等）。
 *
 * 角色承担了为成员分配权限的能力。
 * 以 [群聊][Group] 为例，一个普通的群聊可能存在三种最常见的角色：*普通群员*、*管理员*和*创建者*。
 *
 * [Role] 只关系最基本的属性, 即这个角色是否能够代表为一个 [管理员][isAdmin]。
 * 而针对不同组件可能存在的更细致的划分，则由组件的实现者具体提供。
 *
 * @see Group.roles
 * @see Channel.roles
 * @see Guild.roles
 *
 */
public interface Role : IDContainer {
    /**
     * 这个角色的ID
     */
    override val id: ID
    
    /**
     * 这个角色的名称。
     */
    public val name: String
    
    /**
     * 是否拥有 *管理权限* 。大多数场景下，[拥有者][Organization.ownerId] 也拥有管理权限。
     */
    public val isAdmin: Boolean
    
}
