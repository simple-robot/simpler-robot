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

package love.forte.simbot.kook.api.blacklist

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
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [获取黑名单列表](https://developer.kookapp.cn/doc/http/blacklist#%E8%8E%B7%E5%8F%96%E9%BB%91%E5%90%8D%E5%8D%95%E5%88%97%E8%A1%A8)
 *
 * @since 4.3.0
 *
 * @author ForteScarlet
 */
public class GetBlacklistListApi private constructor(
    private val guildId: String,
    /**
     * 目标页数
     */
    private val page: Int? = null,
    /**
     * 每页数据数量
     */
    private val pageSize: Int? = null,
) : KookGetApi<ListData<BlacklistItem>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("blacklist", "list")

        private val serializer = ListData.serializer(BlacklistItem.serializer())

        /**
         * 构造 [获取黑名单列表][GetBlacklistListApi] 请求。
         *
         * @param guildId 服务器id
         * @param page 目标页数
         * @param pageSize 每页数据数量
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            page: Int? = null,
            pageSize: Int? = null
        ): GetBlacklistListApi = GetBlacklistListApi(guildId, page, pageSize)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<BlacklistItem>>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("guild_id", guildId)
            appendIfNotNull("page", page) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }
}

/**
 * [GetBlacklistListApi] 的响应结果中的黑名单项。
 */
@Serializable
public data class BlacklistItem @ApiResultType constructor(
    /**
     * 用户id
     */
    @SerialName("user_id")
    val userId: String,
    /**
     * 加入黑名单的时间戳(毫秒)
     */
    @SerialName("created_time")
    val createdTime: Long,
    /**
     * 加入黑名单的原因
     */
    val remark: String,
    /**
     * 用户信息
     */
    val user: SimpleUser,
)

/**
 * 批次量的通过 [GetBlacklistListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetBlacklistListApi] 发起一次请求
 */
public inline fun GetBlacklistListApi.Factory.createFlow(
    crossinline block: suspend GetBlacklistListApi.Factory.(page: Int) -> ListData<BlacklistItem>
): Flow<ListData<BlacklistItem>> = flow {
    var page = 1
    do {
        val listData = block(page)
        emit(listData)
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}

/**
 * 批次量的通过 [GetBlacklistListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetBlacklistListApi] 发起一次请求
 */
public inline fun GetBlacklistListApi.Factory.createItemFlow(
    crossinline block: suspend GetBlacklistListApi.Factory.(page: Int) -> ListData<BlacklistItem>
): Flow<BlacklistItem> = flow {
    var page = 1
    do {
        val listData = block(page)
        listData.items.forEach { emit(it) }
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}
