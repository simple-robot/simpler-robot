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
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.requestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for [DeleteTemplateApi].
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalTemplateApi::class)
class DeleteTemplateApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val id = "template_001"
        val api = DeleteTemplateApi.create(id)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/template/delete", api.url.toString())
    }

    @Test
    fun testRequestBodyStructure() = runTest {
        val id = "template_001"
        val api = DeleteTemplateApi.create(id)

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
        assertEquals(1, requestJson.size, "Request should contain only the id field")
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // Based on delete template API response structure
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        // DeleteTemplateApi returns Unit, so we just verify the response structure
        val unitResult = apiResult.parseData(json, Unit.serializer())
        assertEquals(Unit, unitResult)
    }

    @Test
    fun testFactoryMethod() {
        val id = "template_001"

        // Test create method
        val api = DeleteTemplateApi.create(id)
        assertNotNull(api)
    }

    @Test
    fun testMultipleDeletions() {
        // Test creating APIs for multiple template deletions
        val ids = listOf("template_001", "template_002", "template_003")

        val apis = ids.map { DeleteTemplateApi.create(it) }

        assertEquals(3, apis.size)
        apis.forEach { api ->
            assertNotNull(api)
            assertEquals("https://www.kookapp.cn/api/v3/template/delete", api.url.toString())
        }
    }

    @Test
    fun testIdValidation() {
        // Test with various ID formats
        val validIds = listOf("template_001", "tpl_123", "abc123def", "1234567890123456")
        
        validIds.forEach { id ->
            val api = DeleteTemplateApi.create(id)
            assertNotNull(api, "API should be created for valid ID: $id")
        }
    }

    private fun createSuccessResponseJson(): String = """
        {
            "code": 0,
            "message": "操作成功",
            "data": {}
        }
    """.trimIndent()
}
