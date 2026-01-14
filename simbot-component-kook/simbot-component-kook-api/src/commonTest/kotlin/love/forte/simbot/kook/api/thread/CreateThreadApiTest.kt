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
 * Tests for [CreateThreadApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class CreateThreadApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1111111111111111"
        val guildId = "2222222222222222"
        val title = "Test Thread"
        val content = "Test content"
        val api = CreateThreadApi.create(channelId, guildId, title, content)

        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/thread/create", api.url.toString())

        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
        assertEquals(channelId, body.channelId)
        assertEquals(guildId, body.guildId)
        assertEquals(title, body.title)
        assertEquals(content, body.content)
    }

    @Test
    fun testRequestBodyStructureMinimal() = runTest {
        val channelId = "1111111111111111"
        val guildId = "2222222222222222"
        val title = "Test Thread"
        val content = "Test content"
        val api = CreateThreadApi.create(channelId, guildId, title, content)

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
        assertEquals(guildId, requestJson["guild_id"]?.jsonPrimitive?.content)
        assertEquals(title, requestJson["title"]?.jsonPrimitive?.content)
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        // Optional fields should not be present in minimal case
        assertFalse(requestJson.containsKey("category_id"))
        assertFalse(requestJson.containsKey("cover"))
    }

    @Test
    fun testRequestBodyStructureComplete() = runTest {
        val channelId = "1111111111111111"
        val guildId = "2222222222222222"
        val title = "Complete Test Thread"
        val content = "Complete test content"
        val categoryId = "3333333333333333"
        val cover = "https://example.com/cover.png"

        val body = CreateThreadApi.builder(channelId, guildId, title, content)
            .categoryId(categoryId)
            .cover(cover)
            .build()

        val api = CreateThreadApi.create(body)

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
        assertEquals(guildId, requestJson["guild_id"]?.jsonPrimitive?.content)
        assertEquals(title, requestJson["title"]?.jsonPrimitive?.content)
        assertEquals(content, requestJson["content"]?.jsonPrimitive?.content)
        assertEquals(categoryId, requestJson["category_id"]?.jsonPrimitive?.content)
        assertEquals(cover, requestJson["cover"]?.jsonPrimitive?.content)
        assertEquals(6, requestJson.size)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        // Based on official documentation example
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val threadView = apiResult.parseData(json, ThreadView.serializer())
        assertNotNull(threadView)
        assertEquals("111111", threadView.id)
        assertEquals(2, threadView.status)
        assertEquals("apitest", threadView.title)
        assertEquals("https://xxx.com/xxxxx.png", threadView.cover)
        assertEquals("xxxxxx", threadView.postId)
        assertTrue(threadView.mentionAll)
        assertFalse(threadView.mentionHere)
        assertEquals("机器人新闻播报：今日要闻 @全体成员  test@", threadView.previewContent)

        // Test media
        assertEquals(1, threadView.medias.size)
        val media = threadView.medias[0]
        assertEquals(2, media.type)
        assertEquals("https://xxx.com/xxxxx.png", media.src)
        assertEquals("", media.title)

        // Test category
        assertEquals("xxx", threadView.category.id)
        assertEquals("xxx", threadView.category.name)
        assertEquals(0, threadView.category.allow)
        assertEquals(0, threadView.category.deny)
    }

    @Test
    fun testBuilderPattern() {
        val channelId = "1111111111111111"
        val guildId = "2222222222222222"
        val title = "Builder Test"
        val content = "Builder content"
        val categoryId = "3333333333333333"
        val cover = "https://example.com/cover.jpg"

        val body = CreateThreadApi.builder(channelId, guildId, title, content)
            .categoryId(categoryId)
            .cover(cover)
            .build()

        CreateThreadApi.create(body)
        assertEquals(channelId, body.channelId)
        assertEquals(guildId, body.guildId)
        assertEquals(title, body.title)
        assertEquals(content, body.content)
        assertEquals(categoryId, body.categoryId)
        assertEquals(cover, body.cover)
    }

    @Test
    fun testFactoryMethods() {
        val channelId = "1111111111111111"
        val guildId = "2222222222222222"
        val title = "Factory Test"
        val content = "Factory content"

        // Test direct create method
        val api1 = CreateThreadApi.create(channelId, guildId, title, content)
        assertNotNull(api1)
        assertEquals(channelId, api1.body.channelId)

        // Test create with Body
        val body = CreateThreadApi.Body(channelId, guildId, title, content)
        val api2 = CreateThreadApi.create(body)
        assertNotNull(api2)
        assertEquals(channelId, api2.body.channelId)
    }

    @Test
    fun testBodyEqualsAndHashCode() {
        val channelId = "1111111111111111"
        val guildId = "2222222222222222"
        val title = "Test"
        val content = "Content"

        val body1 = CreateThreadApi.Body(channelId, guildId, title, content)
        val body2 = CreateThreadApi.Body(channelId, guildId, title, content)
        val body3 = CreateThreadApi.Body(channelId, guildId, title, "Different Content")

        assertEquals(body1, body2)
        assertEquals(body1.hashCode(), body2.hashCode())
        assertNotEquals(body1, body3)
        assertNotEquals(body1.hashCode(), body3.hashCode())
    }

    private fun createSuccessResponseJson(): String {
        return """{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": "111111",
    "status": 2,
    "title": "apitest",
    "cover": "https://xxx.com/xxxxx.png",
    "post_id": "xxxxxx",
    "medias": [
      {
        "type": 2,
        "src": "https://xxx.com/xxxxx.png",
        "title": ""
      }
    ],
    "preview_content": "机器人新闻播报：今日要闻 @全体成员  test@",
    "user": {
      "id": "xxxxx",
      "identify_num": "xxxx",
      "username": "test",
      "avatar": "https://xxx.com/xxxxx.png",
      "is_vip": false,
      "vip_avatar": "https://xxx.com/xxxxx.png",
      "nickname": "",
      "roles": [10219]
    },
    "category": {
      "id": "xxx",
      "name": "xxx",
      "allow": 0,
      "deny": 0
    },
    "tags": [],
    "content": "",
    "mention": [],
    "mention_all": true,
    "mention_here": false,
    "mention_part": [],
    "mention_role_part": [],
    "channel_part": [],
    "item_part": []
  }
}"""
    }
}
