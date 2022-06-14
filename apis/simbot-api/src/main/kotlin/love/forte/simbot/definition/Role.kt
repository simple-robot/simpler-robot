/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
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