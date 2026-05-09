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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [获取邀请列表](https://developer.kookapp.cn/doc/http/invite#%E8%8E%B7%E5%8F%96%E9%82%80%E8%AF%B7%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetInviteListApi private constructor(
    private val guildId: String? = null,
    private val channelId: String? = null,
    private val page: Int? = null,
    private val pageSize: Int? = null,
) : KookGetApi<InviteInfo>() {
    public companion object Factory {
        private val PATH = ApiPath.create("invite", "list")
        private val EMPTY = GetInviteListApi()

        /**
         * 构建 [GetInviteListApi] 实例。
         *
         * @param guildId 服务器 id
         * @param channelId 频道 id
         * @param page 目标页数
         * @param pageSize 每页数据数量
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String? = null,
            channelId: String? = null,
            page: Int? = null,
            pageSize: Int? = null,
        ): GetInviteListApi =
            if (guildId == null && channelId == null && page == null && pageSize == null) {
                EMPTY
            } else {
                GetInviteListApi(guildId, channelId, page, pageSize)
            }

    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<InviteInfo>
        get() = InviteInfo.serializer()

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            appendIfNotNull("guild_id", guildId)
            appendIfNotNull("channel_id", channelId)
            appendIfNotNull("page", page) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }

}
