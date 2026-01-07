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

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.requestResult
import love.forte.simbot.kook.objects.template.SimpleTemplate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * [CreateTemplateApi] 的测试。
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalTemplateApi::class)
class CreateTemplateApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val title = "Test Template"
        val content = "Hello {{user.name}}!"
        val msgtype = 1
        val api = CreateTemplateApi.create(title, content, msgtype)

        // 测试 API 属性
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/template/create", api.url.toString())
    }

    @Test
    fun testRequestBodyStructureMinimal() = runTest {
        val title = "Minimal Template"
        val content = "Simple content"
        val msgtype = 1
        val api = CreateTemplateApi.create(title, content, msgtype)

        // 使用 MockEngine 捕获实际请求体
        var capturedRequestBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedRequestBody = request.body.toByteArray().decodeToString()
            respond(
                content = createSuccessResponseJson(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)

        // 发送实际请求以捕获序列化后的请求体
        api.requestResult(client, "Bot test-token")

        // 验证捕获的 JSON 请求体结构
        assertNotNull(capturedRequestBody, "Request body should be captured")
        val requestJson = json.parseToJsonElement(capturedRequestBody).jsonObject

        // 验证 JSON 结构符合预期的序列化格式
        assertEquals(title, requestJson["title"]?.jsonPrimitive?.content)
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        assertEquals(msgtype, requestJson["msgtype"]?.jsonPrimitive?.content?.toInt())
        assertEquals(0, requestJson["type"]?.jsonPrimitive?.content?.toInt())
        assertEquals("", requestJson["test_data"]?.jsonPrimitive?.content)
        assertEquals("", requestJson["test_channel"]?.jsonPrimitive?.content)
    }

    @Test
    fun testRequestBodyStructureComplete() = runTest {
        val title = "Complete Template"
        val content = "Welcome to {{guild.name}}, {{user.name}}!"
        val msgtype = 2
        val type = 0
        val testData = "{\"user\": \"test\"}"
        val testChannel = "test_channel"
        val api = CreateTemplateApi.create(title, content, msgtype, type, testData, testChannel)

        // Capture actual request body using MockEngine
        var capturedRequestBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedRequestBody = request.body.toByteArray().decodeToString()
            respond(
                content = createSuccessResponseJson(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)

        // Make actual request to capture serialized body
        api.requestResult(client, "Bot test-token")

        // Validate the captured JSON body structure
        assertNotNull(capturedRequestBody, "Request body should be captured")
        val requestJson = json.parseToJsonElement(capturedRequestBody).jsonObject

        // Verify JSON structure matches expected serialization
        assertEquals(title, requestJson["title"]?.jsonPrimitive?.content)
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        assertEquals(msgtype, requestJson["msgtype"]?.jsonPrimitive?.content?.toInt())
        assertEquals(type, requestJson["type"]?.jsonPrimitive?.content?.toInt())
        assertEquals(testData, requestJson["test_data"]?.jsonPrimitive?.content)
        assertEquals(testChannel, requestJson["test_channel"]?.jsonPrimitive?.content)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // 基于模板 API 响应结构
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val template = apiResult.parseData(json, CreateTemplateResult.serializer(SimpleTemplate.serializer()))
        assertNotNull(template)
        assertNotNull(template.model)
        assertEquals("template_001", template.model.id)
        assertEquals("Welcome Template", template.model.title)
        assertEquals("Welcome to {{guild.name}}!", template.model.content)
        assertEquals(0, template.model.type)
        assertEquals(1, template.model.msgtype)
        assertEquals(0, template.model.status)
        assertEquals("{}", template.model.testData)
        assertEquals("", template.model.testChannel)
    }

    @Test
    fun testFactoryMethods() {
        val title = "Factory Test Template"
        val content = "Factory test content"
        val msgtype = 1

        // 测试带完整参数的 create 方法
        val api1 = CreateTemplateApi.create(title, content, msgtype, 0, "{}", "test_channel")
        assertNotNull(api1)

        // 测试带最少参数的 create 方法
        val api2 = CreateTemplateApi.create(title, content, msgtype)
        assertNotNull(api2)
    }

    private fun createSuccessResponseJson(): String = """
        {
            "code": 0,
            "message": "操作成功",
            "data": {
                "model": {
                    "id": "template_001",
                    "title": "Welcome Template",
                    "content": "Welcome to {{guild.name}}!",
                    "type": 0,
                    "msgtype": 1,
                    "status": 0,
                    "test_data": "{}",
                    "test_channel": ""
                }
            }
        }
    """.trimIndent()
}
