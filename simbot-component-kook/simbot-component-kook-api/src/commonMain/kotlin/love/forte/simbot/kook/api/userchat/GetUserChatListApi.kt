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

package love.forte.simbot.kook.api.userchat

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.api.ListMeta
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [获取私信聊天会话列表](https://developer.kookapp.cn/doc/http/user-chat#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetUserChatListApi private constructor(
    /**
     * 目标页数
     */
    private val page: Int? = null,
    /**
     * 每页数据数量
     */
    private val pageSize: Int? = null,
) : KookGetApi<ListData<UserChatListView>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("user-chat", "list")

        private val serializer = ListData.serializer(UserChatListView.serializer())

        private val EMPTY = GetUserChatListApi()

        /**
         * 构造 [获取私信聊天会话列表][GetUserChatListApi] 请求。
         *
         * @param page 目标页数
         * @param pageSize 每页数据数量
         */
        @JvmStatic
        @JvmOverloads
        public fun create(page: Int? = null, pageSize: Int? = null): GetUserChatListApi =
            if (page == null && pageSize == null) EMPTY else GetUserChatListApi(page, pageSize)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<UserChatListView>>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            appendIfNotNull("page", page) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }
}


/**
 * [GetUserChatListApi] 的响应结果。
 */
@Serializable
public data class UserChatListView @ApiResultType constructor(
    val code: String,
    /**
     * 上次阅读消息的时间 (毫秒)
     */
    @SerialName("last_read_time") val lastReadTime: Long,
    /**
     * 最新消息时间 (毫秒)
     */
    @SerialName("latest_msg_time") val latestMsgTime: Long,
    /**
     * 未读消息数
     */
    @SerialName("unread_count") val unreadCount: Int,
    /**
     * 目标用户信息
     */
    @SerialName("target_info") val targetInfo: TargetInfo,
)


/**
 * 批次量的通过 [GetUserChatListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetUserChatListApi] 发起一次请求
 */
public inline fun GetUserChatListApi.Factory.createFlow(
    crossinline block: suspend GetUserChatListApi.Factory.(page: Int) -> ListData<UserChatListView>
): Flow<ListData<UserChatListView>> = flow {
    var page = 1
    do {
        val listData = block(page)
        emit(listData)
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}

/**
 * 批次量的通过 [GetUserChatListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetUserChatListApi] 发起一次请求
 */
public inline fun GetUserChatListApi.Factory.createItemFlow(
    crossinline block: suspend GetUserChatListApi.Factory.(page: Int) -> ListData<UserChatListView>
): Flow<UserChatListView> = flow {
    var page = 1
    do {
        val listData = block(page)
        listData.items.forEach { emit(it) }
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}
