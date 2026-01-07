/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.role

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.ApiResultException
import kotlin.jvm.JvmSynthetic

/**
 * 代表一个频道中某个成员所拥有的某个角色。
 *
 * ## 查询
 *
 * [KookMemberRole] 是通过 [KookMember.roles] 进行列表查询得到的，
 * 或者通过下述的**赋予**来 _"构建"_ 实例。
 *
 * ## 移除
 *
 * [KookMemberRole] 是一个附着于某个成员上的角色，
 * 当bot权限足够时可以将其从当前成员中 [移除][delete]。
 *
 * ## 赋予
 *
 * 可以将一个 [KookGuildRole] 赋予([KookGuildRole.grantTo]) 给一个指定的成员。
 * 当赋予成功后将会得到一个 [KookMemberRole] 实例。这也是一种 _"构建"_ [KookMemberRole] 实例的方式。
 *
 * <hr />
 *
 * > 统一性说明参考 [KookRole]
 *
 * @see KookRole
 *
 * @author ForteScarlet
 */
@ExperimentalSimbotAPI
public interface KookMemberRole : KookRole {

    /**
     * 此角色所属用户
     */
    public val member: KookMember

    /**
     * 得到当前角色所代表的服务器角色信息。
     */
    public val guildRole: KookGuildRole

    /**
     * 将当前角色从当前用户上移除。
     *
     * 移除后不应再使用其他API，可能会产生任何预期外的问题。
     *
     * @throws ApiResultException 可能在API请求过程中产生的任何异常，包括权限验证等
     * @throws ApiResponseException 可能在API请求过程中产生的任何异常，包括权限验证等
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)
}
