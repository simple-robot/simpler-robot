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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.ability.AcceptOption
import love.forte.simbot.ability.AcceptSupport
import love.forte.simbot.ability.RejectOption
import love.forte.simbot.ability.RejectSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.qguild.common.QGInternalInheritanceApi
import love.forte.simbot.qguild.model.group.GroupJoinApplySource
import love.forte.simbot.qguild.model.group.GroupJoinRequest
import love.forte.simbot.qguild.model.group.GroupJoinVerifyInfo
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmExposeBoxed
import kotlin.time.Instant

/**
 * 一条可直接审批的入群申请。
 *
 * [QGGroupJoinRequest] 实现 [RejectSupport], [AcceptSupport]，可以通过
 * [QGGroupJoinRequest.reject] 和 [QGGroupJoinRequest.accept] 方法进行审批。
 * 其中，[QGGroupJoinRequest.reject] 的 `options` 参数支持额外提供
 * [QGGroupJoinRequestRejectOption] 类型的可选项作为有效参数。
 *
 * @since 5.0
 * @author Forte Scarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@SubclassOptInRequired(QGInternalInheritanceApi::class)
public abstract class QGGroupJoinRequest : QGObjectiveContainer<GroupJoinRequest>, RejectSupport, AcceptSupport {
    /**
     * 审批时回传的申请 ID。
     */
    public val joinRequestId: ID
        get() = source.joinRequestId.ID

    /**
     * 申请人的群成员 OpenID。
     */
    public val memberOpenid: ID
        get() = source.memberOpenid.ID

    /**
     * 申请人昵称。
     */
    public val username: String
        get() = source.username

    /**
     * 申请时间点。
     */
    public val applyAt: Instant
        get() = source.applyAt

    /**
     * 入群申请来源
     */
    @get:JvmExposeBoxed
    public val applySource: GroupJoinApplySource
        get() = source.applySource

    /**
     * 是否为机器人账号。
     */
    public val bot: Boolean
        get() = source.bot

    /**
     * 安全提示语。
     */
    public val riskTips: String?
        get() = source.riskTips

    /**
     * 应用或开放平台统一标识，如有。
     */
    public val unionOpenid: ID?
        get() = source.unionOpenid?.ID

    /**
     * 邀请人 OpenID；受邀申请时有效。
     */
    public val invitedBy: ID?
        get() = source.invitedBy?.ID

    /**
     * 入群验证信息，如有。
     */
    public val verifyInfo: GroupJoinVerifyInfo?
        get() = source.verifyInfo

    /**
     * 拒绝此入群申请。
     */
    @ST
    abstract override suspend fun reject()

    /**
     * 拒绝此入群申请。
     *
     * @param options 额外可选项。支持提供 [QGGroupJoinRequestRejectOption] 类型的额外选项。
     */
    @ST
    abstract override suspend fun reject(vararg options: RejectOption)

    /**
     * 接受此入群审批。
     */
    @ST
    abstract override suspend fun accept()

    /**
     * 接受此入群审批。
     */
    @ST
    abstract override suspend fun accept(vararg options: AcceptOption)
}
