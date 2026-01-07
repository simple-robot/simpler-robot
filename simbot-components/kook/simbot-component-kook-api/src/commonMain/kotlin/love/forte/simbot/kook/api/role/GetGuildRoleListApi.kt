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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.objects.Role
import love.forte.simbot.kook.objects.SimpleRole
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [获取服务器角色列表](https://developer.kookapp.cn/doc/http/guild-role#%E8%8E%B7%E5%8F%96%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%92%E8%89%B2%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetGuildRoleListApi private constructor(
    private val guildId: String,
    private val page: Int? = null,
    private val pageSize: Int? = null,
) : KookGetApi<ListData<Role>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-role", "list")
        private val serializer = ListData.serializer(SimpleRole.serializer())

        /**
         * 构建 [GetGuildRoleListApi]
         *
         * @param guildId 频道服务器ID
         * @param page 目标页数
         * @param pageSize 每页数据数量
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, page: Int? = null, pageSize: Int? = null): GetGuildRoleListApi =
            GetGuildRoleListApi(guildId, page, pageSize)

    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<Role>>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("guild_id", guildId)
            page?.let { append("page", it.toString()) }
            pageSize?.let { append("page_size", it.toString()) }
        }
    }
}
