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
import kotlinx.serialization.json.Json
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.objects.template.SimpleTemplate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * [GetTemplateListApi] 的测试。
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalTemplateApi::class)
class GetTemplateListApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val api = GetTemplateListApi.create()

        // 测试 API 属性
        assertEquals(HttpMethod.Get, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/template/list", api.url.toString())
    }

    @Test
    fun testApiWithPagination() {
        val api = GetTemplateListApi.create(page = 2, pageSize = 10)

        // 测试 API 属性
        assertEquals(HttpMethod.Get, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/template/list?page=2&page_size=10", api.url.toString())
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // 基于模板 API 响应结构
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(love.forte.simbot.kook.api.ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val templateList = apiResult.parseData(json, ListData.serializer(SimpleTemplate.serializer()))
        assertNotNull(templateList)

        // 验证模板列表结构
        assertEquals(2, templateList.items.size)

        val firstTemplate = templateList.items[0]
        assertEquals("template_001", firstTemplate.id)
        assertEquals("Welcome Template", firstTemplate.title)
        assertEquals("Welcome to {{guild.name}}!", firstTemplate.content)
        assertEquals(0, firstTemplate.type)
        assertEquals(2, firstTemplate.msgtype)
        assertEquals(0, firstTemplate.status)
        assertEquals("{}", firstTemplate.testData)
        assertEquals("", firstTemplate.testChannel)

        val secondTemplate = templateList.items[1]
        assertEquals("template_002", secondTemplate.id)
        assertEquals("Announcement Template", secondTemplate.title)
        assertEquals("**Important:** {{announcement}}", secondTemplate.content)
        assertEquals(0, secondTemplate.type)
        assertEquals(2, secondTemplate.msgtype)
        assertEquals(0, secondTemplate.status)
        assertEquals("{}", secondTemplate.testData)
        assertEquals("", secondTemplate.testChannel)

        // 验证元数据信息
        val meta = templateList.meta
        assertNotNull(meta, "Meta should not be null")
        assertEquals(1, meta.page)
        assertEquals(1, meta.pageTotal)
        assertEquals(20, meta.pageSize)
        assertEquals(2, meta.total)
    }

    private fun createSuccessResponseJson(): String = """
        {
            "code": 0,
            "message": "操作成功",
            "data": {
                "items": [
                    {
                        "id": "template_001",
                        "title": "Welcome Template",
                        "content": "Welcome to {{guild.name}}!",
                        "type": 0,
                        "status": 0,
                        "test_channel": "",
                        "msgtype": 2,
                        "test_data": "{}"
                    },
                    {
                        "id": "template_002",
                        "title": "Announcement Template",
                        "content": "**Important:** {{announcement}}",
                        "type": 0,
                        "status": 0,
                        "test_channel": "",
                        "msgtype": 2,
                        "test_data": "{}"
                    }
                ],
                "meta": {
                    "page": 1,
                    "page_total": 1,
                    "page_size": 20,
                    "total": 2
                }
            }
        }
    """.trimIndent()

    private fun createEmptyResponseJson(): String = """
        {
            "code": 0,
            "message": "操作成功",
            "data": {
                "items": [],
                "meta": {
                    "page": 1,
                    "page_total": 0,
                    "page_size": 20,
                    "total": 0
                }
            }
        }
    """.trimIndent()
}
