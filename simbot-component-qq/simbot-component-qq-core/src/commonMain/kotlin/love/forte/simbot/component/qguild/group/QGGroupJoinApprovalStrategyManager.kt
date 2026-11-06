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

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreate
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreateBuilder
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic

/**
 * 机器人创建的入群自动审批策略管理器。
 *
 * ```kotlin
 * val created = bot.groupJoinApprovalStrategies.create {
 *     addGroupOpenid("群OpenID")
 *     isEnable = true
 * }
 * created.execute()
 * ```
 *
 * @since 5.0
 */
@SubclassOptInRequired(InternalForInheritanceQGGroupJoinApprovalStrategyApi::class)
public interface QGGroupJoinApprovalStrategyManager {
    /**
     * 创建只携带策略 ID 的操作句柄，此操作不会产生请求。
     *
     * @param strategyId 策略 ID。
     * @throws IllegalArgumentException [strategyId] 为空白。
     */
    public fun handle(strategyId: ID): QGGroupJoinApprovalStrategyHandle

    /**
     * 创建入群自动审批策略，指定关联群号。策略 ID 由服务端生成。
     * 一个机器人最多 20 个策略，设置的规则在机器人拥有群管理员身份时生效。
     *
     * @param input 创建请求。
     * @return 保留创建回执的策略操作句柄。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝。
     */
    @ST
    public suspend fun create(input: GroupJoinApprovalStrategyCreate): QGGroupJoinApprovalStrategyCreated

    /**
     * 查询当前生效中的策略列表，按创建时间倒序，支持分页。
     *
     * 返回内部是冷流的 [Collectable]，收集时才会开始产生请求并自动分页。
     *
     * @param startCursor 可选提供的首个分页游标。
     * @param limit 单页数量，默认 20，最大 50（不做本地上限校验）。
     * @param autoPagination 是否自动分页。`false` 表示仅请求一页，默认为 `true`，即抓取所有数据。
     * @throws IllegalArgumentException 收集时 [limit] 不为正数。
     * @throws love.forte.simbot.qguild.QQGuildApiException 收集时请求被 QQ API 拒绝。
     */
    public fun list(
        startCursor: String? = null,
        limit: Int? = null,
        autoPagination: Boolean = true,
    ): Collectable<QGGroupJoinApprovalStrategyRecord>

    /**
     * 从第一页开始查询策略，收集时自动分页。
     */
    public fun list(): Collectable<QGGroupJoinApprovalStrategyRecord> = list(startCursor = null)
}

/**
 * 使用 DSL 创建入群自动审批策略。
 *
 * @param block 构建器配置。
 * @throws IllegalArgumentException 请求参数不满足构建器约束。
 * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝。
 * @since 5.0
 */
@JvmSynthetic
public suspend inline fun QGGroupJoinApprovalStrategyManager.create(
    block: GroupJoinApprovalStrategyCreateBuilder.() -> Unit,
): QGGroupJoinApprovalStrategyCreated = create(GroupJoinApprovalStrategyCreateBuilder().apply(block).build())
