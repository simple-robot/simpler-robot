/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.actor

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMemberRole.Companion.valueOfLenient
import love.forte.simbot.definition.Role
import kotlin.jvm.JvmStatic


/**
 * OneBot的群成员角色枚举。
 *
 * @author ForteScarlet
 */
public enum class OneBotMemberRole : Role {
    /**
     * 群主
     */
    OWNER,

    /**
     * 管理员
     */
    ADMIN,

    /**
     * 普通成员
     */
    MEMBER;

    /**
     * id，即 [name]。
     */
    override val id: ID
        get() = name.ID

    /**
     * 是否拥有管理权限。
     * [OWNER] 和 [ADMIN] 被视为拥有管理权限。
     */
    override val isAdmin: Boolean
        get() = ordinal < MEMBER.ordinal

    public companion object {
        /**
         * 使用 [name] 获取到对应的权限，
         * 相比较于 [valueOf],
         * [valueOfLenient] 更宽松：
         * 不会抛出异常，且 [name] 不要求区分大小写。
         */
        @JvmStatic
        public fun valueOfLenient(name: String): OneBotMemberRole? =
            when {
                OWNER.name.equals(name, ignoreCase = true) -> OWNER
                ADMIN.name.equals(name, ignoreCase = true) -> ADMIN
                MEMBER.name.equals(name, ignoreCase = true) -> MEMBER
                else -> null
            }
    }
}
