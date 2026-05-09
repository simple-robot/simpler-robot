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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [回复列表](https://developer.kookapp.cn/doc/http/thread#%E5%9B%9E%E5%A4%8D%E5%88%97%E8%A1%A8)
 *
 * @since 4.3.0
 * @author Forte
 */
public class GetThreadPostApi private constructor(
    /**
     * 频道id
     */
    private val channelId: String,
    /**
     * 帖子id
     */
    private val threadId: String,
    /**
     * 某一楼评论的post_id，查看楼中楼需要
     */
    private val postId: String? = null,
    /**
     * 回复的create_time用于分页
     */
    private val time: String? = null,
    /**
     * 一页几个
     */
    private val pageSize: String? = null,
    /**
     * 'asc'升序 'desc'降序
     */
    private val order: String,
    /**
     * 页码
     */
    private val page: String
) : KookGetApi<ListData<Post>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("thread", "post")

        private val serializer = ListData.serializer(Post.serializer())

        /**
         * 构造 [回复列表][GetThreadPostApi] 请求。
         *
         * @param channelId 频道id
         * @param threadId 帖子id
         * @param order 'asc'升序 'desc'降序
         * @param page 页码
         * @param postId 某一楼评论的post_id，查看楼中楼需要
         * @param time 回复的create_time用于分页
         * @param pageSize 一页几个
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            channelId: String,
            threadId: String,
            order: String,
            page: String,
            postId: String? = null,
            time: String? = null,
            pageSize: String? = null
        ): GetThreadPostApi = GetThreadPostApi(channelId, threadId, postId, time, pageSize, order, page)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<Post>>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("channel_id", channelId)
            append("thread_id", threadId)
            append("order", order)
            append("page", page)
            appendIfNotNull("post_id", postId) { it }
            appendIfNotNull("time", time) { it }
            appendIfNotNull("page_size", pageSize) { it }
        }
    }
}

/**
 * 排序方式枚举。
 *
 * @since 4.3.0
 * @author Forte
 */
public enum class PostOrderType(public val value: String) {
    /**
     * 升序
     */
    ASC("asc"),

    /**
     * 降序
     */
    DESC("desc")
}

/**
 * 批次量的通过 [GetThreadPostApi] 查询所有结果直至最后一次响应的页码达到最大值。
 *
 * @param block 通过页码参数来通过 [GetThreadPostApi] 发起一次请求
 */
public inline fun GetThreadPostApi.Factory.createFlow(
    channelId: String,
    threadId: String,
    order: String,
    postId: String? = null,
    time: String? = null,
    pageSize: String? = null,
    crossinline block: suspend GetThreadPostApi.Factory.(page: String) -> ListData<Post>
): Flow<ListData<Post>> = flow {
    var page = 1
    do {
        val listData = block(page.toString())
        emit(listData)
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}

/**
 * 批次量的通过 [GetThreadPostApi] 查询所有结果，返回单个回复的流。
 *
 * @param block 通过页码参数来通过 [GetThreadPostApi] 发起一次请求
 */
public inline fun GetThreadPostApi.Factory.createItemFlow(
    channelId: String,
    threadId: String,
    order: String,
    postId: String? = null,
    time: String? = null,
    pageSize: String? = null,
    crossinline block: suspend GetThreadPostApi.Factory.(page: String) -> ListData<Post>
): Flow<Post> = flow {
    var page = 1
    do {
        val listData = block(page.toString())
        listData.items.forEach { emit(it) }
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}
