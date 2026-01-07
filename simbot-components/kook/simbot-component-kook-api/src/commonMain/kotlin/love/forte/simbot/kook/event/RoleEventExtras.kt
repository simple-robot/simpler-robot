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

package love.forte.simbot.kook.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.objects.SimpleRole


/**
 * [服务器角色增加](https://developer.kookapp.cn/doc/event/guild-role#%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2%E5%A2%9E%E5%8A%A0)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(AddedRoleEventExtra.TYPE)
public data class AddedRoleEventExtra(override val body: SimpleRole) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "added_role"
    }
}

/**
 * [服务器角色删除](https://developer.kookapp.cn/doc/event/guild-role#%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2%E5%88%A0%E9%99%A4)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeletedRoleEventExtra.TYPE)
public data class DeletedRoleEventExtra(override val body: SimpleRole) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_role"
    }
}

/**
 * [服务器角色更新](https://developer.kookapp.cn/doc/event/guild-role#%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2%E6%9B%B4%E6%96%B0)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdatedRoleEventExtra.TYPE)
public data class UpdatedRoleEventExtra(override val body: SimpleRole) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_role"
    }
}
