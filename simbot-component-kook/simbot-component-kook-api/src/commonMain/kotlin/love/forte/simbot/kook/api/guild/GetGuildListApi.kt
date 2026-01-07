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

package love.forte.simbot.kook.api.guild

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.api.ListMeta
import love.forte.simbot.kook.objects.SimpleGuild
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 *
 * [获取当前用户加入的服务器列表](https://developer.kookapp.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E5%8A%A0%E5%85%A5%E7%9A%84%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetGuildListApi private constructor(
    private val page: Int? = null,
    private val pageSize: Int? = null,
    private val sort: String? = null,
) : KookGetApi<ListData<SimpleGuild>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild", "list")
        private val deserializer = ListData.serializer(SimpleGuild.serializer())

        /**
         * 此分页API的第 `1` 页的 `50` 条数据查询。
         *
         */
        @JvmField
        public val FIRST_PAGE: GetGuildListApi = GetGuildListApi(page = 1, pageSize = 50)

        /**
         * 构造 [GetGuildListApi].
         *
         * @param page 目标页数
         * @param pageSize 每页数据数量
         * @param sort 代表排序的字段, 比如-id 代表 id 按 DESC 排序，id 代表 id 按 ASC 排序。不一定有, 如果有，接口中会声明支持的排序字段。
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(page: Int? = null, pageSize: Int? = null, sort: String): GetGuildListApi =
            GetGuildListApi(page, pageSize, sort)

        /**
         * 构造 [GetGuildListApi].
         *
         * @param page 目标页数
         * @param pageSize 每页数据数量
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(page: Int? = null, pageSize: Int? = null): GetGuildListApi {
            if ((page == null || page == FIRST_PAGE.page) && (pageSize == null || pageSize == FIRST_PAGE.pageSize)) {
                return FIRST_PAGE
            }

            return GetGuildListApi(page = page, pageSize = pageSize, sort = null)
        }

    }

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<SimpleGuild>> get() = deserializer
    override val apiPath: ApiPath get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            page?.also { append("page", it.toString()) }
            pageSize?.also { append("page_size", it.toString()) }
            sort?.also { append("sort", it) }
        }
    }
}


/**
 * 批次量的通过 [GetGuildListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetGuildListApi] 发起一次请求
 */
public inline fun GetGuildListApi.Factory.createFlow(
    crossinline block: suspend GetGuildListApi.Factory.(page: Int) -> ListData<SimpleGuild>
): Flow<ListData<SimpleGuild>> = flow {
    var page = 1
    do {
        val listData = block(page)
        emit(listData)
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}

/**
 * 批次量的通过 [GetGuildListApi] 查询所有结果直至最后一次响应的 [ListMeta.page] >= [ListMeta.pageTotal]。
 *
 * @param block 通过一个页码参数来通过 [GetGuildListApi] 发起一次请求
 */
public inline fun GetGuildListApi.Factory.createItemFlow(
    crossinline block: suspend GetGuildListApi.Factory.(page: Int) -> ListData<SimpleGuild>
): Flow<SimpleGuild> = flow {
    var page = 1
    do {
        val listData = block(page)
        listData.items.forEach { emit(it) }
        page = listData.meta.page + 1
    } while (listData.items.isNotEmpty() && listData.meta.page < listData.meta.pageTotal)
}
