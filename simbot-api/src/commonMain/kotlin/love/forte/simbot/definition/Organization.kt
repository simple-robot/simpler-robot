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

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP


/**
 * 一个组织。
 *
 * 一个组织是一个拥有多个 [成员][Member] 的行为主体。
 *
 * @see Guild
 * @see ChatGroup
 *
 * @author ForteScarlet
 */
public interface Organization : Actor {
    /**
     * 此组织的名称。
     */
    public val name: String

    /**
     * 此组织的拥有者的ID。
     *
     * 如果不支持获取则可能得到 `null`。
     */
    public val ownerId: ID?

    /**
     * 根据ID寻找或查询指定的成员信息。
     * 如果找不到则会得到 `null`。
     *
     * @throws Exception 可能产生任何异常
     */
    @ST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    public suspend fun member(id: ID): Member?

    /**
     * 获取此组织内的所有成员集合。也会包括 Bot 自身在组织内的表现，除非平台特性无法混淆 Bot 与 [Member]。
     */
    public val members: Collectable<Member>

    /**
     * bot 在当前组织内作为 [成员][Member] 的表现。
     *
     * @throws Exception 可能产生任何异常
     */
    @STP
    public suspend fun botAsMember(): Member

    /**
     * 此组织中的所有可用角色集。
     * 有可能得到一个空的集合 —— 这说明当前组织没有角色这一概念。
     */
    public val roles: Collectable<Role>
}

/**
 * 一个组织内的成员。
 *
 * @see Organization
 */
public interface Member : User, SendSupport {
    /**
     * 此成员的名称。通常是代表它作为一个用户的名称，而不是在某个组织内的“昵称”。
     */
    override val name: String

    /**
     * 此成员在组织内的昵称。如果未设置或无法获取则会得到 `null`。
     */
    public val nick: String?
}
