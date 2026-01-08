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
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.ColorIntSerializer
import love.forte.simbot.qguild.model.Role
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 *
 * [创建频道身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html)
 *
 * 用于在 `guild_id` 指定的频道下创建一个身份组。
 *
 * - 需要使用的 `token` 对应的用户具备创建身份组权限。如果是机器人，要求被添加为管理员。
 * - 参数为非必填，但至少需要传其中之一，默认为空或 0。
 *
 * @author ForteScarlet
 */
public class CreateGuildRoleApi private constructor(
    guildId: String,
    private val _body: NewBody,
) : PostQQGuildApi<GuildRoleCreated>() {
    public companion object Factory : SimplePostApiDescription(
        "/guilds/{guild_id}/roles"
    ) {

        /**
         * 构造 [CreateGuildRoleApi].
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
            guildId: String,
            name: String? = null,
            color: Int? = null,
            hoist: Int? = null
        ): CreateGuildRoleApi =
            CreateGuildRoleApi(guildId, NewBody(name, color, hoist))
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "roles")

    override val resultDeserializationStrategy: DeserializationStrategy<GuildRoleCreated>
        get() = GuildRoleCreated.serializer()

    override fun createBody(): Any? = _body

    @Serializable
    private data class NewBody(
        val name: String?,
        @Serializable(ColorIntSerializer::class) val color: Int?,
        val hoist: Int?,
    ) {
        init {
            require(name != null || color != null || hoist != null) {
                "At least one of the parameters should not be null"
            }
        }
    }
}

/**
 * [CreateGuildRoleApi] 创建成功后的返回值
 */
@ApiModel
@Serializable
public data class GuildRoleCreated(
    /**
     * 身份组 ID
     */
    @SerialName("role_id") val roleId: String,
    /**
     * 所创建的频道身份组对象
     */
    val role: Role? = null
    // TODO 与文档描述不符，实际上为null
    //  see https://qun.qq.com/qqweb/qunpro/share?_wv=3&_wwv=128&appChannel=share&inviteCode=1ZCS7xZH0UF&contentID=1jDEHr&businessType=2&from=181174&shareSource=5&biz=ka
)



