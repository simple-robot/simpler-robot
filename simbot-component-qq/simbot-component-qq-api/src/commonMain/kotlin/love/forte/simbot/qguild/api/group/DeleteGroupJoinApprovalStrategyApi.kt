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

import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmStatic

/**
 * [删除入群自动审批策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id.delete.html)。
 *
 * 删除指定的入群自动审批策略。
 *
 * @since 5.0
 */
public class DeleteGroupJoinApprovalStrategyApi private constructor(
    strategyId: String,
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    /**
     * [DeleteGroupJoinApprovalStrategyApi] 的描述与构建入口。
     */
    public companion object Factory : SimpleDeleteApiDescription("/v2/groups/join_approval_strategy/{strategy_id}") {
        /**
         * 构建 [DeleteGroupJoinApprovalStrategyApi]。
         *
         * @param strategyId 策略 ID。
         * @throws IllegalArgumentException [strategyId] 为空白。
         */
        @JvmStatic
        public fun create(strategyId: String): DeleteGroupJoinApprovalStrategyApi {
            require(strategyId.isNotBlank()) { "strategyId must not be blank" }
            return DeleteGroupJoinApprovalStrategyApi(strategyId)
        }
    }

    override val path: Array<String> = arrayOf("v2", "groups", "join_approval_strategy", strategyId)
}
