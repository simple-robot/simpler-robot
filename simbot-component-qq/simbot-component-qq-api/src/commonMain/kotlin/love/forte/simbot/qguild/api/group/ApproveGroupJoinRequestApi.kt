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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePostApiDescription
import kotlin.jvm.JvmStatic

/**
 * [入群申请审批](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_approval_join_request_member_openid.post.html)。
 *
 * 机器人需要群管理员身份。
 *
 * @since 5.0
 */
public class ApproveGroupJoinRequestApi private constructor(
    groupOpenid: String,
    memberOpenid: String,
    override val body: Any,
) : PostQQGuildApi<Unit>(), QQGuildApiWithoutResult {
    /**
     * ApproveGroupJoinRequestApi 的构建入口。
     */
    public companion object Factory : SimplePostApiDescription(
        "/v2/groups/{group_openid}/approval_join_request/{member_openid}"
    ) {
        /**
         * 构建指定申请人的审批请求。
         *
         * @param groupOpenid 群OpenID
         * @param memberOpenid 成员OpenID
         * @param op 审批操作
         */
        @JvmStatic
        public fun create(
            groupOpenid: String,
            memberOpenid: String,
            op: ApproveGroupJoinRequestOp
        ): ApproveGroupJoinRequestApi =
            ApproveGroupJoinRequestApi(groupOpenid, memberOpenid, Body(op))

        /**
         * 构建指定申请人的审批请求。
         *
         * @param groupOpenid 群OpenID
         * @param memberOpenid 成员OpenID
         * @param op 审批操作
         *  @param joinRequestId 申请 ID；建议传入列表接口返回的 join_request_id。
         *  @param rejectReason 拒绝理由，仅拒绝时使用。
         *  @param addToMemberBlacklist 是否同时加入群黑名单，仅拒绝时使用。
         */
        @JvmStatic
        public fun create(
            groupOpenid: String,
            memberOpenid: String,
            op: ApproveGroupJoinRequestOp,
            joinRequestId: String? = null,
            rejectReason: String? = null,
            addToMemberBlacklist: Boolean? = null,
        ): ApproveGroupJoinRequestApi =
            ApproveGroupJoinRequestApi(
                groupOpenid = groupOpenid,
                memberOpenid = memberOpenid,
                body = Body(
                    op = op,
                    joinRequestId = joinRequestId,
                    rejectReason = rejectReason,
                    addToMemberBlacklist = addToMemberBlacklist
                )
            )
    }

    override val path: Array<String> =
        arrayOf("v2", "groups", groupOpenid, "approval_join_request", memberOpenid)

    override fun createBody(): Any? = null

    /**
     * 审批请求体。
     *
     * @property op 审批动作。
     * @property joinRequestId 申请 ID；建议传入列表接口返回的 join_request_id。
     * @property rejectReason 拒绝理由，仅拒绝时使用。
     * @property addToMemberBlacklist 是否同时加入群黑名单，仅拒绝时使用。
     *
     * @since 5.0
     */
    @Serializable
    internal class Body(
        val op: ApproveGroupJoinRequestOp,
        @SerialName("join_request_id") val joinRequestId: String? = null,
        @SerialName("reject_reason") val rejectReason: String? = null,
        @SerialName("add_to_member_blacklist") val addToMemberBlacklist: Boolean? = null,
    ) {
        override fun toString(): String {
            return "Body(op=$op, " +
                "joinRequestId=$joinRequestId, " +
                "rejectReason=$rejectReason, " +
                "addToMemberBlacklist=$addToMemberBlacklist)"
        }
    }
}

/**
 * [ApproveGroupJoinRequestApi] 的审批动作
 */
public enum class ApproveGroupJoinRequestOp {
    /**
     * 通过
     */
    @SerialName("approve")
    APPROVE,

    /**
     * 拒绝
     */
    @SerialName("decline")
    DECLINE
}
