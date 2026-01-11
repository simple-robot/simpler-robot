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

package love.forte.simbot.kook.api.channel

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Comprehensive tests for [ChannelKickoutApi] focusing on API structure, serialization/deserialization,
 * and realistic scenarios based on official documentation.
 *
 * Tests include JSON serialization validation, API structure verification,
 * and response deserialization using patterns from official KOOK API documentation.
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
class ChannelKickoutApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "3321010478582002"
        val userId = "1700000"
        val api = ChannelKickoutApi.create(channelId, userId)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/channel/kickout", api.url.toString())

        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
    }

    @Test
    fun testUrlConstruction() {
        val api = ChannelKickoutApi.create("test_channel", "test_user")
        val url = api.url.toString()

        // Verify URL contains correct Kook API base and path
        assertEquals("https://www.kookapp.cn/api/v3/channel/kickout", url)
    }

    @Test
    fun testRequestBodyStructure() = runTest {
        val channelId = "3321010478582002"
        val userId = "1700000"
        val api = ChannelKickoutApi.create(channelId, userId)

        // Capture actual request body using MockEngine
        var capturedRequestBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedRequestBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"code": 0, "message": "操作成功", "data": []}""",
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
        assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)
        assertEquals(userId, requestJson["user_id"]?.jsonPrimitive?.content)
        assertEquals(2, requestJson.size)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // Based on official documentation example
        //language=json
        val successResponseJson = """{
            "code": 0,
            "message": "操作成功",
            "data": []
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        assertNotNull(apiResult.data)
    }

    @Test
    fun testErrorResponseDeserialization() {
        // Test realistic error scenario - channel doesn't exist or not a voice channel
        //language=json
        val errorResponseJson = """{
            "code": 40001,
            "message": "频道不存在或不是语音频道"
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), errorResponseJson)

        assertNotNull(apiResult)
        assertEquals(40001, apiResult.code)
        assertEquals("频道不存在或不是语音频道", apiResult.message)
    }

    @Test
    fun testApiFactory() {
        val channelId = "7480000000000000"
        val userId = "2418000000"

        // Test factory method
        val api = ChannelKickoutApi.create(channelId, userId)
        assertNotNull(api)

        // Verify the created API has the correct properties
        assertEquals(HttpMethod.Post, api.method)
        assertNotNull(api.body)
        assertNotNull(api.url)

        // Verify API path structure
        val url = api.url
        assertEquals("https://www.kookapp.cn/api/v3/channel/kickout", url.toString())
        assertEquals(listOf("api", "v3", "channel", "kickout"), url.segments)
    }

    @Test
    fun testMultipleApiInstances() {
        val api1 = ChannelKickoutApi.create("3321010478582001", "1700001")
        val api2 = ChannelKickoutApi.create("3321010478582002", "1700002")

        // Verify that different instances have different bodies
        val body1 = api1.body
        val body2 = api2.body

        assertNotNull(body1)
        assertNotNull(body2)

        // Bodies should be different objects (different parameters)
        assertTrue(body1 !== body2, "Different API instances should have different body objects")

        // Verify they have different string representations
        val string1 = body1.toString()
        val string2 = body2.toString()
        assertTrue(string1 != string2, "Different instances should have different string representations")
    }

    @Test
    fun testRealisticScenario() = runTest {
        // Test with realistic IDs from documentation
        val voiceChannelId = "3321010478582002"  // Voice channel from docs
        val userId = "1700000"  // User ID from docs

        val api = ChannelKickoutApi.create(voiceChannelId, userId)

        // Verify API structure
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/channel/kickout", api.url.toString())

        // Capture actual request body using MockEngine
        var capturedRequestBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedRequestBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"code": 0, "message": "操作成功", "data": []}""",
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
        
        // Verify JSON structure matches expected serialization for realistic scenario
        assertEquals(voiceChannelId, requestJson["channel_id"]?.jsonPrimitive?.content)
        assertEquals(userId, requestJson["user_id"]?.jsonPrimitive?.content)
        assertEquals(2, requestJson.size)
    }

    @Test
    fun testParameterValidation() = runTest {
        // Test with various parameter combinations
        val testCases = listOf(
            "7480000000000000" to "2418000000",  // From documentation
            "3321010478582002" to "1700000",     // Voice channel example
            "test_channel_123" to "test_user_456"  // Generic test case
        )

        testCases.forEach { (channelId, userId) ->
            val api = ChannelKickoutApi.create(channelId, userId)

            // Verify each API instance is properly constructed
            assertEquals(HttpMethod.Post, api.method)
            assertNotNull(api.body)
            assertNotNull(api.url)

            // Capture actual request body using MockEngine for each test case
            var capturedRequestBody: String? = null
            val mockEngine = MockEngine { request ->
                capturedRequestBody = request.body.toByteArray().decodeToString()
                respond(
                    content = """{"code": 0, "message": "操作成功", "data": []}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val client = HttpClient(mockEngine)
            
            // Make actual request to capture serialized body
            api.requestResult(client, "Bot test-token")
            
            // Validate the captured JSON body structure for each parameter combination
            assertNotNull(capturedRequestBody, "Request body should be captured for channelId=$channelId, userId=$userId")
            val requestJson = json.parseToJsonElement(capturedRequestBody).jsonObject
            
            // Verify JSON structure matches expected serialization
            assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)
            assertEquals(userId, requestJson["user_id"]?.jsonPrimitive?.content)
            assertEquals(2, requestJson.size)
        }
    }

    @Test
    fun testApiResultStructureWithWrapper() {
        // Test that API result properly handles KOOK's wrapper structure

        //language=json
        val wrappedResponseJson = """{
            "code": 0,
            "message": "操作成功",
            "data": []
        }"""

        val result = json.decodeFromString(ApiResult.serializer(), wrappedResponseJson)

        // Verify wrapper structure is properly handled
        assertNotNull(result)
        assertEquals(0, result.code)
        assertEquals("操作成功", result.message)
        assertNotNull(result.data)
    }
}
