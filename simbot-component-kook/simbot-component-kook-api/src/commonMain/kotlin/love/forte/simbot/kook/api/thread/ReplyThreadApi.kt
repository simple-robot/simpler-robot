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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [评论/回复](https://developer.kookapp.cn/doc/http/thread#%E8%AF%84%E8%AE%BA%2F%E5%9B%9E%E5%A4%8D)
 *
 * @since 4.3.0
 * @author Forte
 */
public class ReplyThreadApi private constructor(public override val body: Body) : KookPostApi<Post>() {
    /**
     * 用于评论/回复的请求体。
     *
     * @since 4.3.0
     */
    @Serializable
    public class Body internal constructor(
        /** 频道 id */
        @SerialName("channel_id")
        public val channelId: String,

        /** 帖子 id */
        @SerialName("thread_id")
        public val threadId: String,

        /** 文本内容 */
        public val content: String,

        /** 回复的post_id，如果是评论主楼则不传，回复其它楼和楼中楼则必传 */
        @SerialName("reply_id")
        public val replyId: String? = null
    ) {
        override fun toString(): String {
            return "ReplyThreadApi.Body(" +
                "channelId='$channelId', " +
                "threadId='$threadId', " +
                "content='$content', " +
                "replyId=$replyId)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false

            if (channelId != other.channelId) return false
            if (threadId != other.threadId) return false
            if (content != other.content) return false
            if (replyId != other.replyId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = channelId.hashCode()
            result = 31 * result + threadId.hashCode()
            result = 31 * result + content.hashCode()
            result = 31 * result + (replyId?.hashCode() ?: 0)
            return result
        }
    }

    public companion object Factory {
        private val PATH = ApiPath.create("thread", "reply")

        private val serializer = Post.serializer()

        /**
         * 构造 [评论/回复][ReplyThreadApi] 请求。
         *
         * @param body 请求体
         */
        @JvmStatic
        public fun create(body: Body): ReplyThreadApi = ReplyThreadApi(body)

        /**
         * 构造 [评论/回复][ReplyThreadApi] 请求。
         *
         * @param channelId 频道 id
         * @param threadId 帖子 id
         * @param content 文本内容
         * @param replyId 回复的post_id，如果是评论主楼则不传，回复其它楼和楼中楼则必传
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            channelId: String,
            threadId: String,
            content: String,
            replyId: String? = null
        ): ReplyThreadApi = ReplyThreadApi(
            Body(
                channelId = channelId,
                threadId = threadId,
                content = content,
                replyId = replyId
            )
        )
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Post>
        get() = serializer
}
