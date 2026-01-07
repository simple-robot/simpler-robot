/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * 权限值
 *
 * 权限是一个 `unsigned int` 值，由比特位代表是否拥有对应的权限。
 * 权限值与对应比特位进行按位与操作，判断是否拥有该权限。
 *
 * ### 判断是否有某权限
 * 其中: `permissions` 代表权限值，`bitValue` 代表某权限比特位，`1 << bitValue` 代表某权限值。
 *
 * ```
 * permissions & (1 << bitValue)  == (1 << bitValue);
 * ```
 *
 * _注意： 正常角色由上向下排序，这个先后顺序是角色的优先级(position字段)。
 * 如果你有管理员权限，你只能管理优先级比自己低的用户，不能管理优先级等于或比自己高的用户。这个地方的逻辑举例来说是这样的：对于一个公司的hr来说，他是有招员工的权利也有开除员工的权利（类比于管理权限），但是他不能开掉老板，也不是招自己的boss。因此，在使用授予权限，更新等接口时，要注意一下，可能机器人虽然有管理权限，但是也不是什么角色都可以授予，也不是什么人都可以操作。_
 *
 * 更多参考[文档](https://developer.kookapp.cn/doc/http/guild-role)
 *
 * @property perm 权限具体的值。
 * @author ForteScarlet
 */
@JvmInline
@Serializable
public value class Permissions(public val perm: UInt) {

    public constructor(permissionType: PermissionType) : this(permissionType.value)
    public constructor(vararg permissionTypes: PermissionType) : this(permissionTypes.combine())

    /**
     * 判断提供的权限类型全部存在于当前值中。
     */
    public operator fun contains(permissionsValue: UInt): Boolean = contains(permissionsValue, true)


    /**
     * 判断提供的权限类型全部存在于当前值中。
     * @param full 如果为 `true` 则判断是否为完全包括，否则仅判断部分包括.
     */
    public fun contains(permissionsValue: UInt, full: Boolean): Boolean {
        return if (full) {
            permissionsValue == perm and permissionsValue
        } else {
            perm and permissionsValue > 0u
        }
    }

    /**
     * 判断提供的权限类型全部存在于当前值中。
     */
    public operator fun contains(permissionType: PermissionType): Boolean = contains(permissionType.value, true)


    /**
     * 判断提供的权限类型全部存在于当前值中。
     * @param full 如果为 `true` 则判断是否为完全包括，否则仅判断部分包括.
     */
    public fun contains(permissionType: PermissionType, full: Boolean): Boolean = contains(permissionType.value, full)

    /**
     * 判断提供的权限类型全部存在于当前值中。
     */
    public operator fun contains(permissions: Permissions): Boolean = contains(permissions.perm, true)


    /**
     * 判断提供的权限类型全部存在于当前值中。
     * @param full 如果为 `true` 则判断是否为完全包括，否则仅判断部分包括.
     */
    public fun contains(permissions: Permissions, full: Boolean): Boolean = contains(permissions.perm, full)

    /**
     * 合并两个权限。
     * ```
     * newPerm = perm1 | perm2
     * ```
     */
    public operator fun plus(other: Permissions): Permissions = Permissions(perm or other.perm)

    /**
     * 判断提供的权限类型任意一个存在于当前值中。
     *
     * @suppress deprecated
     */
    @Deprecated("Use contains", ReplaceWith("contains(permissionType, false)"))
    public fun anyContains(permissionType: PermissionType): Boolean {
        return contains(permissionType, false)
    }
}
