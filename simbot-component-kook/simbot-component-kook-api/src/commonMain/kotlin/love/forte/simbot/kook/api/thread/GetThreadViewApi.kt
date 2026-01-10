/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

package love.forte.simbot.kook.api.thread

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic

/**
 * [帖子详情](https://developer.kookapp.cn/doc/http/thread#%E5%B8%96%E5%AD%90%E8%AF%A6%E6%83%85)
 *
 * @since 4.3.0
 * @author Forte
 */
public class GetThreadViewApi private constructor(
    /**
     * 频道 id
     */
    private val channelId: String,
    /**
     * 帖子 id
     */
    private val threadId: String
) : KookGetApi<ThreadView>() {
    public companion object Factory {
        private val PATH = ApiPath.create("thread", "view")

        private val serializer = ThreadView.serializer()

        /**
         * 构造 [帖子详情][GetThreadViewApi] 请求。
         *
         * @param channelId 频道 id
         * @param threadId 帖子 id
         */
        @JvmStatic
        public fun create(channelId: String, threadId: String): GetThreadViewApi =
            GetThreadViewApi(channelId, threadId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ThreadView>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("channel_id", channelId)
            append("thread_id", threadId)
        }
    }
}
