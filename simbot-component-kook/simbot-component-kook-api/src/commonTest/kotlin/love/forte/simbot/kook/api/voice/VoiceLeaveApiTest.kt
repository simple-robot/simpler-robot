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

package love.forte.simbot.kook.api.voice

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
 * Tests for [VoiceLeaveApi]
 */
class VoiceLeaveApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1234567890"
        val api = VoiceLeaveApi.create(channelId)

        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/voice/leave", api.url.toString())
        assertNotNull(api.body)
    }

    @Test
    fun testRequestBodyStructure() = runTest {
        val channelId = "test_channel_id"
        val api = VoiceLeaveApi.create(channelId)

        var capturedRequestBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedRequestBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"code": 0, "message": "操作成功", "data": {}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        api.requestResult(client, "Bot test-token")

        assertNotNull(capturedRequestBody)
        val requestJson = json.parseToJsonElement(capturedRequestBody).jsonObject

        assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)
        assertEquals(1, requestJson.size) // Should only have channel_id
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        val successResponseJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {}
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        assertNotNull(apiResult.data)
    }

    @Test
    fun testErrorResponseDeserialization() {
        val errorResponseJson = """{
            "code": 40004,
            "message": "用户不在该语音频道中"
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), errorResponseJson)
        assertEquals(40004, apiResult.code)
        assertEquals("用户不在该语音频道中", apiResult.message)
    }

    @Test
    fun testApiFactory() {
        val channelId = "test_channel"
        
        val api1 = VoiceLeaveApi.create(channelId)
        val api2 = VoiceLeaveApi.create("different_channel")
        
        assertNotNull(api1)
        assertNotNull(api2)
        assertEquals(HttpMethod.Post, api1.method)
        assertEquals(HttpMethod.Post, api2.method)
        
        // Different channel IDs should create different instances
        assertTrue(api1 !== api2)
    }

    @Test
    fun testRealisticScenario() = runTest {
        val channelId = "voice_channel_123"
        val api = VoiceLeaveApi.create(channelId)

        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/api/v3/voice/leave", request.url.encodedPath)
            
            // Verify request body contains correct channel_id
            val bodyString = request.body.toByteArray().decodeToString()
            val bodyJson = json.parseToJsonElement(bodyString).jsonObject
            assertEquals(channelId, bodyJson["channel_id"]?.jsonPrimitive?.content)

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {}
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestResult(client, "Bot test-token")
        
        assertEquals(0, result.code)
        assertEquals("操作成功", result.message)
        assertEquals(true, result.isSuccess)
        
        // Verify Unit result deserialization
        val unitResult = result.parseDataOrThrow(deserializationStrategy = api.resultDeserializationStrategy)
        assertEquals(Unit, unitResult)
    }

    @Test
    fun testMultipleChannelIds() = runTest {
        val testChannelIds = listOf(
            "123456789",
            "voice_general",
            "music_room_456",
            ""
        )

        testChannelIds.forEach { channelId ->
            val api = VoiceLeaveApi.create(channelId)
            
            var capturedChannelId: String? = null
            val mockEngine = MockEngine { request ->
                val bodyString = request.body.toByteArray().decodeToString()
                val bodyJson = json.parseToJsonElement(bodyString).jsonObject
                capturedChannelId = bodyJson["channel_id"]?.jsonPrimitive?.content

                respond(
                    content = """{"code": 0, "message": "OK", "data": {}}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val client = HttpClient(mockEngine)
            api.requestResult(client, "Bot test-token")
            
            assertEquals(channelId, capturedChannelId, "Channel ID mismatch for input: '$channelId'")
        }
    }

    @Test
    fun testEmptyResponseData() {
        // Test various empty data scenarios
        val testCases = listOf(
            """{"code": 0, "message": "操作成功", "data": {}}""",
            """{"code": 0, "message": "操作成功", "data": []}""",
            """{"code": 0, "message": "操作成功", "data": null}"""
        )

        testCases.forEach { responseJson ->
            val apiResult = json.decodeFromString(ApiResult.serializer(), responseJson)
            assertEquals(0, apiResult.code)
        }
    }
}
