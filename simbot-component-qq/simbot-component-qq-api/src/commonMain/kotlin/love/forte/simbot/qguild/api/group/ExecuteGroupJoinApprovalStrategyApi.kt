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

import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePostApiDescription
import kotlin.jvm.JvmStatic

/**
 * [执行入群自动审批策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id_execute.post.html)。
 *
 * 对策略关联的全部群发起全量扫描，命中白名单号码的入群申请自动审批通过。异步执行，约 10 分钟完成。
 *
 * 请求成功表示服务端已接受本次执行请求。
 *
 * @since 5.0
 */
public class ExecuteGroupJoinApprovalStrategyApi private constructor(
    strategyId: String,
) : QQGuildApiWithoutResult, PostQQGuildApi<Unit>() {
    /**
     * [ExecuteGroupJoinApprovalStrategyApi] 的描述与构建入口。
     */
    public companion object Factory : SimplePostApiDescription("/v2/groups/join_approval_strategy/{strategy_id}/execute") {
        /**
         * 构建 [ExecuteGroupJoinApprovalStrategyApi]。
         *
         * @param strategyId 策略 ID。
         * @throws IllegalArgumentException [strategyId] 为空白。
         */
        @JvmStatic
        public fun create(strategyId: String): ExecuteGroupJoinApprovalStrategyApi {
            require(strategyId.isNotBlank()) { "strategyId must not be blank" }
            return ExecuteGroupJoinApprovalStrategyApi(strategyId)
        }
    }

    override val path: Array<String> = arrayOf("v2", "groups", "join_approval_strategy", strategyId, "execute")

    override fun createBody(): Any? = null
}
