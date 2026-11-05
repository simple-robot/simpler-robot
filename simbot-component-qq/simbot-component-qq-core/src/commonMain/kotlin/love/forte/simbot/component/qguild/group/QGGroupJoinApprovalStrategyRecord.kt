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

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategy
import kotlin.time.Instant

/**
 * 入群自动审批策略记录，内容为查询时的瞬时快照。
 *
 * @since 5.0
 */
@SubclassOptInRequired(InternalForInheritanceQGGroupJoinApprovalStrategyApi::class)
public abstract class QGGroupJoinApprovalStrategyRecord :
    QGObjectiveContainer<GroupJoinApprovalStrategy>,
    QGGroupJoinApprovalStrategyHandle {
    /**
     *  关联的群 openid 列表，未返回时为 null。
     */
    public val groupOpenids: List<ID>?
        get() = source.groupOpenids?.map { it.ID }

    /**
     *  关联的 QQ 群号列表，未返回时为 null。
     */
    public val groupIds: List<ID>?
        get() = source.groupIds?.map { it.ID }

    /**
     *  白名单中的号码数量（估算，可能存在少量误差）。
     */
    public val whitelistUserCount: Int
        get() = source.whitelistUserCount

    /**
     *  策略是否启用。
     */
    public val isEnable: Boolean
        get() = source.isEnable

    /**
     *  过期时间。
     */
    public val expireAt: Instant
        get() = source.expireAt

    /**
     *  创建时间。
     */
    public val createdAt: Instant
        get() = source.createdAt

    /**
     *  最近更新时间。
     */
    public val updatedAt: Instant
        get() = source.updatedAt

    /**
     *  策略备注。
     */
    public val remark: String?
        get() = source.remark

}
