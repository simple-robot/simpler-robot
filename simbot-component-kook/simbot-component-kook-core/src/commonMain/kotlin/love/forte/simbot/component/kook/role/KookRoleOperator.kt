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


/**
 *
 * [KookRole] 操作器。
 *
 * 由 [KookGuild] 实现，提供与 [KookRole] 相关的（更多）操作API。
 *
 * @see KookGuild
 *
 * @author ForteScarlet
 */
public interface KookRoleOperator {

    /**
     * 构建一个针对当前频道服务器的角色创建器，用于构建一个新的角色 `Role`。
     *
     * @see KookGuildRoleCreator
     */
    @ExperimentalSimbotAPI
    public fun roleCreator(): KookGuildRoleCreator
}


/**
 * 使用 DSL 风格API创建一个 [KookGuildRole].
 *
 * ```kotlin
 * val newRole = operator.createRole {
 *    name = "武旦"
 * }
 * ```
 *
 * @see KookRoleOperator.roleCreator
 */
@ExperimentalSimbotAPI
public suspend inline fun KookRoleOperator.createRole(block: KookGuildRoleCreator.() -> Unit): KookGuildRole {
    return roleCreator().also(block).create()
}
