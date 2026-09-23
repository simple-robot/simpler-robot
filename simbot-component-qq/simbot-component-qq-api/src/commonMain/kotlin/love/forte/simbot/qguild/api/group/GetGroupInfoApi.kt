/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.group

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.group.GroupInfo
import kotlin.jvm.JvmStatic

/**
 * [获取群基本信息](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_info.get.html)。
 *
 * @since 5.0
 */
public class GetGroupInfoApi private constructor(groupOpenid: String) : GetQQGuildApi<GroupInfo>() {
    /**
     * GetGroupInfoApi 的构建入口。
     */
    public companion object Factory : SimpleGetApiDescription("/v2/groups/{group_openid}/info") {
        /**
         * 构建指定群的请求。
         *
         * @param groupOpenid 群OpenID
         */
        @JvmStatic
        public fun create(groupOpenid: String): GetGroupInfoApi = GetGroupInfoApi(groupOpenid)
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "info")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupInfo>
        get() = GroupInfo.serializer()
}
