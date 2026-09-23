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
import love.forte.simbot.qguild.model.group.GroupJoinRequest
import love.forte.simbot.qguild.model.group.GroupJoinRequestPage
import kotlin.jvm.JvmStatic

/**
 * [拉取入群申请列表](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_join_request_list.get.html)。
 *
 * 机器人需要群管理员身份。分页参数通过 URL 查询参数传递。
 *
 * @since 5.0
 */
public class GetGroupJoinRequestListApi private constructor(
    groupOpenid: String,
    private val cursor: String?,
    private val limit: Int?,
) : GetQQGuildApi<GroupJoinRequestPage>() {
    /**
     * GetGroupJoinRequestListApi 的构建入口。
     */
    public companion object Factory : SimpleGetApiDescription("/v2/groups/{group_openid}/join_request_list") {
        /**
         * 构建分页请求。limit 默认为平台的 20，最大为 50。
         *
         * @param groupOpenid 群OpenID
         */
        @JvmStatic
        public fun create(groupOpenid: String): GetGroupJoinRequestListApi =
            GetGroupJoinRequestListApi(groupOpenid, null, null)

        /**
         * 构建分页请求。limit 默认为平台的 20，最大为 50；参数不在本地校验。
         *
         * @param groupOpenid 群OpenID
         */
        @JvmStatic
        public fun create(groupOpenid: String, cursor: String? = null, limit: Int? = null): GetGroupJoinRequestListApi =
            GetGroupJoinRequestListApi(groupOpenid, cursor, limit)
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "join_request_list")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupJoinRequestPage>
        get() = GroupJoinRequestPage.serializer()

    override fun URLBuilder.buildUrl() {
        cursor?.also { parameters.append("cursor", it) }
        limit?.also { parameters.append("limit", it.toString()) }
    }
}

/**
 * 按平台游标连续拉取指定群的入群申请。
 *
 * 返回冷流，收集时才调用 [doRequest]。空页仍根据下一页游标继续；
 * 下一页游标为空时结束，游标重复时抛出 [IllegalStateException]。
 *
 * @param groupOpenid 群 OpenID。
 * @param cursor 起始游标，null 表示从第一页开始。
 * @param limit 每页数量；null 使用平台默认值。
 * @param doRequest 执行单页请求并返回结果。
 * @since 5.0
 */
public inline fun GetGroupJoinRequestListApi.Factory.createFlow(
    groupOpenid: String,
    cursor: String? = null,
    limit: Int? = null,
    crossinline doRequest: suspend GetGroupJoinRequestListApi.() -> GroupJoinRequestPage,
): Flow<GroupJoinRequest> = flow {
    var currentCursor = cursor
    val seenCursors = mutableSetOf<String>()
    cursor?.takeIf { it.isNotEmpty() }?.also { seenCursors.add(it) }

    while (true) {
        val page = create(groupOpenid, currentCursor, limit).doRequest()
        page.list.forEach { emit(it) }

        val nextCursor = page.nextCursor
        if (nextCursor.isEmpty()) break
        check(seenCursors.add(nextCursor)) { "Repeated group join request cursor: $nextCursor" }
        currentCursor = nextCursor
    }
}
