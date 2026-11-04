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

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.PatchQQGuildApi
import love.forte.simbot.qguild.api.SimplePatchApiDescription
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModified
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModify
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModifyBuilder
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [修改入群自动审批策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id.patch.html)。
 *
 * 修改策略的生效状态、失效时间或增删关联群。
 * 关联群增删操作的群标识形式须与创建时一致。
 *
 * @since 5.0
 */
public class ModifyGroupJoinApprovalStrategyApi private constructor(
    strategyId: String,
    override val body: GroupJoinApprovalStrategyModify,
) : PatchQQGuildApi<GroupJoinApprovalStrategyModified>() {
    /**
     * [ModifyGroupJoinApprovalStrategyApi] 的描述与构建入口。
     */
    public companion object Factory : SimplePatchApiDescription("/v2/groups/join_approval_strategy/{strategy_id}") {
        /**
         * 使用 [body] 构建 [ModifyGroupJoinApprovalStrategyApi]。
         *
         * @param strategyId 策略 ID。
         * @param body 请求体。
         * @throws IllegalArgumentException [strategyId] 为空白。
         */
        @JvmStatic
        public fun create(
            strategyId: String,
            body: GroupJoinApprovalStrategyModify,
        ): ModifyGroupJoinApprovalStrategyApi {
            require(strategyId.isNotBlank()) { "strategyId must not be blank" }
            return ModifyGroupJoinApprovalStrategyApi(strategyId, body)
        }

        /**
         * 通过 DSL 构建器创建 [ModifyGroupJoinApprovalStrategyApi]。
         *
         * @param strategyId 策略 ID。
         * @param block 构建器配置。
         * @throws IllegalArgumentException [strategyId] 为空白或请求参数不满足构建器约束。
         */
        @JvmSynthetic
        public inline fun create(
            strategyId: String,
            block: GroupJoinApprovalStrategyModifyBuilder.() -> Unit,
        ): ModifyGroupJoinApprovalStrategyApi =
            create(strategyId, GroupJoinApprovalStrategyModifyBuilder().apply(block).build())
    }

    override val path: Array<String> = arrayOf("v2", "groups", "join_approval_strategy", strategyId)

    override val resultDeserializationStrategy: DeserializationStrategy<GroupJoinApprovalStrategyModified>
        get() = GroupJoinApprovalStrategyModified.serializer()

    override fun createBody(): Any? = null
}
