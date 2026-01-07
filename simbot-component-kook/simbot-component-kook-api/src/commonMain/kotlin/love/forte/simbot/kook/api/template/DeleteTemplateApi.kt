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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic

/**
 * [删除模板](https://developer.kookapp.cn/doc/http/template)
 *
 * POST /api/v3/template/delete
 *
 * @author ForteScarlet
 * @since 4.3.0
 */
@ExperimentalTemplateApi
public class DeleteTemplateApi private constructor(
    private val id: String,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("template", "delete")

        /**
         * 构建 [DeleteTemplateApi]
         *
         * @param id 要删除的模板ID，最长16
         */
        @JvmStatic
        public fun create(id: String): DeleteTemplateApi = DeleteTemplateApi(id)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any = Body(id)

    @Serializable
    private data class Body(
        val id: String,
    )
}
