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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.group.GroupMembersRemoveResult
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmOverloads

/**
 * [群成员批量移除](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_batch_remove_members.post.html)。
 *
 * 批量移除群成员，单次最多 20 个，可选择同时加入黑名单。
 *
 * @since 5.0
 */
public class BatchRemoveGroupMembersApi private constructor(
    groupOpenid: String,
    override val body: Any,
) : PostQQGuildApi<GroupMembersRemoveResult>() {
    public companion object Factory : SimplePostApiDescription("/v2/groups/{group_openid}/batch_remove_members") {
        /**
         * 构建一次批量请求。
         *
         * 注意：不会校验 [memberOpenids] 的数量**上限**，这交给服务端处理。
         *
         * @throws IllegalArgumentException 群或成员 OpenID 为空或 [memberOpenids] 为空。
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            groupOpenid: String,
            memberOpenids: Iterable<String>,
            addToMemberBlacklist: Boolean = false,
        ): BatchRemoveGroupMembersApi {
            require(groupOpenid.isNotBlank()) { "groupOpenid must not be blank" }
            val members = memberOpenids.toList()
            require(members.isNotEmpty()) { "memberOpenids must not be empty" }
            require(members.all { it.isNotBlank() }) { "memberOpenids must not contain blank IDs" }
            return BatchRemoveGroupMembersApi(groupOpenid, Body(members, addToMemberBlacklist))
        }
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "batch_remove_members")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupMembersRemoveResult>
        get() = GroupMembersRemoveResult.serializer()

    override fun createBody(): Any? = null

    @Serializable
    private class Body(
        @SerialName("member_openids") val memberOpenids: List<String>,
        @SerialName("add_to_member_blacklist") val addToMemberBlacklist: Boolean,
    )
}
