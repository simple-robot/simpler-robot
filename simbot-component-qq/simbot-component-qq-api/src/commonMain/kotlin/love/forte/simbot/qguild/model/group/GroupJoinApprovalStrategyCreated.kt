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
import kotlin.time.Instant

/**
 * 创建入群自动审批策略后的结果。
 *
 * @property strategyId 服务端生成的策略 ID。
 * @property isEnable 是否启用，`true` 对应 `on`，`false` 对应 `off`。
 * @property expireAt 过期时间。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupJoinApprovalStrategyCreated @ApiModelConstructor internal constructor(
    @SerialName("strategy_id")
    public val strategyId: String,
    @SerialName("is_enable")
    @Serializable(with = GroupJoinApprovalStrategyEnabledSerializer::class)
    public val isEnable: Boolean,
    @SerialName("expire_at")
    public val expireAt: Instant,
) {
    override fun toString(): String {
        return "GroupJoinApprovalStrategyCreated(" +
            "strategyId=$strategyId, " +
            "isEnable=$isEnable, " +
            "expireAt=$expireAt)"
    }
}
