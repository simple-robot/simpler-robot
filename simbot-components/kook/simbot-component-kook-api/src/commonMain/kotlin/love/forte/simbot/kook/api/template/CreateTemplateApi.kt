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
 * [创建模板](https://developer.kookapp.cn/doc/http/template)
 *
 * POST /api/v3/template/create
 *
 * @author ForteScarlet
 * @since 4.3.0
 */
@ExperimentalTemplateApi
public class CreateTemplateApi private constructor(
    private val title: String,
    private val content: String,
    private val msgtype: Int,
    private val type: Int = 0,
    private val testData: String = "",
    private val testChannel: String = "",
) : KookPostApi<CreateTemplateResult<Template>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("template", "create")

        /**
         * 构建 [CreateTemplateApi]
         *
         * @param title 模板标题，最长64
         * @param content 模板内容
         * @param msgtype 消息类型：1代表kmd消息，2代表通过json发卡片消息，3代表通过yaml发卡片消息
         * @param type 模板类型，目前固定为0，代表模板使用twig渲染
         * @param testData 测试数据，主要用于界面上的便利测试
         * @param testChannel 测试的频道，最长64，主要用于界面上的便利测试
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            title: String,
            content: String,
            msgtype: Int,
            type: Int = 0,
            testData: String = "",
            testChannel: String = ""
        ): CreateTemplateApi = CreateTemplateApi(title, content, msgtype, type, testData, testChannel)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<CreateTemplateResult<Template>>
        get() = CreateTemplateResult.serializer(SimpleTemplate.serializer())

    override fun createBody(): Any = Body(title, content, msgtype, type, testData, testChannel)

    @Serializable
    private data class Body(
        val title: String,
        val content: String,
        val msgtype: Int,
        val type: Int = 0,
        @SerialName("test_data")
        val testData: String = "",
        @SerialName("test_channel")
        val testChannel: String = "",
    )
}

// 返回值结构：{"code":0,"message":"操作成功","data":{"model":{"id":"85604912","content":"Hello {name}!","type":0,"status":0,"test_data":"","test_channel":"","title":"Test Template","msgtype":1}}}
// {"model":{"id":"85604912","content":"Hello {name}!","type":0,"status":0,"test_data":"","test_channel":"","title":"Test Template","msgtype":1}}}
// 这他妈又是什么粽子
// data 里面套了一层 model，真是醉了

@Serializable
@ConsistentCopyVisibility
@ExperimentalTemplateApi
public data class CreateTemplateResult<out T : Template> @ApiResultType internal constructor(
    val model: T
)
