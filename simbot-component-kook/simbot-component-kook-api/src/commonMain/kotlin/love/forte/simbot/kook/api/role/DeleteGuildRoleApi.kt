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
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic


/**
 * [删除服务器角色](https://developer.kookapp.cn/doc/http/guild-role#%E5%88%A0%E9%99%A4%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2)
 *
 * @author ForteScarlet
 */
public class DeleteGuildRoleApi private constructor(
    private val guildId: String,
    private val roleId: Long,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-role", "delete")

        /**
         * 构建 [DeleteGuildRoleApi].
         *
         * @param guildId 服务器ID
         * @param roleId 角色ID
         */
        @JvmStatic
        public fun create(guildId: String, roleId: Long): DeleteGuildRoleApi = DeleteGuildRoleApi(guildId, roleId)
    }


    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any =
        Body(guildId, roleId)

    @Serializable
    private data class Body(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("role_id")
        val roleId: Long,
    )

}
