/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.qguild.api.forum

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.forum.Thread
import kotlin.jvm.JvmStatic

/**
 *
 * [获取帖子详情](https://bot.q.qq.com/wiki/develop/api/openapi/forum/get_thread.html)
 *
 * 该接口用于获取子频道下的帖子详情。
 *
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetThreadApi(channelId: String, threadId: String) : GetQQGuildApi<ThreadInfoResult>() {
    public companion object Factory : SimpleGetApiDescription(
        "/channels/{channel_id}/threads/{thread_id}"
    ) {
        /**
         * 构造 [GetThreadApi]
         * @param channelId 频道ID，需要是帖子类型
         * @param threadId 帖子ID
         */
        @JvmStatic
        public fun create(channelId: String, threadId: String): GetThreadApi =
            GetThreadApi(channelId, threadId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "threads", threadId)

    override val resultDeserializationStrategy: DeserializationStrategy<ThreadInfoResult>
        get() = ThreadInfoResult.serializer()
}

/**
 * [GetThreadApi] 响应体。
 */
@Serializable
public data class ThreadInfoResult(val thread: Thread)
