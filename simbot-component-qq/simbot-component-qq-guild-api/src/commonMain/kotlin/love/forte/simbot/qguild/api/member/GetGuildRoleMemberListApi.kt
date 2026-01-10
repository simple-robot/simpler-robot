/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.member

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleMember
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [获取频道身份组成员列表](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_role_members.html)
 *
 * 用于获取 `guild_id` 频道中指定 `role_id` 身份组下所有成员的详情列表，支持分页。
 *
 * ### 有关返回结果的说明
 * 1. 每次返回的member数量与limit不一定完全相等。特定管理身份组下的成员可能存在一次性返回全部的情况
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetGuildRoleMemberListApi private constructor(
    guildId: String,
    roleId: String,
    /**
     * 将上一次回包中 `next` 填入， 如果是第一次请求填 `0`，默认为 `0`
     */
    private val startIndex: String? = null,
    /**
     * 分页大小，`1-400`，默认是 `1`。成员较多的频道尽量使用较大的 `limit` 值，以减少请求数
     */
    private val limit: Int,
) : GetQQGuildApi<GuildRoleMemberList>() {
    init {
        require(limit > 0) { "limit must > 0, but $limit" }
        if (limit > MAX_LIMIT) {
            // or throw error? or ignore?
            logger.warn("The maximum value of the limit is $MAX_LIMIT, but {}", limit)
        }
    }

    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/roles/{role_id}/members"
    ) {
        private val logger = LoggerFactory.logger<GetGuildRoleMemberListApi>()

        /**
         * [limit] 的最大可能值
         */
        public const val MAX_LIMIT: Int = 400

        /**
         * 构造 [GetGuildRoleMemberListApi]
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String, roleId: String, startIndex: String? = null, limit: Int = MAX_LIMIT
        ): GetGuildRoleMemberListApi = GetGuildRoleMemberListApi(guildId, roleId, startIndex, limit)

        /**
         * 构造 [GetGuildRoleMemberListApi]
         */
        @JvmStatic
        public fun create(
            guildId: String, roleId: String, limit: Int
        ): GetGuildRoleMemberListApi = GetGuildRoleMemberListApi(guildId, roleId, null, limit)
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "roles", roleId, "members")

    override val resultDeserializationStrategy: DeserializationStrategy<GuildRoleMemberList>
        get() = GuildRoleMemberList.serializer()

    override fun URLBuilder.buildUrl() {
        startIndex?.also { parameters.append("start_index", startIndex) }
        parameters.append("limit", limit.toString())
    }
}


/**
 * [GetGuildRoleMemberListApi] 的响应体包装类。
 */
@ApiModel
@Serializable
public data class GuildRoleMemberList(
    /**
     * 一组用户信息对象
     */
    val data: List<SimpleMember>,

    /**
     * 下一次请求的分页标识
     */
    val next: String
)


/**
 * 使用流的方式查询所有频道服务器。
 *
 * @param guildId 要查询的 guild id
 * @param batch 每次查询所使用的 limit，默认为 [GetGuildRoleMemberListApi.MAX_LIMIT]
 * @param startIndex 第一次查询的 startIndex，默认为null代表从头查询
 */
public inline fun GetGuildRoleMemberListApi.Factory.createFlow(
    guildId: String,
    roleId: String,
    batch: Int = MAX_LIMIT,
    startIndex: String? = null,
    crossinline doRequest: suspend GetGuildRoleMemberListApi.() -> GuildRoleMemberList
): Flow<SimpleMember> = flow {
    var after: String? = startIndex
    while (true) {
        val (list, next) = create(guildId = guildId, roleId = roleId, startIndex = after, limit = batch).doRequest()
        if (list.isEmpty()) {
            break
        }

        list.forEach { emit(it) }
        after = next
    }
}
