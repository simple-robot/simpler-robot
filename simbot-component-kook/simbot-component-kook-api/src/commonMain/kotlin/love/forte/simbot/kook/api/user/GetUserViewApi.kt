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

package love.forte.simbot.kook.api.user

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.objects.User
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic

/**
 * [获取目标用户信息](https://developer.kookapp.cn/doc/http/user#%E8%8E%B7%E5%8F%96%E7%9B%AE%E6%A0%87%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF)
 *
 * @author ForteScarlet
 */
public class GetUserViewApi private constructor(
    private val userId: String,
    private val guildId: String
) : KookGetApi<User>() {
    public companion object Factory {
        private val PATH = ApiPath.create("user", "view")

        /**
         * 构造 [GetUserViewApi].
         *
         * @param userId 用户 id
         * @param guildId 服务器 id
         *
         */
        @JvmStatic
        public fun create(userId: String, guildId: String): GetUserViewApi =
            GetUserViewApi(userId, guildId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleUser>
        get() = SimpleUser.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("user_id", userId)
            append("guild_id", guildId)
        }
    }
}


//public interface UserView : User // TODO 实现 UserView ?
