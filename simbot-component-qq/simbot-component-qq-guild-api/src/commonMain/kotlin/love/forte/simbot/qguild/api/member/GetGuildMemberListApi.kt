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
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleMember
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [获取频道成员列表](https://bot.q.qq.com/wiki/develop/api/openapi/member/get_members.html)
 *
 * 用于获取 guild_id 指定的频道中所有成员的详情列表，支持分页。
 *
 * ### 有关返回结果的说明
 * 1. 在每次翻页的过程中，可能会返回上一次请求已经返回过的 `member` 信息，需要调用方自己根据 `user id` 来进行去重。
 * 2. 每次返回的 `member` 数量与 `limit` 不一定完全相等。翻页请使用最后一个 `member` 的 `user id` 作为下一次请求的 `after` 参数，直到回包为空，拉取结束。
 *
 * @throws IllegalArgumentException [limit] 不大于0时
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetGuildMemberListApi private constructor(
    guildId: String,
    /**
     * 上一次回包中最后一个member的 `user id`， 如果是第一次请求填 0，默认为 0
     */
    private val after: String?,

    /**
     * 分页大小，1-400，默认是 1。成员较多的频道尽量使用较大的limit值，以减少请求数.
     */
    private val limit: Int,
) : GetQQGuildApi<List<SimpleMember>>() {
    init {
        require(limit > 0) { "limit must > 0, but $limit" }
        if (limit > MAX_LIMIT) {
            // or throw error? or ignore?
            logger.warn("The maximum value of the limit is $MAX_LIMIT, but {}", limit)
        }
    }

    public companion object Factory : SimpleGetApiDescription(
        "/guilds/{guild_id}/members"
    ) {
        /**
         * [GetGuildMemberListApi.limit] 的最大有效值。
         */
        public const val MAX_LIMIT: Int = 400

        private val deserializer = ListSerializer(SimpleMember.serializer())
        private val logger = LoggerFactory.logger<GetGuildMemberListApi>()

        /**
         * 构造 [GetGuildMemberListApi]
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, after: String? = null, limit: Int = 1): GetGuildMemberListApi =
            GetGuildMemberListApi(guildId, after, limit)

        /**
         * 构造 [GetGuildMemberListApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String, limit: Int): GetGuildMemberListApi =
            GetGuildMemberListApi(guildId, null, limit)
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "members")

    override val resultDeserializationStrategy: DeserializationStrategy<List<SimpleMember>>
        get() = deserializer

    override fun URLBuilder.buildUrl() {
        after?.also { parameters.append("after", it) }
        parameters.append("limit", limit.toString())
    }
}

/**
 * 使用流的方式查询所有频道服务器中的成员。
 *
 * @param guildId 要查询的 guild id
 * @param batch 每次查询所使用的 limit，默认为 [GetGuildMemberListApi.MAX_LIMIT]
 * @param after 第一次查询的 after，默认为null代表从头查询
 */
public inline fun GetGuildMemberListApi.Factory.createFlow(
    guildId: String,
    batch: Int = MAX_LIMIT,
    after: String? = null,
    crossinline doRequest: suspend GetGuildMemberListApi.() -> List<SimpleMember>
): Flow<SimpleMember> = flow {
    var after0: String? = after
    while (true) {
        val list = create(guildId, after = after0, limit = batch).doRequest()
        if (list.isEmpty()) {
            break
        }

        list.forEach { emit(it) }
        after0 = list.last().user.id
    }
}
