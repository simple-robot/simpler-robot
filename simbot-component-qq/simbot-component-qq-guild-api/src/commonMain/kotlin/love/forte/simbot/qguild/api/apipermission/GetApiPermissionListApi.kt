/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.apipermission

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.ApiDescription
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.ApiPermission
import love.forte.simbot.qguild.model.isAuthorized
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * [获取频道可用权限列表](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/get_guild_api_permission.html)
 *
 * 用于获取机器人在频道 `guild_id` 内可以使用的权限列表。
 *
 * @author ForteScarlet
 */
public class GetApiPermissionListApi private constructor(
    guildId: String
) : GetQQGuildApi<ApiPermissions>() {

    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/api_permission"
    ) {

        /**
         * 构造 [GetApiPermissionListApi].
         *
         * @param guildId 频道服务器ID
         */
        @JvmStatic
        public fun create(guildId: String): GetApiPermissionListApi = GetApiPermissionListApi(guildId)
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "api_permission")

    override val resultDeserializationStrategy: DeserializationStrategy<ApiPermissions>
        get() = ApiPermissions.serializer()
}

/**
 * [GetApiPermissionListApi] 的API响应体。
 */
@ApiModel
@Serializable
public class ApiPermissions(public val apis: List<ApiPermission>) : Iterable<ApiPermission> by apis {
    private val apisMap by lazy(LazyThreadSafetyMode.PUBLICATION) { apis.groupBy { it.path } }

    /**
     * 在当前权限列表中寻找是否与 [description] 描述相同的权限.
     */
    public operator fun contains(description: ApiDescription): Boolean =
        apisMap[description.path]?.any { it.method == description.method } ?: false

    /**
     * 在当前权限列表中寻找与 [description] 描述相同的权限.
     */
    public fun find(description: ApiDescription): ApiPermission? =
        apisMap[description.path]?.find { it.method == description.method }

    /**
     * 尝试获取某个 [ApiDescription] 下的所有相关API权限.
     */
    public operator fun get(description: ApiDescription): List<ApiPermission> =
        apisMap[description.path] ?: emptyList()

    override fun toString(): String {
        return "ApiPermissions($apis)"
    }

    public companion object {
        /**
         * 权限列表为空的 [ApiPermissions]
         */
        @JvmField
        public val EMPTY: ApiPermissions = ApiPermissions(emptyList())
    }
}

/**
 * 判断在 [ApiDescription] 中是否存在 [ApiPermission.isAuthorized] == `true` 的权限.
 *
 * @see ApiPermissions
 * @see ApiPermission.isAuthorized
 */
public infix fun ApiPermissions.hasAuth(description: ApiDescription): Boolean =
    find(description)?.isAuthorized ?: false

