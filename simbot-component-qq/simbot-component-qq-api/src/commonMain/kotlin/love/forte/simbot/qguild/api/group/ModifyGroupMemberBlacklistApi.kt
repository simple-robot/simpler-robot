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
import love.forte.simbot.qguild.model.group.GroupBlacklistModifyResult
import kotlin.jvm.JvmStatic

/**
 * [群黑名单操作](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_member_blacklist.post.html)。
 *
 * 群黑名单操作，只有在目标用户不在群中时才能加入群黑名单。
 *
 * @since 5.0
 */
public class ModifyGroupMemberBlacklistApi private constructor(
    groupOpenid: String,
    override val body: Any,
) : PostQQGuildApi<GroupBlacklistModifyResult>() {
    public companion object Factory : SimplePostApiDescription("/v2/groups/{group_openid}/member_blacklist") {
        /**
         * 构建一次批量请求。
         *
         * @param groupOpenid 群OpenID
         * @param op 操作类型
         * @param memberOpenids 目标成员 openid 列表，单次最多 20 个（不做本地上限校验）
         *
         * @throws IllegalArgumentException 群或成员 OpenID 为空白，或 [memberOpenids] 为空。
         */
        @JvmStatic
        public fun create(
            groupOpenid: String,
            op: ModifyGroupMemberBlacklistOp,
            memberOpenids: Iterable<String>,
        ): ModifyGroupMemberBlacklistApi {
            require(groupOpenid.isNotBlank()) { "groupOpenid must not be blank" }
            val members = memberOpenids.toList()
            require(members.isNotEmpty()) { "memberOpenids must not be empty" }
            require(members.all { it.isNotBlank() }) { "memberOpenids must not contain blank IDs" }
            return ModifyGroupMemberBlacklistApi(groupOpenid, Body(op, members))
        }
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "member_blacklist")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupBlacklistModifyResult>
        get() = GroupBlacklistModifyResult.serializer()

    override fun createBody(): Any? = null

    @Serializable
    private class Body(
        val op: ModifyGroupMemberBlacklistOp,
        @SerialName("member_openids") val memberOpenids: List<String>,
    )
}

/**
 * 群黑名单变更操作。
 * @since 5.0
 */
public enum class ModifyGroupMemberBlacklistOp {
    /** 加入黑名单。 */
    @SerialName("add")
    ADD,

    /** 移出黑名单。 */
    @SerialName("del")
    DEL
}
