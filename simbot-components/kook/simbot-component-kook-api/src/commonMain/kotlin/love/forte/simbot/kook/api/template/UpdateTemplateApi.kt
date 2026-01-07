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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookPostApi
import love.forte.simbot.kook.objects.template.SimpleTemplate
import love.forte.simbot.kook.objects.template.Template
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [更新模板](https://developer.kookapp.cn/doc/http/template)
 *
 * POST /api/v3/template/update
 *
 * @author ForteScarlet
 * @since 4.3.0
 */
@ExperimentalTemplateApi
public class UpdateTemplateApi private constructor(
    private val id: String,
    private val title: String? = null,
    private val content: String? = null,
    private val msgtype: Int? = null,
    private val type: Int? = null,
    private val testData: String? = null,
    private val testChannel: String? = null,
) : KookPostApi<UpdateTemplateResult<Template>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("template", "update")

        /**
         * 构建 [UpdateTemplateApi]
         *
         * @param id 模板ID，最长16
         * @param title 模板标题，最长64，可选更新
         * @param content 模板内容，可选更新
         * @param msgtype 消息类型：1代表kmd消息，2代表通过json发卡片消息，3代表通过yaml发卡片消息，可选更新
         * @param type 模板类型，目前固定为0，代表模板使用twig渲染，可选更新
         * @param testData 测试数据，主要用于界面上的便利测试，可选更新
         * @param testChannel 测试的频道，最长64，主要用于界面上的便利测试，可选更新
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String? = null,
            content: String? = null,
            msgtype: Int? = null,
            type: Int? = null,
            testData: String? = null,
            testChannel: String? = null
        ): UpdateTemplateApi = UpdateTemplateApi(id, title, content, msgtype, type, testData, testChannel)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<UpdateTemplateResult<Template>>
        get() = UpdateTemplateResult.serializer(SimpleTemplate.serializer())

    override fun createBody(): Any = Body(id, title, content, msgtype, type, testData, testChannel)

    @Serializable
    private data class Body(
        val id: String,
        val title: String? = null,
        val content: String? = null,
        val msgtype: Int? = null,
        val type: Int? = null,
        @SerialName("test_data")
        val testData: String? = null,
        @SerialName("test_channel")
        val testChannel: String? = null,
    )
}

// 服了。


@Serializable
@ConsistentCopyVisibility
@ExperimentalTemplateApi
public data class UpdateTemplateResult<out T : Template> @ApiResultType internal constructor(
    val model: T
)
