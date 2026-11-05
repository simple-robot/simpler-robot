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
 * 修改入群自动审批策略白名单后的结果。
 *
 * @property strategyId 策略 ID。
 * @property whitelistUserCount 操作后策略当前白名单号码数（估算）。
 * @property updatedAt 策略更新时间。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupJoinApprovalStrategyWhitelistModified @ApiModelConstructor internal constructor(
    @SerialName("strategy_id")
    public val strategyId: String,
    @SerialName("whitelist_user_count")
    public val whitelistUserCount: Int,
    @SerialName("updated_at")
    public val updatedAt: Instant,
) {
    override fun toString(): String {
        return "GroupJoinApprovalStrategyWhitelistModified(" +
            "strategyId=$strategyId, " +
            "whitelistUserCount=$whitelistUserCount, " +
            "updatedAt=$updatedAt)"
    }
}
