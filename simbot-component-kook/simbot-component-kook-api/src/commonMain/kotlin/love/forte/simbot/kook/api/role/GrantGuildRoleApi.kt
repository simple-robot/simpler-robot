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
import kotlin.jvm.JvmStatic


/*
赋予用户角色
https://developer.kookapp.cn/doc/http/guild-role#%E8%B5%8B%E4%BA%88%E7%94%A8%E6%88%B7%E8%A7%92%E8%89%B2

接口说明
地址	请求方式	说明
/api/v3/guild-role/grant	POST

参数列表
参数名	位置	类型	必需	说明
guild_id	body	string	true	服务器 id
user_id	body	string	true	用户 id
role_id	body	unsigned int	true	服务器角色 id

返回参数说明
参数名	类型	说明
user_id	string	用户 id
guild_id	string	服务器 id
roles	Array	角色 id 的列表
 */

/**
 * [赋予用户角色](https://developer.kookapp.cn/doc/http/guild-role#%E8%B5%8B%E4%BA%88%E7%94%A8%E6%88%B7%E8%A7%92%E8%89%B2)
 * @author ForteScarlet
 */
public class GrantGuildRoleApi private constructor(
    private val guildId: String,
    private val userId: String,
    private val roleId: Long,
) : KookPostApi<UserRoleOperated>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-role", "grant")

        /**
         * 构建 [赋予用户角色][GrantGuildRoleApi] 请求。
         *
         * @param guildId 服务器id
         * @param userId 用户id
         * @param roleId 角色id
         */
        @JvmStatic
        public fun create(guildId: String, userId: String, roleId: Long): GrantGuildRoleApi =
            GrantGuildRoleApi(guildId, userId, roleId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<UserRoleOperated>
        get() = UserRoleOperated.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun createBody(): Any = Body(guildId, userId, roleId)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("user_id") val userId: String,
        @SerialName("role_id") val roleId: Long,
    )


}
