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

package love.forte.simbot.kook.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.util.BooleanToIntSerializer
import kotlin.jvm.JvmSynthetic


/**
 * [角色 Role](https://developer.kookapp.cn/doc/objects#%E8%A7%92%E8%89%B2%20Role)
 *
 * @author ForteScarlet
 */
public interface Role : Comparable<Role> {

    /** 角色id */
    public val roleId: Long // role_id

    /** 角色名称 */
    public val name: String

    /** 颜色色值 */
    public val color: Int

    /** 顺序位置 */
    public val position: Int

    /** 是否为角色设定(与普通成员分开显示) */
    public val isHoist: Boolean
    // ser name: hoist

    /** 是否允许任何人@提及此角色 */
    public val isMentionable: Boolean
    // ser name: mentionable

    /** 权限码 */
    @get:JvmSynthetic
    public val permissions: Permissions

    /**
     * 权限码的数字值
     */
    public val permissionsValue: Int get() = permissions.perm.toInt()

    /**
     * 排序
     */
    public override fun compareTo(other: Role): Int = position.compareTo(other.position)

    public companion object
}

/**
 * [Role] 的基础实现，大部分数值字段默认值为 `-1`。
 */
@Serializable
public data class SimpleRole(
    @SerialName("role_id")
    override val roleId: Long,
    override val name: String,
    override val color: Int = -1,
    override val position: Int = -1,
    @SerialName("hoist")
    @Serializable(with = BooleanToIntSerializer::class)
    override val isHoist: Boolean = false,
    @SerialName("mentionable")
    @Serializable(with = BooleanToIntSerializer::class)
    override val isMentionable: Boolean = false,
    override val permissions: Permissions = Permissions(0u)
) : Role

/**
 * 提及角色权限组时候使用的 `mention_role_part` 字段值。
 */
@Serializable
public data class MentionRolePart(@SerialName("role_id") val id: String, val name: String)
