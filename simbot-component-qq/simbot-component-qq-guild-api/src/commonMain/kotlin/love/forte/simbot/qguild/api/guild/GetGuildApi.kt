/*
 * Copyright (c) 2022-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.guild

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleGuild
import kotlin.jvm.JvmStatic

/**
 * [获取频道详情](https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild.html)
 *
 * 用于获取 `guildId` 指定的频道的详情。
 *
 */
public class GetGuildApi private constructor(guildId: String) : GetQQGuildApi<SimpleGuild>() {
    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}"
    ) {

        /**
         * 构造 [GetGuildApi].
         *
         */
        @JvmStatic
        public fun create(guildId: String): GetGuildApi = GetGuildApi(guildId)
    }

    override val path: Array<String> = arrayOf("guilds", guildId)

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleGuild>
        get() = SimpleGuild.serializer()
}
