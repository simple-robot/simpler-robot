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

package love.forte.simbot.kook.api.guild

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.objects.SimpleGuildWithRolesAndChannels
import kotlin.jvm.JvmStatic


/**
 *
 * [获取服务器详情](https://developer.kookapp.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%AF%A6%E6%83%85)
 *
 * @author ForteScarlet
 */
public class GetGuildViewApi private constructor(private val guildId: String) : KookGetApi<SimpleGuildWithRolesAndChannels>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild", "view")

        /**
         * 构造 [GetGuildViewApi].
         *
         * @param guildId 频道服务器ID
         *
         */
        @JvmStatic
        public fun create(guildId: String): GetGuildViewApi = GetGuildViewApi(guildId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleGuildWithRolesAndChannels> get() = SimpleGuildWithRolesAndChannels.serializer()
    override val apiPath: ApiPath get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters.append("guild_id", guildId)
    }
}
