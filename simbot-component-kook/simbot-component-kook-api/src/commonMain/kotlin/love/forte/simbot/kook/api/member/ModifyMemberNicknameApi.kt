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
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [修改服务器中用户的昵称](https://developer.kookapp.cn/doc/http/guild#%E4%BF%AE%E6%94%B9%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%B8%AD%E7%94%A8%E6%88%B7%E7%9A%84%E6%98%B5%E7%A7%B0)
 *
 * @author ForteScarlet
 */
public class ModifyMemberNicknameApi private constructor(
    @Suppress("ConstructorParameterNaming")
    private val _body: Body
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild", "nickname")

        /**
         * 构造 [ModifyMemberNicknameApi].
         *
         * @param guildId 服务器的 ID
         * @param nickname 昵称，2 - 64 长度，不传则清空昵称
         * @param userId 要修改昵称的目标用户 ID，不传则修改当前登陆用户的昵称
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, nickname: String? = null, userId: String? = null): ModifyMemberNicknameApi =
            ModifyMemberNicknameApi(Body(guildId, nickname, userId))

    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any get() = _body

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        val nickname: String? = null,
        @SerialName("user_id") val userId: String? = null
    )
}
