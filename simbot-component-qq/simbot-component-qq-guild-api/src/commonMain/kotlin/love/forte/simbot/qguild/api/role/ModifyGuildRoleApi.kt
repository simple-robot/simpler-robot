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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PatchQQGuildApi
import love.forte.simbot.qguild.api.SimpleApiDescription
import love.forte.simbot.qguild.model.ColorIntSerializer
import love.forte.simbot.qguild.model.Role
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [修改频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_role.html#%E4%BF%AE%E6%94%B9%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84)
 *
 * 用于修改频道 `guild_id` 下 `role_id` 指定的身份组。
 *
 * - 需要使用的 `token` 对应的用户具备修改身份组权限。如果是机器人，要求被添加为管理员。
 * - 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数。
 *
 * @author ForteScarlet
 */
public class ModifyGuildRoleApi private constructor(
    guildId: String, roleId: String,
    private val _body: Body,
) : PatchQQGuildApi<GuildRoleModified>() {
    public companion object Factory : SimpleApiDescription(
        HttpMethod.Patch, "/guilds/{guild_id}/roles/{role_id}"
    ) {

        /**
         * 构造 [ModifyGuildRoleApi]
         *
         * 参数可选，但是至少需要传其中之一。
         *
         * @param name 名称(非必填)
         * @param color ARGB 的 HEX 十六进制颜色值转换后的十进制数值(非必填)
         * @param hoist 在成员列表中单独展示: 0-否, 1-是(非必填)
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String, roleId: String, name: String? = null, color: Int? = null, hoist: Int? = null
        ): ModifyGuildRoleApi = ModifyGuildRoleApi(guildId, roleId, Body(name, color, hoist))

    }

    override val path: Array<String> = arrayOf("guilds", guildId, "roles", roleId)

    override val resultDeserializationStrategy: DeserializationStrategy<GuildRoleModified>
        get() = GuildRoleModified.serializer()

    override fun createBody(): Any = _body

    @Serializable
    private data class Body(
        val name: String?,
        @Serializable(ColorIntSerializer::class) val color: Int?,
        val hoist: Int?
    ) {
        init {
            require(name != null || color != null || hoist != null) {
                "At least one of the parameters should not be null"
            }
        }
    }
}

/**
 * [ModifyGuildRoleApi] 的响应体类型。
 */
@Serializable
public data class GuildRoleModified(
    /**
     * 频道ID
     */
    @SerialName("guild_id") public val guildId: String,

    /**
     * 身份组ID
     */
    @SerialName("role_id") public val roleId: String,

    /**
     * 修改后的频道身份组对象
     */
    public val role: Role,
)
