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
import love.forte.simbot.qguild.model.group.GroupMemberPage
import love.forte.simbot.qguild.model.group.GroupMemberInfo
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [获取群成员列表](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_group_openid_members.get.html)。
 *
 * 每次最多返回 30 条，支持分页。
 *
 * @since 5.0
 */
public class GetGroupMemberListApi private constructor(
    groupOpenid: String,
    private val cursor: String?,
) : GetQQGuildApi<GroupMemberPage>() {
    public companion object Factory : SimpleGetApiDescription("/v2/groups/{group_openid}/members") {
        /**
         * 构建 [GetGroupMemberListApi]。
         *
         * @param cursor 分页游标
         * */
        @JvmStatic
        @JvmOverloads
        public fun create(
            groupOpenid: String,
            cursor: String? = null,
        ): GetGroupMemberListApi = GetGroupMemberListApi(groupOpenid, cursor)
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "members")

    override val resultDeserializationStrategy: DeserializationStrategy<GroupMemberPage>
        get() = GroupMemberPage.serializer()

    override fun URLBuilder.buildUrl() {
        cursor?.also { parameters.append("cursor", it) }
    }
}

/**
 * 按游标连续拉取获取群成员列表结果。
 *
 * 返回冷流，收集时才请求。
 * 游标的连续性由 [GroupMemberPage.nextCursor] 决定而不是当前页码内的数据列。
 *
 * @since 5.0
 */
public inline fun GetGroupMemberListApi.Factory.createFlow(
    groupOpenid: String,
    cursor: String? = null,
    crossinline doRequest: suspend GetGroupMemberListApi.() -> GroupMemberPage,
): Flow<GroupMemberInfo> = flow {
    var currentCursor = cursor

    while (true) {
        val page = create(groupOpenid, currentCursor).doRequest()
        page.members.forEach { emit(it) }

        val nextCursor = page.nextCursor
        if (nextCursor.isEmpty()) break
        currentCursor = nextCursor
    }
}
