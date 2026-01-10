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

package love.forte.simbot.qguild.api.forum

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.NumberAsBooleanSerializer
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.forum.Thread
import kotlin.jvm.JvmStatic

/**
 *
 * [获取帖子列表](https://bot.q.qq.com/wiki/develop/api/openapi/forum/get_threads_list.html)
 *
 * 该接口用于获取子频道下的帖子列表。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetThreadListApi private constructor(channelId: String) : GetQQGuildApi<ThreadListResult>() {
    public companion object Factory : SimpleGetApiDescription("/channels/{channel_id}/threads") {

        /**
         * 构造 [GetThreadListApi].
         *
         * @param channelId 频道ID
         */
        @JvmStatic
        public fun create(channelId: String): GetThreadListApi =
            GetThreadListApi(channelId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "threads")

    override val resultDeserializationStrategy: DeserializationStrategy<ThreadListResult>
        get() = ThreadListResult.serializer()
}

/**
 * API [GetThreadListApi] 的响应体。
 */
@ApiModel
@Serializable
public data class ThreadListResult(
    /**
     * 帖子列表对象（返回值里面的content字段，可参照RichText结构）
     */
    val threads: List<Thread>,
    /**
     * 是否拉取完毕(0:否；1:是)
     */
    @SerialName("is_finish")
    @Serializable(with = NumberAsBooleanSerializer::class)
    val isFinish: Boolean
) : Iterable<Thread> by threads
