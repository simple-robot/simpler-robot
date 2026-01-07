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

package love.forte.simbot.kook.api.invite

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [删除邀请链接](https://developer.kookapp.cn/doc/http/invite#%E5%88%A0%E9%99%A4%E9%82%80%E8%AF%B7%E9%93%BE%E6%8E%A5)
 *
 * @author ForteScarlet
 */
public class DeleteInviteApi private constructor(
    private val urlCode: String,
    private val guildId: String? = null,
    private val channelId: String? = null,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("invite", "delete")

        /**
         * 构建一个 [DeleteInviteApi] 实例。
         *
         * @param urlCode 邀请码
         * @param guildId 服务器id
         * @param channelId 频道id
         */
        @JvmStatic
        @JvmOverloads
        public fun create(urlCode: String, guildId: String? = null, channelId: String? = null): DeleteInviteApi =
            DeleteInviteApi(urlCode, guildId, channelId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any = Body(urlCode, guildId, channelId)

    private data class Body(
        @SerialName("url_code") val urlCode: String,
        @SerialName("guild_id") val guildId: String?,
        @SerialName("channel_id") val channelId: String?,
    )


}
