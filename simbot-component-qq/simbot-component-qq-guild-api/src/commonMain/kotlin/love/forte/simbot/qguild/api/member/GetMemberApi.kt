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

package love.forte.simbot.qguild.api.member

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleMember
import kotlin.jvm.JvmStatic


/**
 * [获取某个成员信息](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_member.html)
 *
 * 用于获取 `guild_id` 指定的频道中 `user_id` 对应成员的详细信息。
 *
 * @author ForteScarlet
 */
public class GetMemberApi private constructor(
    guildId: String, userId: String
) : GetQQGuildApi<SimpleMember>() {
    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/members/{user_id}"
    ) {
        /**
         * 构造 [GetMemberApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String, userId: String): GetMemberApi = GetMemberApi(guildId, userId)
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "members", userId)

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleMember>
        get() = SimpleMember.serializer()
}
