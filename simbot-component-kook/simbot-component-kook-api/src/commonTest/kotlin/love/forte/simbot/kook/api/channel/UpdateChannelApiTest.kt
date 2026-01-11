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
import kotlin.test.*

/**
 * Comprehensive tests for [UpdateChannelApi] focusing on API structure, serialization/deserialization,
 * and realistic scenarios based on official documentation.
 *
 * Tests include JSON serialization validation, API structure verification,
 * and response deserialization using patterns from official KOOK API documentation.
 *
 * @author ForteScarlet
 * @since 4.3.0
 */
class UpdateChannelApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1111111111111111"
        val api = UpdateChannelApi.create(channelId, name = "test-channel")

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/channel/update", api.url.toString())

        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
    }

    @Test
    fun testUrlConstruction() {
        val api = UpdateChannelApi.create("test_channel_id")
        val url = api.url.toString()

        // Verify URL contains correct Kook API base and path
        assertEquals("https://www.kookapp.cn/api/v3/channel/update", url)
    }

    @Test
    fun testRequestBodyStructureMinimal() = runTest {
        val channelId = "1111111111111111"
        val api = UpdateChannelApi.create(channelId)

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

        // Verify JSON structure matches expected serialization - only channel_id should be present
        assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)
        assertEquals(1, requestJson.size) // Only channel_id should be present
    }

    @Test
    fun testRequestBodyStructureComplete() = runTest {
        val channelId = "1111111111111111"
        val name = "updated-channel"
        val level = 5
        val parentId = "2222222222222222"
        val topic = "Updated channel topic"
        val slowMode = 5000
        val limitAmount = 50
        val voiceQuality = "2"
        val password = "secret"

        val api = UpdateChannelApi.create(
            channelId = channelId,
            name = name,
            level = level,
            parentId = parentId,
            topic = topic,
            slowMode = slowMode,
            limitAmount = limitAmount,
            voiceQuality = voiceQuality,
            password = password
        )

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
        assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)
        assertEquals(name, requestJson["name"]?.jsonPrimitive?.content)
        assertEquals(level, requestJson["level"]?.jsonPrimitive?.content?.toInt())
        assertEquals(parentId, requestJson["parent_id"]?.jsonPrimitive?.content)
        assertEquals(topic, requestJson["topic"]?.jsonPrimitive?.content)
        assertEquals(slowMode, requestJson["slow_mode"]?.jsonPrimitive?.content?.toInt())
        assertEquals(limitAmount, requestJson["limit_amount"]?.jsonPrimitive?.content?.toInt())
        assertEquals(voiceQuality, requestJson["voice_quality"]?.jsonPrimitive?.content)
        assertEquals(password, requestJson["password"]?.jsonPrimitive?.content)
        assertEquals(9, requestJson.size)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // Based on official documentation example
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val channelView = apiResult.parseData(json, ChannelView.serializer())
        assertNotNull(channelView)
        assertEquals("1111111111111111", channelView.id)
        assertEquals("q'q-!!", channelView.name)
        assertEquals("1111111111", channelView.userId)
        assertEquals("1111111111111111", channelView.guildId)
        assertEquals("mmm", channelView.topic)
        assertEquals(1, channelView.type)
        assertEquals(3, channelView.level)
        assertEquals(0, channelView.slowMode)
    }

    @Test
    fun testErrorResponseDeserialization() {
        // Test realistic error scenario - channel doesn't exist
        //language=json
        val errorResponseJson = """{
            "code": 40000,
            "message": "频道不存在"
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), errorResponseJson)

        assertNotNull(apiResult)
        assertEquals(40000, apiResult.code)
        assertEquals("频道不存在", apiResult.message)
    }

    @Test
    fun testApiFactory() {
        val channelId = "7480000000000000"

        // Test factory method
        val api = UpdateChannelApi.create(channelId)
        assertNotNull(api)

        // Verify the created API has the correct properties
        assertEquals(HttpMethod.Post, api.method)
        assertNotNull(api.body)
        assertNotNull(api.url)

        // Verify API path structure
        val url = api.url
        assertEquals("https://www.kookapp.cn/api/v3/channel/update", url.toString())
        assertEquals(listOf("api", "v3", "channel", "update"), url.segments)
    }

    @Test
    fun testBuilderPattern() {
        val channelId = "1111111111111111"

        val api = UpdateChannelApi.create(channelId) {
            name("test-channel")
            level(10)
            parentId("2222222222222222")
            topic("Test topic")
            slowModeValue(300000) // 5 minutes
            limitAmount(25)
            voiceQualityValue("3") // High quality
            password("secret")
        }

        assertNotNull(api)
        assertEquals(HttpMethod.Post, api.method)
    }

    @Test
    fun testBuilderBasics() {
        val channelId = "test-channel-id"
        val builder = UpdateChannelApi.builder(channelId)

        assertEquals(channelId, builder.channelId)

        // Test setting properties
        builder.name("test-name")
            .level(5)
            .parentId("parent-id")
            .topic("test topic")
            .slowModeValue(5000)
            .limitAmount(50)
            .voiceQualityValue("2")
            .password("secret")

        val api = builder.build()
        assertNotNull(api)
    }

    @Test
    fun testBuilderValueSettings() {
        val builder = UpdateChannelApi.builder("test-id")

        // Test setting values directly
        builder.slowModeValue = 300000
        assertEquals(300000, builder.slowModeValue)

        builder.voiceQualityValue = "3"
        assertEquals("3", builder.voiceQualityValue)

        // Test that builder works correctly
        val api = builder.build()
        assertNotNull(api)
    }

    @Test
    fun testRealisticScenario() = runTest {
        // Test updating a text channel with common properties
        val channelId = "3321010478582002"
        val newName = "updated-general"
        val newTopic = "Updated general discussion channel"
        val newSlowMode = 10000 // 10 seconds

        val api = UpdateChannelApi.create(
            channelId = channelId,
            name = newName,
            topic = newTopic,
            slowMode = newSlowMode
        )

        // Mock successful response from server
        val mockEngine = MockEngine {
            respond(
                content = createSuccessResponseJson(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestResult(client, "Bot realistic-test-token")

        // Verify successful result
        assertNotNull(result)
        assertEquals(0, result.code)
        assertNotNull(result.data)
    }

    @Test
    fun testParameterValidation() = runTest {
        // Test various parameter combinations
        val testCases = listOf(
            Triple("channel1", "new-name", null),
            Triple("channel2", null, "new topic"),
            Triple("channel3", null, null)
        )

        testCases.forEach { (channelId, name, topic) ->
            val api = UpdateChannelApi.create(
                channelId = channelId,
                name = name,
                topic = topic
            )

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
            api.requestResult(client, "Bot test-token")

            assertNotNull(capturedRequestBody)
            val requestJson = json.parseToJsonElement(capturedRequestBody).jsonObject

            assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)

            if (name != null) {
                assertEquals(name, requestJson["name"]?.jsonPrimitive?.content)
            } else {
                assertNull(requestJson["name"])
            }

            if (topic != null) {
                assertEquals(topic, requestJson["topic"]?.jsonPrimitive?.content)
            } else {
                assertNull(requestJson["topic"])
            }
        }
    }

    @Test
    fun testApiResultStructureWithWrapper() {
        // Test that the API properly handles wrapped response structure
        val responseJson = createSuccessResponseJson()
        val apiResult = json.decodeFromString(ApiResult.serializer(), responseJson)

        // Verify wrapper structure
        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        // Verify data is properly deserialized as ChannelView
        val channelData = apiResult.parseData(json, ChannelView.serializer())
        assertNotNull(channelData)
        assertIs<ChannelView>(channelData)
        assertEquals("1111111111111111", channelData.id)
    }

    private fun createSuccessResponseJson(): String {
        // Based on official documentation example with realistic data
        //language=json
        return """{
            "code": 0,
            "message": "操作成功",
            "data": {
                "id": "1111111111111111",
                "name": "q'q-!!",
                "user_id": "1111111111",
                "guild_id": "1111111111111111",
                "voice_quality": "2",
                "limit_amount": 0,
                "is_category": false,
                "parent_id": "1111111111111111",
                "level": 3,
                "slow_mode": 0,
                "topic": "mmm",
                "type": 1,
                "permission_overwrites": [
                    {
                        "role_id": 0,
                        "allow": 5152,
                        "deny": 8
                    }
                ],
                "permission_users": [],
                "permission_sync": 1,
                "has_password": false,
                "server_url": "rtc-new.kookapp.cn:39999/gateway/v1"
            }
        }"""
    }
}
