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
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [帖子列表](https://developer.kookapp.cn/doc/http/thread#%E5%B8%96%E5%AD%90%E5%88%97%E8%A1%A8)
 *
 * @since 4.3.0
 * @author Forte
 */
public class GetThreadListApi private constructor(
    /**
     * 频道 id
     */
    private val channelId: String,
    /**
     * 帖子分区 id（若无默认为综合分区）
     */
    private val categoryId: String? = null,
    /**
     * 排序规则，1最新回复 2最新创建，默认按频道设置来
     */
    private val sort: Int? = null,
    /**
     * 分页，默认30
     */
    private val pageSize: Int? = null,
    /**
     * 翻页时，从什么时间开始找(传最后一个帖子的对应时间，sort=1时取latest_active_time，sort=2时取create_time)
     */
    private val time: Long? = null
) : KookGetApi<ThreadListData>() {
    public companion object Factory {
        private val PATH = ApiPath.create("thread", "list")

        private val serializer = ThreadListData.serializer()

        /**
         * 构造 [帖子列表][GetThreadListApi] 请求。
         *
         * @param channelId 频道 id
         * @param categoryId 帖子分区 id（若无默认为综合分区）
         * @param sort 排序规则，1最新回复 2最新创建，默认按频道设置来
         * @param pageSize 分页，默认30
         * @param time 翻页时，从什么时间开始找
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            channelId: String,
            categoryId: String? = null,
            sort: Int? = null,
            pageSize: Int? = null,
            time: Long? = null
        ): GetThreadListApi = GetThreadListApi(channelId, categoryId, sort, pageSize, time)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ThreadListData>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("channel_id", channelId)
            appendIfNotNull("category_id", categoryId) { it }
            appendIfNotNull("sort", sort) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
            appendIfNotNull("time", time) { it.toString() }
        }
    }
}

/**
 * [GetThreadListApi] 的响应结果。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class ThreadListData @ApiResultType constructor(
    /**
     * 帖子列表
     */
    public val items: List<ThreadView>
)

/**
 * 排序规则枚举。
 *
 * @since 4.3.0
 * @author Forte
 */
public enum class ThreadSortType(public val value: Int) {
    /**
     * 最新回复
     */
    LATEST_REPLY(1),
    
    /**
     * 最新创建
     */
    LATEST_CREATE(2)
}

/**
 * 批次量的通过 [GetThreadListApi] 查询所有结果。
 *
 * @param block 通过时间参数来通过 [GetThreadListApi] 发起一次请求
 */
public inline fun GetThreadListApi.Factory.createFlow(
    channelId: String,
    categoryId: String? = null,
    sort: Int? = null,
    pageSize: Int? = null,
    crossinline block: suspend GetThreadListApi.Factory.(time: Long?) -> ThreadListData
): Flow<ThreadListData> = flow {
    var time: Long? = null
    do {
        val listData = block(time)
        emit(listData)
        // 获取最后一个帖子的时间作为下次查询的起点
        val lastItem = listData.items.lastOrNull()
        time = when (sort) {
            1 -> lastItem?.latestActiveTime
            2 -> lastItem?.createTime
            else -> lastItem?.latestActiveTime ?: lastItem?.createTime
        }
    } while (listData.items.isNotEmpty())
}

/**
 * 批次量的通过 [GetThreadListApi] 查询所有结果，返回单个帖子的流。
 *
 * @param block 通过时间参数来通过 [GetThreadListApi] 发起一次请求
 */
public inline fun GetThreadListApi.Factory.createItemFlow(
    channelId: String,
    categoryId: String? = null,
    sort: Int? = null,
    pageSize: Int? = null,
    crossinline block: suspend GetThreadListApi.Factory.(time: Long?) -> ThreadListData
): Flow<ThreadView> = flow {
    var time: Long? = null
    do {
        val listData = block(time)
        listData.items.forEach { emit(it) }
        // 获取最后一个帖子的时间作为下次查询的起点
        val lastItem = listData.items.lastOrNull()
        time = when (sort) {
            1 -> lastItem?.latestActiveTime
            2 -> lastItem?.createTime
            else -> lastItem?.latestActiveTime ?: lastItem?.createTime
        }
    } while (listData.items.isNotEmpty())
}
