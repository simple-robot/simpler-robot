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

package love.forte.simbot.kook.api.category

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.thread.Category
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic

/**
 * [获取帖子分区列表](https://developer.kookapp.cn/doc/http/thread#%E8%8E%B7%E5%8F%96%E5%B8%96%E5%AD%90%E5%88%86%E5%8C%BA%E5%88%97%E8%A1%A8)
 *
 * @since 4.3.0
 * @author Forte
 */
public class GetCategoryListApi private constructor(
    /**
     * 帖子频道 id
     */
    private val channelId: String
) : KookGetApi<CategoryListData>() {
    public companion object Factory {
        private val PATH = ApiPath.create("category", "list")

        private val serializer = CategoryListData.serializer()

        /**
         * 构造 [获取帖子分区列表][GetCategoryListApi] 请求。
         *
         * @param channelId 帖子频道 id
         */
        @JvmStatic
        public fun create(channelId: String): GetCategoryListApi =
            GetCategoryListApi(channelId)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<CategoryListData>
        get() = serializer

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("channel_id", channelId)
        }
    }
}

/**
 * [GetCategoryListApi] 的响应结果。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class CategoryListData @ApiResultType constructor(
    /**
     * 帖子分区列表
     */
    public val list: List<Category>
)
