/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.role

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
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
 */
@ApiModel
@Serializable
public data class GuildRoleList(
    /**
     * 频道 ID
     */
    @SerialName("guild_id")
    public val guildId: String,
    /**
     *  一组频道身份组对象
     */
    public val roles: List<Role>,
    /**
     * 	默认分组上限
     */
    @SerialName("role_num_limit")
    public val roleNumLimit: String // Int?
)

