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

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategy
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyPage
import kotlin.jvm.JvmStatic

/**
 * [查询入群自动审批策略列表](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy.get.html)。
 *
 * 查询当前生效中的策略列表，按创建时间倒序，支持分页。
 *
 * @since 5.0
 */
public class GetGroupJoinApprovalStrategyListApi private constructor(
    private val cursor: String?,
    private val limit: Int?,
) : GetQQGuildApi<GroupJoinApprovalStrategyPage>() {
    init {
        require(limit == null || limit > 0) { "limit must be positive" }
    }

    /**
     * GetGroupJoinApprovalStrategyListApi 的构建入口。
     */
    public companion object Factory : SimpleGetApiDescription("/v2/groups/join_approval_strategy") {
        /**
         * 构建分页请求。limit 默认为平台的 20，最大为 50。
         */
        @JvmStatic
        public fun create(): GetGroupJoinApprovalStrategyListApi =
            GetGroupJoinApprovalStrategyListApi(null, null)

        /**
         * 构建分页请求。
         *
         * @param cursor 分页游标，首次请求可不传或传空串。
         * @param limit 单页数量，默认 20，最大 50（不做本地上限校验）。
         * @throws IllegalArgumentException [limit] 不为正数。
         */
        @JvmStatic
        public fun create(
            cursor: String? = null,
            limit: Int? = null,
        ): GetGroupJoinApprovalStrategyListApi =
            GetGroupJoinApprovalStrategyListApi(cursor, limit)
    }

    override val path: Array<String> = arrayOf("v2", "groups", "join_approval_strategy")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupJoinApprovalStrategyPage>
        get() = GroupJoinApprovalStrategyPage.serializer()

    override fun URLBuilder.buildUrl() {
        cursor?.also { parameters.append("cursor", it) }
        limit?.also { parameters.append("limit", it.toString()) }
    }
}

/**
 * 按平台游标连续拉取入群自动审批策略。
 *
 * 返回冷流，收集时才调用 [doRequest]。空页仍根据下一页游标继续，下一页游标为空时结束。
 *
 * @param cursor 起始游标，null 表示从第一页开始。
 * @param limit 每页数量；null 使用平台默认值。
 * @param doRequest 执行单页请求并返回结果。
 * @throws IllegalArgumentException [limit] 不为正数。
 * @since 5.0
 */
public inline fun GetGroupJoinApprovalStrategyListApi.Factory.createFlow(
    cursor: String? = null,
    limit: Int? = null,
    crossinline doRequest: suspend GetGroupJoinApprovalStrategyListApi.() -> GroupJoinApprovalStrategyPage,
): Flow<GroupJoinApprovalStrategy> = flow {
    var currentCursor = cursor

    while (true) {
        val page = create(currentCursor, limit).doRequest()
        page.strategies.forEach { emit(it) }

        val nextCursor = page.nextCursor
        if (nextCursor.isEmpty()) break
        currentCursor = nextCursor
    }
}
