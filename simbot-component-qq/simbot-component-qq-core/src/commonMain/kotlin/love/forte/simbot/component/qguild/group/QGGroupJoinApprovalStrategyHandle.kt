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

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModified
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModify
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModifyBuilder
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyWhitelistModified
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic

/**
 * 入群自动审批策略操作句柄，保存策略 ID 并提供对其的操作行为。
 *
 * ```kotlin
 * val strategy = bot.groupJoinApprovalStrategies.handle("策略ID".ID)
 * strategy.update { isEnable = false }
 * strategy.addWhitelistUsers(listOf("1234567".ID))
 * ```
 *
 * @since 5.0
 */
@SubclassOptInRequired(InternalForInheritanceQGGroupJoinApprovalStrategyApi::class)
public interface QGGroupJoinApprovalStrategyHandle : DeleteSupport {
    /**
     * 策略 ID。
     */
    public val id: ID

    /**
     * 修改策略的生效状态、失效时间或增删关联群。
     * 关联群增删操作的群标识形式须与创建时一致。
     *
     * @param input 修改请求。
     * @return 服务端返回的启用状态和过期时间。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝或产生错误。
     */
    @ST
    public suspend fun update(input: GroupJoinApprovalStrategyModify): GroupJoinApprovalStrategyModified

    /**
     * 对指定策略批量新增白名单 QQ 号码，单次最多 10000 个，号码上限 10W。
     *
     * @param users QQ 号码的 ID 列表。
     * @return 操作后策略当前白名单号码数（估算）及更新时间。
     * @throws IllegalArgumentException [users] 为空或包含空白号码。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝或产生错误。
     */
    @ST
    public suspend fun addWhitelistUsers(users: Collection<ID>): GroupJoinApprovalStrategyWhitelistModified

    /**
     * 对指定策略批量删除白名单 QQ 号码，单次最多 10000 个。
     *
     * @param users QQ 号码的 ID 列表。
     * @return 操作后策略当前白名单号码数（估算）及更新时间。
     * @throws IllegalArgumentException [users] 为空或包含空白号码。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝或产生错误。
     */
    @ST
    public suspend fun removeWhitelistUsers(users: Collection<ID>): GroupJoinApprovalStrategyWhitelistModified

    /**
     * 对策略关联的全部群发起全量扫描，命中白名单号码的入群申请自动审批通过。
     * 异步执行，约 10 分钟完成。成功返回表示服务端已接受本次执行请求。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝或产生错误。
     */
    @ST
    public suspend fun execute()

    /**
     * 删除指定的入群自动审批策略。
     *
     * @param options 删除选项，支持 [love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_FAILURE]。
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝或产生错误。
     */
    @ST
    override suspend fun delete(vararg options: DeleteOption)
}

/**
 * 使用 DSL 修改入群自动审批策略。
 *
 * @param block 构建器配置。
 * @throws IllegalArgumentException 请求参数不满足构建器约束。
 * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝或产生错误。
 * @since 5.0
 */
@JvmSynthetic
public suspend inline fun QGGroupJoinApprovalStrategyHandle.update(
    block: GroupJoinApprovalStrategyModifyBuilder.() -> Unit,
): GroupJoinApprovalStrategyModified = update(GroupJoinApprovalStrategyModifyBuilder().apply(block).build())
