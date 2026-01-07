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

package love.forte.simbot.kook.api.template

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.objects.template.SimpleTemplate
import love.forte.simbot.kook.objects.template.Template
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [获取模板列表](https://developer.kookapp.cn/doc/http/template)
 *
 * GET /api/v3/template/list
 *
 * @author ForteScarlet
 * @since 4.3.0
 */
@ExperimentalTemplateApi
public class GetTemplateListApi private constructor(
    /**
     * 目标页数
     */
    private val page: Int? = null,
    /**
     * 每页数据数量
     */
    private val pageSize: Int? = null,
) : KookGetApi<TemplatePageList<Template>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("template", "list")
        private val SER = TemplatePageList.serializer(SimpleTemplate.serializer())

        /**
         * 构造 [获取模板列表][GetTemplateListApi] 请求。
         *
         * @param page 目标页数
         * @param pageSize 每页数据数量
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            page: Int? = null,
            pageSize: Int? = null
        ): GetTemplateListApi = GetTemplateListApi(page, pageSize)
    }

    override val apiPath: ApiPath get() = PATH
    override val resultDeserializationStrategy: DeserializationStrategy<TemplatePageList<Template>>
        get() = SER

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            appendIfNotNull("page", page) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }
}

/*
meta 也tm不一样
"meta": {
			"currentPage": 1,
			"pageCount": 1,
			"perPage": 50,
			"totalCount": 3
		},
 */

@ExperimentalTemplateApi
@Serializable
public data class TemplatePageList<out T : Template> @ApiResultType constructor(
    val meta: TemplatePageMeta,
    val items: List<T>
)

@ExperimentalTemplateApi
@Serializable
public data class TemplatePageMeta @ApiResultType constructor(
    val currentPage: Int,
    val pageCount: Int,
    val perPage: Int,
    val totalCount: Int
)

/**
 * 批次量的通过 [GetTemplateListApi] 查询所有结果直至最后一次响应的 meta.page >= meta.pageTotal。
 *
 * @param block 通过一个页码参数来通过 [GetTemplateListApi] 发起一次请求
 */
@ExperimentalTemplateApi
public inline fun GetTemplateListApi.Factory.createFlow(
    crossinline block: suspend GetTemplateListApi.Factory.(page: Int) -> TemplatePageList<Template>
): Flow<TemplatePageList<Template>> = flow {
    var page = 1
    do {
        val templateList = block(page)
        emit(templateList)
        page = templateList.meta.currentPage + 1
    } while (templateList.items.isNotEmpty() &&
        templateList.meta.currentPage < templateList.meta.pageCount
    )
}

/**
 * 批次量的通过 [GetTemplateListApi] 查询所有结果直至最后一次响应的 meta.page >= meta.pageTotal。
 *
 * @param block 通过一个页码参数来通过 [GetTemplateListApi] 发起一次请求
 */
@ExperimentalTemplateApi
public inline fun GetTemplateListApi.Factory.createItemFlow(
    crossinline block: suspend GetTemplateListApi.Factory.(page: Int) -> TemplatePageList<Template>
): Flow<Template> = flow {
    var page = 1
    do {
        val templateList = block(page)
        templateList.items.forEach { emit(it) }
        page = templateList.meta.currentPage + 1
    } while (templateList.items.isNotEmpty() &&
        templateList.meta.currentPage < templateList.meta.pageCount
    )
}
