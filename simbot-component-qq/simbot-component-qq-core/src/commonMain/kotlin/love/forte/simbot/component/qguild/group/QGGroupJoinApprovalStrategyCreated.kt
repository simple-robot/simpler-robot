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

import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreated
import kotlin.time.Instant

/**
 * 入群自动审批策略创建结果，保留服务端创建回执。
 *
 * @since 5.0
 */
@SubclassOptInRequired(InternalForInheritanceQGGroupJoinApprovalStrategyApi::class)
public abstract class QGGroupJoinApprovalStrategyCreated :
    QGObjectiveContainer<GroupJoinApprovalStrategyCreated>,
    QGGroupJoinApprovalStrategyHandle {
    /**
     * 策略是否启用。
     */
    public val isEnable: Boolean
        get() = source.isEnable

    /**
     * 过期时间。
     */
    public val expireAt: Instant
        get() = source.expireAt

}
