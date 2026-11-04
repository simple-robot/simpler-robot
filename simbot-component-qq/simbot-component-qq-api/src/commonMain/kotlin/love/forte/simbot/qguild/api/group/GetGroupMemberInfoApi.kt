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
import love.forte.simbot.qguild.model.group.GroupMemberInfo
import kotlin.jvm.JvmStatic

/**
 * [获取群成员信息](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_members_member_openid.get.html)。
 *
 * @since 5.0
 */
public class GetGroupMemberInfoApi private constructor(
    groupOpenid: String,
    memberOpenid: String,
) : GetQQGuildApi<GroupMemberInfo>() {
    public companion object Factory : SimpleGetApiDescription("/v2/groups/{group_openid}/members/{member_openid}") {
        /**
         * 构建指定群成员的资料查询请求。
         * @param groupOpenid 群OpenID
         * @param memberOpenid 成员OpenID
         */
        @JvmStatic
        public fun create(groupOpenid: String, memberOpenid: String): GetGroupMemberInfoApi =
            GetGroupMemberInfoApi(groupOpenid, memberOpenid)
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "members", memberOpenid)

    override val resultDeserializationStrategy: DeserializationStrategy<GroupMemberInfo>
        get() = GroupMemberInfo.serializer()
}
