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

package love.forte.simbot.qguild.model.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import kotlin.jvm.JvmExposeBoxed
import kotlin.time.Instant

/**
 * 群入群申请分页结果。
 *
 * @property list 本页申请。
 * @property nextCursor 下一页游标；空串表示已到末页。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupJoinRequestPage @ApiModelConstructor internal constructor(
    public val list: List<GroupJoinRequest> = emptyList(),
    @SerialName("next_cursor")
    public val nextCursor: String = "",
) {
    override fun toString(): String {
        return "GroupJoinRequestPage(list=$list, nextCursor='$nextCursor')"
    }
}

/**
 * 一条入群申请。
 *
 * @property joinRequestId 审批时回传的申请 ID。
 * @property memberOpenid 申请人的群成员 OpenID。
 * @property username 申请人昵称。
 * @property applyAt 申请时间点。
 * @property applySource 入群申请来源。
 * @property bot 是否为机器人账号。
 * @property riskTips 安全提示语。
 * @property unionOpenid 应用或开放平台统一标识，如有。
 * @property invitedBy 邀请人 OpenID；受邀申请时有效。
 * @property verifyInfo 入群验证信息，如有。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@ApiModel
@Serializable
public class GroupJoinRequest @ApiModelConstructor internal constructor(
    @SerialName("join_request_id")
    public val joinRequestId: String,
    @SerialName("member_openid")
    public val memberOpenid: String,
    public val username: String,
    @SerialName("apply_at")
    public val applyAt: Instant,
    @SerialName("apply_source")
    @get:JvmExposeBoxed
    public val applySource: GroupJoinApplySource,
    public val bot: Boolean,
    @SerialName("risk_tips")
    public val riskTips: String? = null,
    @SerialName("union_openid")
    public val unionOpenid: String? = null,
    @SerialName("invited_by")
    public val invitedBy: String? = null,
    @SerialName("verify_info")
    public val verifyInfo: GroupJoinVerifyInfo? = null,
) {
    override fun toString(): String {
        return "GroupJoinRequest(invitedBy=$invitedBy, " +
            "unionOpenid=$unionOpenid, " +
            "riskTips=$riskTips, " +
            "bot=$bot, " +
            "applySource=$applySource, " +
            "applyAt=$applyAt, " +
            "username='$username', " +
            "memberOpenid='$memberOpenid', " +
            "joinRequestId='$joinRequestId')"
    }
}

/**
 * 入群申请的验证信息。
 *
 * @property method 入群验证方式。
 * @property verifyMessage 验证消息；消息验证时可能携带。
 * @property reviewQaList 管理员审核问答。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@ApiModel
@Serializable
public class GroupJoinVerifyInfo @ApiModelConstructor internal constructor(
    @get:JvmExposeBoxed
    public val method: GroupJoinVerifyMethod,
    @SerialName("verify_message")
    public val verifyMessage: String? = null,
    @SerialName("review_qa_list")
    public val reviewQaList: List<GroupJoinReviewQa> = emptyList(),
) {
    override fun toString(): String {
        return "GroupJoinVerifyInfo(method=$method, " +
            "verifyMessage=$verifyMessage, " +
            "reviewQaList=$reviewQaList)"
    }
}

/**
 * 入群申请中的一组审核问答。
 *
 * @property question 管理员设置的问题。
 * @property answer 申请人填写的答案。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupJoinReviewQa @ApiModelConstructor internal constructor(
    public val question: String,
    public val answer: String,
) {
    override fun toString(): String {
        return "GroupJoinReviewQa(question='$question', answer='$answer')"
    }
}
