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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmName

/**
 * [子频道权限对象(ChannelPermissions)](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.htm)
 *
 * @property channelId 子频道 ID。
 * @property userId 用户 ID，只会与 [roleId] 返回其中之一。
 * @property roleId 身份组 ID，只会与 [userId] 返回其中之一。
 * @property permissions 用户拥有的子频道权限。
 */
@ApiModel
@Serializable
public class ChannelPermissions @ApiModelConstructor constructor(
    @SerialName("channel_id") public val channelId: String,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("role_id") public val roleId: String? = null,
    @get:JvmName("getPermissions") public val permissions: Permissions
) {
    /**
     * 获取[userId]或[roleId]的值。
     * > 用户 id 或 身份组 id，只会返回其中之一。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val userIdOrRoleId: String get() = (userId ?: roleId)!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelPermissions) return false

        if (channelId != other.channelId) return false
        if (userId != other.userId) return false
        if (roleId != other.roleId) return false
        if (permissions != other.permissions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + roleId.hashCode()
        result = 31 * result + permissions.hashCode()
        return result
    }

    override fun toString(): String {
        return "ChannelPermissions(" +
            "channelId='$channelId', " +
            "userId=$userId, " +
            "roleId=$roleId, " +
            "permissions=$permissions)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component1(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("userId"))
    public operator fun component2(): String? = userId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roleId"))
    public operator fun component3(): String? = roleId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("permissions"))
    public operator fun component4(): Permissions = permissions

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        channelId: String = this.channelId,
        userId: String? = this.userId,
        roleId: String? = this.roleId,
        permissions: Permissions = this.permissions,
    ): ChannelPermissions = ChannelPermissions(channelId, userId, roleId, permissions)
    //endregion
}
