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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi


/**
 * [删除服务器静音或闭麦](https://developer.kookapp.cn/doc/http/guild#%E5%88%A0%E9%99%A4%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%9D%99%E9%9F%B3%E6%88%96%E9%97%AD%E9%BA%A6)
 *
 * @author ForteScarlet
 */
public class DeleteGuildMuteApi private constructor(
    @Suppress("ConstructorParameterNaming")
    private val _body: Any
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-mute", "delete")

        /**
         * 构造 [DeleteGuildMuteApi].
         *
         * @param guildId 服务器 id
         * @param userId 目标用户 id
         * @param type 静音类型，1代表麦克风闭麦，2代表耳机静音
         */
        public fun create(guildId: String, userId: String, type: Int): DeleteGuildMuteApi =
            DeleteGuildMuteApi(Body(guildId, userId, type))

        /**
         * 构造 [DeleteGuildMuteApi].
         *
         * @param guildId 服务器 id
         * @param userId 目标用户 id
         * @param type 静音类型
         */
        public fun create(guildId: String, userId: String, type: MuteType): DeleteGuildMuteApi =
            DeleteGuildMuteApi(Body(guildId, userId, type.value))

    }

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override val body: Any get() = _body

    @Serializable
    private data class Body(val guildId: String, val userId: String, val type: Int)
}
