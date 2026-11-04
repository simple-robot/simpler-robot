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
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreate
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreateBuilder
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreated
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [创建入群自动审批策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy.post.html)。
 *
 * 创建入群自动审批策略，指定关联群号。`strategy_id` 由服务端生成。一个机器人最多 20 个策略。
 * 设置的规则在机器人拥有群管理员身份时生效。
 *
 * @since 5.0
 */
public class CreateGroupJoinApprovalStrategyApi private constructor(
    override val body: GroupJoinApprovalStrategyCreate,
) : PostQQGuildApi<GroupJoinApprovalStrategyCreated>() {
    /**
     * [CreateGroupJoinApprovalStrategyApi] 的描述与构建入口。
     */
    public companion object Factory : SimplePostApiDescription("/v2/groups/join_approval_strategy") {
        /**
         * 使用 [body] 构建 [CreateGroupJoinApprovalStrategyApi]。
         *
         * @param body 请求体。
         */
        @JvmStatic
        public fun create(body: GroupJoinApprovalStrategyCreate): CreateGroupJoinApprovalStrategyApi =
            CreateGroupJoinApprovalStrategyApi(body)

        /**
         * 通过 DSL 构建器创建 [CreateGroupJoinApprovalStrategyApi]。
         *
         * @param block 构建器配置。
         * @throws IllegalArgumentException 群标识组合无效、群列表为空、群 openid 或 QQ 群号为空白。
         */
        @JvmSynthetic
        public inline fun create(
            block: GroupJoinApprovalStrategyCreateBuilder.() -> Unit,
        ): CreateGroupJoinApprovalStrategyApi =
            create(GroupJoinApprovalStrategyCreateBuilder().apply(block).build())
    }

    override val path: Array<String> = arrayOf("v2", "groups", "join_approval_strategy")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupJoinApprovalStrategyCreated>
        get() = GroupJoinApprovalStrategyCreated.serializer()

    override fun createBody(): Any? = null
}
