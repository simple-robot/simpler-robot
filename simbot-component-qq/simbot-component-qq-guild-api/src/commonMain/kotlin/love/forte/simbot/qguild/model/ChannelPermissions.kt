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
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor
import kotlin.jvm.JvmName

/**
 * [子频道权限对象(ChannelPermissions)](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.htm)
 *
 */
@ApiModel
@Serializable
public data class ChannelPermissions @ApiModelConstructor constructor(
    /**
     * 子频道 id
     */
    @SerialName("channel_id") val channelId: String,
    /**
     * 用户 id 或 身份组 id，只会返回其中之一
     *
     * @see userIdOrRoleId
     */
    @SerialName("user_id") val userId: String? = null,
    /**
     * 用户 id 或 身份组 id，只会返回其中之一
     *
     * @see userIdOrRoleId
     */
    @SerialName("role_id") val roleId: String? = null,
    /**
     * 用户拥有的子频道权限
     */
    @get:JvmName("getPermissions") val permissions: Permissions
) {
    /**
     * 获取[userId]或[roleId]的值。
     * > 用户 id 或 身份组 id，只会返回其中之一。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val userIdOrRoleId: String get() = (userId ?: roleId)!!
}
