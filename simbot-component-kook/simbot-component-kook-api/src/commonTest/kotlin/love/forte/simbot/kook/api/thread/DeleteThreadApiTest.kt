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

package love.forte.simbot.kook.api.thread

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.requestResult
import kotlin.test.*

/**
 * Tests for [DeleteThreadApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class DeleteThreadApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasicsForThread() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val api = DeleteThreadApi.createForThread(channelId, threadId)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/thread/delete", api.url.toString())

        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
        assertEquals(channelId, body.channelId)
        assertEquals(threadId, body.threadId)
        assertNull(body.postId)
    }

    @Test
    fun testApiBasicsForPost() {
        val channelId = "1111111111111111"
        val postId = "3333333333333333"
        val api = DeleteThreadApi.createForPost(channelId, postId)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/thread/delete", api.url.toString())

        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
        assertEquals(channelId, body.channelId)
        assertNull(body.threadId)
        assertEquals(postId, body.postId)
    }

    @Test
    fun testApiWithBothIds() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val postId = "3333333333333333"
        val api = DeleteThreadApi.create(channelId, threadId, postId)

        // Test body content - should have both IDs
        val body = api.body
        assertEquals(channelId, body.channelId)
        assertEquals(threadId, body.threadId)
        assertEquals(postId, body.postId)
    }

    @Test
    fun testRequestBodyStructureForThread() = runTest {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val api = DeleteThreadApi.createForThread(channelId, threadId)

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
        assertEquals(threadId, requestJson["thread_id"]?.jsonPrimitive?.content)
        assertNull(requestJson["post_id"]?.jsonPrimitive)
    }

    @Test
    fun testRequestBodyStructureForPost() = runTest {
        val channelId = "1111111111111111"
        val postId = "3333333333333333"
        val api = DeleteThreadApi.createForPost(channelId, postId)

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
        assertEquals(postId, requestJson["post_id"]?.jsonPrimitive?.content)
        // thread_id should be null, but null will not be written into JSON.
        // assertTrue(requestJson.containsKey("thread_id"))
        assertNull(requestJson["thread_id"]?.jsonPrimitive)
    }

    @Test
    fun testRequestBodyStructureForBoth() = runTest {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val postId = "3333333333333333"
        val api = DeleteThreadApi.create(channelId, threadId, postId)

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
        assertEquals(threadId, requestJson["thread_id"]?.jsonPrimitive?.content)
        assertEquals(postId, requestJson["post_id"]?.jsonPrimitive?.content)
        assertEquals(3, requestJson.size)
    }

    @Test
    fun testFactoryMethods() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val postId = "3333333333333333"

        // Test createForThread
        val api1 = DeleteThreadApi.createForThread(channelId, threadId)
        assertNotNull(api1)
        assertEquals(channelId, api1.body.channelId)
        assertEquals(threadId, api1.body.threadId)
        assertNull(api1.body.postId)

        // Test createForPost
        val api2 = DeleteThreadApi.createForPost(channelId, postId)
        assertNotNull(api2)
        assertEquals(channelId, api2.body.channelId)
        assertNull(api2.body.threadId)
        assertEquals(postId, api2.body.postId)

        // Test general create
        val api3 = DeleteThreadApi.create(channelId, threadId, postId)
        assertNotNull(api3)
        assertEquals(channelId, api3.body.channelId)
        assertEquals(threadId, api3.body.threadId)
        assertEquals(postId, api3.body.postId)

        // Test create with Body
        val body = DeleteThreadApi.Body(channelId, threadId, postId)
        val api4 = DeleteThreadApi.create(body)
        assertNotNull(api4)
        assertEquals(channelId, api4.body.channelId)
        assertEquals(threadId, api4.body.threadId)
        assertEquals(postId, api4.body.postId)
    }

    @Test
    fun testParameterValidation() {
        val channelId = "1111111111111111"

        // Should fail when both threadId and postId are null
        assertFailsWith<IllegalArgumentException> {
            DeleteThreadApi.create(channelId, null, null)
        }
    }

    @Test
    fun testBodyEqualsAndHashCode() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val postId = "3333333333333333"
        
        val body1 = DeleteThreadApi.Body(channelId, threadId, null)
        val body2 = DeleteThreadApi.Body(channelId, threadId, null)
        val body3 = DeleteThreadApi.Body(channelId, null, postId)
        val body4 = DeleteThreadApi.Body(channelId, threadId, postId)

        assertEquals(body1, body2)
        assertEquals(body1.hashCode(), body2.hashCode())
        assertNotEquals(body1, body3)
        assertNotEquals(body1.hashCode(), body3.hashCode())
        assertNotEquals(body1, body4)
        assertNotEquals(body1.hashCode(), body4.hashCode())
    }

    private fun createSuccessResponseJson(): String {
        return """{
  "code": 0,
  "message": "操作成功",
  "data": {}
}"""
    }
}
