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
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.requestResult
import kotlin.test.*

/**
 * Tests for [GetThreadViewApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class GetThreadViewApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val api = GetThreadViewApi.create(channelId, threadId)

        // Test API properties
        assertEquals(HttpMethod.Get, api.method)
        val expectedUrl = "https://www.kookapp.cn/api/v3/thread/view?channel_id=$channelId&thread_id=$threadId"
        assertEquals(expectedUrl, api.url.toString())
    }

    @Test
    fun testUrlConstruction() {
        val channelId = "test_channel_id"
        val threadId = "test_thread_id"
        val api = GetThreadViewApi.create(channelId, threadId)
        val url = api.url.toString()

        // Verify URL contains correct Kook API base and path with query parameters
        assertTrue(url.contains("https://www.kookapp.cn/api/v3/thread/view"))
        assertTrue(url.contains("channel_id=test_channel_id"))
        assertTrue(url.contains("thread_id=test_thread_id"))
    }

    @Test
    fun testRequestExecution() = runTest {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val api = GetThreadViewApi.create(channelId, threadId)

        val mockEngine = MockEngine { request ->
            // Verify request URL and method
            assertEquals(HttpMethod.Get, request.method)
            assertTrue(request.url.toString().contains("thread/view"))
            assertTrue(request.url.toString().contains("channel_id=$channelId"))
            assertTrue(request.url.toString().contains("thread_id=$threadId"))
            
            respond(
                content = createSuccessResponseJson(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)

        // Make actual request
        val result = api.requestResult(client, "Bot test-token")
        assertNotNull(result)
    }

    @Test
    fun testSuccessfulResponseStructure() {
        // Test basic JSON structure without complex User deserialization
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        
        // Verify data structure exists without full deserialization
        val data = apiResult.data
        assertNotNull(data)
    }

    @Test
    fun testFactoryMethod() {
        val channelId = "test_channel_123"
        val threadId = "test_thread_456"
        val api = GetThreadViewApi.create(channelId, threadId)
        
        assertNotNull(api)
        val url = api.url.toString()
        assertTrue(url.contains("channel_id=$channelId"))
        assertTrue(url.contains("thread_id=$threadId"))
    }

    @Test
    fun testErrorResponseHandling() = runTest {
        val channelId = "invalid_channel"
        val threadId = "invalid_thread"
        val api = GetThreadViewApi.create(channelId, threadId)

        val mockEngine = MockEngine { request ->
            respond(
                content = """{"code": 40000, "message": "帖子不存在", "data": {}}""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)

        // Should handle error responses gracefully
        assertFailsWith<Exception> {
            api.requestResult(client, "Bot test-token")
        }
    }

    @Test
    fun testParameterValidation() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        
        // Should not throw with valid parameters
        assertNotNull(GetThreadViewApi.create(channelId, threadId))
        
        // Test with empty strings (should still create API, but might fail at request time)
        assertNotNull(GetThreadViewApi.create("", ""))
    }

    private fun createSuccessResponseJson(): String {
        return """{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": "xxxxxxxx",
    "status": 2,
    "title": "apitest",
    "cover": "https://xxx.com/xxxxx.png",
    "post_id": "78329392526262528",
    "medias": [
      {
        "type": 2,
        "src": "https://xxx.com/xxxxx.png",
        "title": ""
      }
    ],
    "preview_content": "机器人新闻播报：今日要闻 @全体成员  test@",
    "user": {
      "id": "xxxx",
      "identify_num": "xxx",
      "username": "test",
      "avatar": "https://xxx.com/xxxxx.png",
      "is_vip": false,
      "vip_avatar": "https://xxx.com/xxxxx.png",
      "nickname": "",
      "roles": []
    },
    "category": {
      "id": "1",
      "name": "10",
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
    "item_part": [],
    "latest_active_time": 11111111111,
    "create_time": 111111111111,
    "is_updated": false,
    "content_deleted": false,
    "content_deleted_type": 0,
    "collect_num": 0,
    "post_count": 1
  }
}"""
    }
}
