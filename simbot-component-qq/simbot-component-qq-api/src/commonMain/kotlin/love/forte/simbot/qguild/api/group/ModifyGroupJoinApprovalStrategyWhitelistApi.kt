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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyOp
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyWhitelistModified
import kotlin.jvm.JvmStatic

/**
 * [修改入群自动审批策略的白名单号码](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id_whitelist_users.post.html)。
 *
 * 对指定策略批量新增或删除白名单 QQ 号码，单次最多 10000 个，号码上限 10W。
 *
 * @since 5.0
 */
public class ModifyGroupJoinApprovalStrategyWhitelistApi private constructor(
    strategyId: String,
    override val body: Any,
) : PostQQGuildApi<GroupJoinApprovalStrategyWhitelistModified>() {
    /**
     * [ModifyGroupJoinApprovalStrategyWhitelistApi] 的描述与构建入口。
     */
    public companion object Factory : SimplePostApiDescription("/v2/groups/join_approval_strategy/{strategy_id}/whitelist_users") {
        /**
         * 构建一次白名单号码操作请求。
         *
         * @param strategyId 策略 ID。
         * @param op 操作类型：ADD 新增号码，DEL 删除号码。
         * @param whitelistUsers QQ 号码列表，单次最多 10000 个；使用字符串类型避免 JS 精度问题。
         * 不做本地上限校验。
         * @throws IllegalArgumentException [strategyId] 为空白、[whitelistUsers] 为空或包含空白号码。
         */
        @JvmStatic
        public fun create(
            strategyId: String,
            op: GroupJoinApprovalStrategyOp,
            whitelistUsers: Iterable<String>,
        ): ModifyGroupJoinApprovalStrategyWhitelistApi {
            require(strategyId.isNotBlank()) { "strategyId must not be blank" }
            val users = whitelistUsers.toList()
            require(users.isNotEmpty()) { "whitelistUsers must not be empty" }
            require(users.all { it.isNotBlank() }) { "whitelistUsers must not contain blank numbers" }
            return ModifyGroupJoinApprovalStrategyWhitelistApi(strategyId, Body(op, users))
        }
    }

    override val path: Array<String> = arrayOf("v2", "groups", "join_approval_strategy", strategyId, "whitelist_users")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupJoinApprovalStrategyWhitelistModified>
        get() = GroupJoinApprovalStrategyWhitelistModified.serializer()

    override fun createBody(): Any? = null

    @Serializable
    private class Body(
        val op: GroupJoinApprovalStrategyOp,
        @SerialName("whitelist_users") val whitelistUsers: List<String>,
    )
}
