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
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic

/**
 * [帖子/评论/回复删除](https://developer.kookapp.cn/doc/http/thread#%E5%B8%96%E5%AD%90%2F%E8%AF%84%E8%AE%BA%2F%E5%9B%9E%E5%A4%8D%E5%88%A0%E9%99%A4)
 *
 * @since 4.3.0
 * @author Forte
 */
public class DeleteThreadApi private constructor(public override val body: Body) : KookPostApi<Unit>() {
    /**
     * 用于删除帖子/评论/回复的请求体。
     *
     * @since 4.3.0
     */
    @Serializable
    public class Body internal constructor(
        /** 频道 id */
        @SerialName("channel_id")
        public val channelId: String,

        /** 帖子id，删除整个帖子时必传，如果同时有post_id只会删除对应post */
        @SerialName("thread_id")
        public val threadId: String? = null,

        /** 评论or回复的id，删除评论/回复时必传 */
        @SerialName("post_id")
        public val postId: String? = null
    ) {
        override fun toString(): String {
            return "DeleteThreadApi.Body(" +
                    "channelId='$channelId', " +
                    "threadId=$threadId, " +
                    "postId=$postId)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false

            if (channelId != other.channelId) return false
            if (threadId != other.threadId) return false
            if (postId != other.postId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = channelId.hashCode()
            result = 31 * result + (threadId?.hashCode() ?: 0)
            result = 31 * result + (postId?.hashCode() ?: 0)
            return result
        }
    }

    public companion object Factory {
        private val PATH = ApiPath.create("thread", "delete")

        private val serializer = Unit.serializer()

        /**
         * 构造 [删除帖子/评论/回复][DeleteThreadApi] 请求。
         *
         * @param body 请求体
         */
        @JvmStatic
        public fun create(body: Body): DeleteThreadApi = DeleteThreadApi(body)

        /**
         * 构造删除整个帖子的 [DeleteThreadApi] 请求。
         *
         * @param channelId 频道 id
         * @param threadId 帖子id，删除整个帖子时必传
         */
        @JvmStatic
        public fun createForThread(
            channelId: String,
            threadId: String
        ): DeleteThreadApi = DeleteThreadApi(
            Body(
                channelId = channelId,
                threadId = threadId
            )
        )

        /**
         * 构造删除评论或回复的 [DeleteThreadApi] 请求。
         *
         * @param channelId 频道 id
         * @param postId 评论or回复的id，删除评论/回复时必传
         */
        @JvmStatic
        public fun createForPost(
            channelId: String,
            postId: String
        ): DeleteThreadApi = DeleteThreadApi(
            Body(
                channelId = channelId,
                postId = postId
            )
        )

        /**
         * 构造 [DeleteThreadApi] 请求。
         * 
         * 如果同时提供 threadId 和 postId，只会删除对应的 post。
         *
         * @param channelId 频道 id
         * @param threadId 帖子id，删除整个帖子时必传，如果同时有post_id只会删除对应post
         * @param postId 评论or回复的id，删除评论/回复时必传
         */
        @JvmStatic
        public fun create(
            channelId: String,
            threadId: String? = null,
            postId: String? = null
        ): DeleteThreadApi {
            require(threadId != null || postId != null) {
                "At least one of threadId or postId must be provided"
            }
            return DeleteThreadApi(
                Body(
                    channelId = channelId,
                    threadId = threadId,
                    postId = postId
                )
            )
        }
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = serializer
}
