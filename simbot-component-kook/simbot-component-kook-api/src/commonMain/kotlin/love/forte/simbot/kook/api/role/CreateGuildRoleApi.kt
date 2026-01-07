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

package love.forte.simbot.kook.api.role

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import love.forte.simbot.kook.objects.Role
import love.forte.simbot.kook.objects.SimpleRole
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [创建服务器角色](https://developer.kookapp.cn/doc/http/guild-role#%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2)
 *
 * @author ForteScarlet
 */
public class CreateGuildRoleApi private constructor(
    private val guildId: String,
    private val name: String? = null,
) : KookPostApi<Role>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-role", "create")

        /**
         * 构建 [CreateGuildRoleApi]
         *
         * @param guildId 频道服务器ID
         * @param name 角色名称。如果不写，则为"新角色"
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, name: String? = null): CreateGuildRoleApi =
            CreateGuildRoleApi(guildId, name)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleRole>
        get() = SimpleRole.serializer()

    override fun createBody(): Any = Body(guildId, name)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        val name: String? = null,
    )
}
//
///**
// * API [CreateGuildRoleApi] 的响应值.
// */
//@Serializable
//public data class CreatedGuildRole @ApiResultType constructor(
//    /**
//     * 角色的id
//     */
//    @SerialName("role_id")
//    public val roleId: Long,
//    /**
//     * 角色的名称
//     */
//    public val name: String,
//    /**
//     * 角色的色值0x000000 - 0xFFFFFF
//     */
//    public val color: Int,
//    /**
//     * 顺序，值越小载靠前
//     */
//    public val position: Int,
//    /**
//     * 只能为0或者1，是否把该角色的用户在用户列表排到前面
//     */
//    @SerialName("hoist")
//    public val hoist: Int,
//    /**
//     * 只能为0或者1，该角色是否可以被提及
//     */
//    public val mentionable: Int,
//
//    /**
//     * 权限信息。Java中可以通过 [permissionsValue] 得到int类型的字面值。
//     */
//    @SerialName("permissions")
//    @get:JvmSynthetic
//    public val permissions: Permissions,
//) : Role {
//
//    /**
//     * 权限信息的数值
//     */
//    @Api4J
//    public val permissionsValue: Int get() = permissions.perm.toInt()
//}
//
