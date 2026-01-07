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
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.requestResult
import kotlin.test.*

/**
 * Tests for [ReplyThreadApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class ReplyThreadApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val content = "Test reply content"
        val api = ReplyThreadApi.create(channelId, threadId, content)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/thread/reply", api.url.toString())

        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
        assertEquals(channelId, body.channelId)
        assertEquals(threadId, body.threadId)
        assertEquals(content, body.content)
        assertNull(body.replyId)
    }

    @Test
    fun testApiWithReplyId() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val content = "Test reply content"
        val replyId = "3333333333333333"
        val api = ReplyThreadApi.create(channelId, threadId, content, replyId)

        // Test body content
        val body = api.body
        assertEquals(channelId, body.channelId)
        assertEquals(threadId, body.threadId)
        assertEquals(content, body.content)
        assertEquals(replyId, body.replyId)
    }

    @Test
    fun testRequestBodyStructureWithoutReplyId() = runTest {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val content = "Test reply content"
        val api = ReplyThreadApi.create(channelId, threadId, content)

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
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        // reply_id should be null, but null will not be written into JSON.
        // assertTrue(requestJson.containsKey("reply_id"))
        assertNull(requestJson["reply_id"]?.jsonPrimitive)
    }

    @Test
    fun testRequestBodyStructureWithReplyId() = runTest {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val content = "Test reply content"
        val replyId = "3333333333333333"
        val api = ReplyThreadApi.create(channelId, threadId, content, replyId)

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
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        assertEquals(replyId, requestJson["reply_id"]?.jsonPrimitive?.content)
        assertEquals(4, requestJson.size)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // Based on official documentation example
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val post = apiResult.parseData(json, Post.serializer())
        assertNotNull(post)
        assertEquals("xxxxxx", post.id)
        assertEquals("0", post.replyId)
        assertEquals("xxxxx", post.threadId)
        assertFalse(post.isUpdated ?: true)
        assertFalse(post.mentionAll)
        assertFalse(post.mentionHere)
        assertEquals("", post.content)
        assertEquals(0, post.mention.size)
        assertEquals(0, post.mentionPart.size)
        assertEquals(0, post.mentionRolePart.size)
        assertEquals(0, post.channelPart.size)
        assertEquals(0, post.itemPart.size)
    }

    @Test
    fun testFactoryMethods() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val content = "Factory test content"

        // Test direct create method without replyId
        val api1 = ReplyThreadApi.create(channelId, threadId, content)
        assertNotNull(api1)
        assertEquals(channelId, api1.body.channelId)
        assertNull(api1.body.replyId)

        // Test direct create method with replyId
        val replyId = "3333333333333333"
        val api2 = ReplyThreadApi.create(channelId, threadId, content, replyId)
        assertNotNull(api2)
        assertEquals(channelId, api2.body.channelId)
        assertEquals(replyId, api2.body.replyId)

        // Test create with Body
        val body = ReplyThreadApi.Body(channelId, threadId, content, replyId)
        val api3 = ReplyThreadApi.create(body)
        assertNotNull(api3)
        assertEquals(channelId, api3.body.channelId)
        assertEquals(replyId, api3.body.replyId)
    }

    @Test
    fun testBodyEqualsAndHashCode() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val content = "Test content"
        
        val body1 = ReplyThreadApi.Body(channelId, threadId, content, null)
        val body2 = ReplyThreadApi.Body(channelId, threadId, content, null)
        val body3 = ReplyThreadApi.Body(channelId, threadId, "Different content", null)
        val body4 = ReplyThreadApi.Body(channelId, threadId, content, "replyId")

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
  "data": {
    "id": "xxxxxx",
    "reply_id": "0",
    "belong_to_post_id": "1",
    "thread_id": "xxxxx",
    "is_updated": false,
    "mention": [],
    "mention_all": false,
    "mention_here": false,
    "content": "",
    "mention_part": [],
    "mention_role_part": [],
    "channel_part": [],
    "item_part": []
  }
}"""
    }
}
