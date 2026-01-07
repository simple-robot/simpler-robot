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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * Tests for [UpdateTemplateApi].
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalTemplateApi::class)
class UpdateTemplateApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val id = "template_001"
        val title = "Updated Template"
        val content = "Updated content"
        val msgtype = 1
        val api = UpdateTemplateApi.create(id, title, content, msgtype)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/template/update", api.url.toString())
    }

    @Test
    fun testRequestBodyStructurePartialUpdate() = runTest {
        val id = "template_001"
        val title = "Updated Title Only"
        val api = UpdateTemplateApi.create(id, title = title)

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
        assertEquals(id, requestJson["id"]?.jsonPrimitive?.content)
        assertEquals(title, requestJson["title"]?.jsonPrimitive?.content)
        // Optional fields should not be present in partial update
        assertFalse(requestJson.containsKey("content"))
        assertFalse(requestJson.containsKey("msgtype"))
        assertFalse(requestJson.containsKey("type"))
        assertFalse(requestJson.containsKey("test_data"))
        assertFalse(requestJson.containsKey("test_channel"))
    }

    @Test
    fun testRequestBodyStructureCompleteUpdate() = runTest {
        val id = "template_001"
        val title = "Completely Updated Template"
        val content = "Welcome to {{guild.name}}, {{user.name}}! Updated version."
        val msgtype = 2
        val type = 0
        val testData = "{\"test\": true}"
        val testChannel = "update_channel"
        val api = UpdateTemplateApi.create(id, title, content, msgtype, type, testData, testChannel)

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
        assertEquals(id, requestJson["id"]?.jsonPrimitive?.content)
        assertEquals(title, requestJson["title"]?.jsonPrimitive?.content)
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        assertEquals(msgtype, requestJson["msgtype"]?.jsonPrimitive?.content?.toInt())
        assertEquals(type, requestJson["type"]?.jsonPrimitive?.content?.toInt())
        assertEquals(testData, requestJson["test_data"]?.jsonPrimitive?.content)
        assertEquals(testChannel, requestJson["test_channel"]?.jsonPrimitive?.content)
        assertEquals(7, requestJson.size)
    }

    @Test
    fun testRequestBodyStructureContentOnly() = runTest {
        val id = "template_001"
        val content = "Only content updated"
        val api = UpdateTemplateApi.create(id, content = content)

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
        assertEquals(id, requestJson["id"]?.jsonPrimitive?.content)
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        // Other optional fields should not be present
        assertFalse(requestJson.containsKey("title"))
        assertFalse(requestJson.containsKey("msgtype"))
        assertEquals(2, requestJson.size)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // 基于模板 API 响应结构
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val template = apiResult.parseData(json, UpdateTemplateResult.serializer(SimpleTemplate.serializer())).model
        assertNotNull(template)
        assertEquals("template_001", template.id)
        assertEquals("Updated Template", template.title)
        assertEquals("Updated content", template.content)
        assertEquals(0, template.type)
        assertEquals(1, template.msgtype)
        assertEquals(0, template.status)
        assertEquals("{}", template.testData)
        assertEquals("", template.testChannel)
    }

    @Test
    fun testFactoryMethods() {
        val id = "template_001"
        val title = "Factory Test Template"
        val content = "Factory test content"
        val msgtype = 1

        // Test create method with all parameters
        val api1 = UpdateTemplateApi.create(id, title, content, msgtype, 0, "{}", "test")
        assertNotNull(api1)

        // Test create method with only ID and title
        val api2 = UpdateTemplateApi.create(id, title = title)
        assertNotNull(api2)

        // Test create method with only ID and content
        val api3 = UpdateTemplateApi.create(id, content = content)
        assertNotNull(api3)

        // Test create method with only ID and msgtype
        val api4 = UpdateTemplateApi.create(id, msgtype = msgtype)
        assertNotNull(api4)
    }

    private fun createSuccessResponseJson(): String = """
        {
            "code": 0,
            "message": "操作成功",
            "data": {
                "model": {
                    "id": "template_001",
                    "title": "Updated Template",
                    "content": "Updated content",
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
