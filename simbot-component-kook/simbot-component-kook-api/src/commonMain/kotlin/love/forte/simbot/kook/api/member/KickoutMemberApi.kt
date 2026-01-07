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

package love.forte.simbot.kook.api.member

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic


/**
 * [踢出服务器](https://developer.kookapp.cn/doc/http/guild#%E8%B8%A2%E5%87%BA%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * @author ForteScarlet
 */
public class KickoutMemberApi private constructor(guildId: String, targetId: String) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild", "kickout")

        /**
         * 构造 [KickoutMemberApi].
         *
         * @param guildId 服务器 ID
         * @param targetId 目标用户 ID
         *
         */
        @JvmStatic
        public fun create(guildId: String, targetId: String): KickoutMemberApi = KickoutMemberApi(guildId, targetId)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any = Body(guildId, targetId)

    @Serializable
    private data class Body(@SerialName("guild_id") val guildId: String, @SerialName("target_id") val targetId: String)
}
