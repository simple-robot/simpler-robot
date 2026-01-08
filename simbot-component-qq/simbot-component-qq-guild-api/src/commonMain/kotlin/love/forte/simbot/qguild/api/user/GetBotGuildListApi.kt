/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.user

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.SimpleGuild
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [获取用户频道列表](https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html)
 *
 * 用于获取当前用户（机器人）所加入的频道列表，支持分页。
 *
 * 当 `HTTP Authorization` 中填入 `Bot Token` 是获取机器人的数据，填入 `Bearer Token` 则获取用户的数据。
 *
 */
public class GetBotGuildListApi private constructor(
    /**
     * 读此id之前的数据。
     *
     * [before] 设置时， 先反序，再分页
     */
    public val before: String?,

    /**
     * 读此id之后的数据。
     *
     * [after] 和 [before] 同时设置时， after 参数无效
     */
    public val after: String?,
    /**
     * 每次拉取多少条数据	最大不超过100，默认100
     */
    public val limit: Int = DEFAULT_LIMIT,
) : GetQQGuildApi<List<SimpleGuild>>() {

    public companion object Factory : SimpleGetApiDescription(
        "/users/@me/guilds"
    ) {
        private val route = arrayOf("users", "@me", "guilds")
        private val serializer = ListSerializer(SimpleGuild.serializer())
        public const val DEFAULT_LIMIT: Int = 100

        /**
         * 构造 [GetBotGuildListApi]
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            before: String? = null,
            after: String? = null,
            limit: Int = DEFAULT_LIMIT
        ): GetBotGuildListApi =
            GetBotGuildListApi(before, after, limit)

        /**
         * 构造 [GetBotGuildListApi]
         */
        @JvmStatic
        public fun create(limit: Int): GetBotGuildListApi =
            GetBotGuildListApi(before = null, after = null, limit)


        /**
         * 构造 [GetBotGuildListApi]。提供 [GetBotGuildListApi.before] 属性。
         *
         * @param limit [GetBotGuildListApi.limit]
         */
        @JvmStatic
        @JvmOverloads
        public fun createByBefore(before: String, limit: Int = DEFAULT_LIMIT): GetBotGuildListApi =
            create(before = before, after = null, limit)


        /**
         * 构造 [GetBotGuildListApi]。提供 [GetBotGuildListApi.after] 属性。
         *
         * @param limit [GetBotGuildListApi.limit]
         */
        @JvmStatic
        @JvmOverloads
        public fun createByAfter(after: String, limit: Int = DEFAULT_LIMIT): GetBotGuildListApi =
            create(before = null, after = after, limit)

    }

    override val resultDeserializationStrategy: DeserializationStrategy<List<SimpleGuild>>
        get() = serializer

    override val path: Array<String>
        get() = route

    override fun URLBuilder.buildUrl() {
        parameters.apply {
            if (before != null) {
                append("before", before.toString())
            }
            if (after != null) {
                append("after", after.toString())
            }
            val limit = limit
            if (limit > 0) {
                append("limit", limit.toString())
            }
        }
    }
}


/**
 * 使用流的方式查询所有频道服务器。
 *
 * @param after 第一次查询的 after，默认为null代表从头查询
 * @param batch 每次查询所使用的limit，默认为 [GetBotGuildListApi.DEFAULT_LIMIT]
 */
public inline fun GetBotGuildListApi.Factory.createFlow(
    batch: Int = DEFAULT_LIMIT,
    after: String? = null,
    crossinline doRequest: suspend GetBotGuildListApi.() -> List<SimpleGuild>
): Flow<SimpleGuild> {
    return flow {
        var after0 = after
        while (true) {
            val list = create(after = after0, limit = batch).doRequest()
            if (list.isEmpty()) {
                break
            }

            list.forEach { emit(it) }
            after0 = list.last().id
        }
    }
}
