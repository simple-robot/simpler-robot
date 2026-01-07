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

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.suspendrunner.ST

/**
 * 服务器频道角色创建器。提供 Kotlin DSL风格的API和Java的惯用API。
 *
 * 通过 [KookGuild.roleCreator] 构建。
 *
 * 对于创建角色来说，[API](https://developer.kookapp.cn/doc/http/guild-role#%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2)
 * 仅提供了 `name` 属性。如果希望指定其他属性，考虑在创建后使用 [KookGuildRole.updater] 进行属性更新。
 *
 */
@ExperimentalSimbotAPI
public interface KookGuildRoleCreator {

    //region DSL API
    /**
     * 角色名称。如果不写，则为"新角色"
     */
    public var name: String?
    //endregion


    /**
     * 角色名称。如果不写，则为"新角色"
     * @see name
     */
    public fun name(value: String): KookGuildRoleCreator = also { name = value }

    /**
     * 创建一个新角色。
     *
     * @throws ApiResultException 可能在API请求过程中产生的任何异常，包括权限验证等
     * @throws ApiResponseException 可能在API请求过程中产生的任何异常，包括权限验证等
     *
     * @return 创建的角色
     */
    @ST
    public suspend fun create(): KookGuildRole
}

