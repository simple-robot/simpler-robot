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
 * [入群自动审批策略列表](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy.get.html)。
 *
 * @property strategies 生效中的策略列表，按创建时间倒序。
 * @property nextCursor 下一页游标，空串表示已到末页。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupJoinApprovalStrategyPage @ApiModelConstructor internal constructor(
    public val strategies: List<GroupJoinApprovalStrategy>,
    @SerialName("next_cursor")
    public val nextCursor: String,
) {
    override fun toString(): String =
        "GroupJoinApprovalStrategyPage(strategies=$strategies, nextCursor='$nextCursor')"
}

/**
 * [入群自动审批策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy.get.html)。
 *
 * @property strategyId 策略 ID。
 * @property groupOpenids 关联的群 openid 列表（创建时使用 `group_openids` 时返回），未返回时为 `null`。
 * @property groupIds 关联的 QQ 群号列表（创建时使用 `group_ids` 时返回），未返回时为 `null`。
 * @property whitelistUserCount 白名单中的号码数量（估算，可能存在少量误差）。
 * @property isEnable 策略是否启用，`true` 对应 `on`，`false` 对应 `off`。
 * @property expireAt 过期时间。
 * @property createdAt 创建时间。
 * @property updatedAt 最近更新时间。
 * @property remark 策略备注。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupJoinApprovalStrategy @ApiModelConstructor internal constructor(
    @SerialName("strategy_id")
    public val strategyId: String,
    @SerialName("whitelist_user_count")
    public val whitelistUserCount: Int,
    @SerialName("is_enable")
    @Serializable(with = GroupJoinApprovalStrategyEnabledSerializer::class)
    public val isEnable: Boolean,
    @SerialName("expire_at")
    public val expireAt: Instant,
    @SerialName("created_at")
    public val createdAt: Instant,
    @SerialName("updated_at")
    public val updatedAt: Instant,
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
    @SerialName("group_ids")
    public val groupIds: List<String>? = null,
    public val remark: String? = null,
) {
    override fun toString(): String {
        return "GroupJoinApprovalStrategy(" +
            "strategyId='$strategyId', " +
            "groupOpenids=$groupOpenids, " +
            "groupIds=$groupIds, " +
            "whitelistUserCount=$whitelistUserCount, " +
            "isEnable=$isEnable, " +
            "expireAt=$expireAt, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "remark=$remark)"
    }
}
