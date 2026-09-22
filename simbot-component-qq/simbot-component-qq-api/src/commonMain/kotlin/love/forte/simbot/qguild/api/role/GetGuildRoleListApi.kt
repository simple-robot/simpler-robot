/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.role

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.model.Role
import kotlin.jvm.JvmStatic

/**
 *
 * [获取频道身份组列表](https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild_roles.html#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84%E5%88%97%E8%A1%A8)
 *
 * 用于获取 `guild_id` 指定的频道下的身份组列表。
 *
 * @author ForteScarlet
 */
public class GetGuildRoleListApi private constructor(guildId: String) : GetQQGuildApi<GuildRoleList>() {
    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/roles"
    ) {
        /**
         * 构造 [GetGuildRoleListApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String): GetGuildRoleListApi = GetGuildRoleListApi(guildId)
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "roles")
    override val resultDeserializationStrategy: DeserializationStrategy<GuildRoleList>
        get() = GuildRoleList.serializer()
}

/**
 * [GetGuildRoleListApi] 的响应体包装
 *
 * @property guildId 频道 ID
 * @property roles 一组频道身份组对象
 * @property roleNumLimit 默认分组上限
 */
@ApiModel
@Serializable
public class GuildRoleList @ApiModelConstructor public constructor(
    @SerialName("guild_id")
    public val guildId: String,
    public val roles: List<Role>,
    @SerialName("role_num_limit")
    public val roleNumLimit: String // Int?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuildRoleList) return false
        if (guildId != other.guildId) return false
        if (roles != other.roles) return false
        if (roleNumLimit != other.roleNumLimit) return false
        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + roles.hashCode()
        result = 31 * result + roleNumLimit.hashCode()
        return result
    }

    override fun toString(): String =
        "GuildRoleList(guildId=$guildId, roles=$roles, roleNumLimit=$roleNumLimit)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roles"))
    public operator fun component2(): List<Role> = roles

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("roleNumLimit"))
    public operator fun component3(): String = roleNumLimit

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        roles: List<Role> = this.roles,
        roleNumLimit: String = this.roleNumLimit,
    ): GuildRoleList = GuildRoleList(guildId, roles, roleNumLimit)
}
