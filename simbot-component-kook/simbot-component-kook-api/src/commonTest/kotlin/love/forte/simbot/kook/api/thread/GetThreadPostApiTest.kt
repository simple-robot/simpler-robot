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
 * Tests for [GetThreadPostApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class GetThreadPostApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasicsMinimal() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val order = "asc"
        val page = "1"
        val api = GetThreadPostApi.create(channelId, threadId, order, page)

        // Test API properties
        assertEquals(HttpMethod.Get, api.method)
        val url = api.url.toString()
        assertTrue(url.contains("https://www.kookapp.cn/api/v3/thread/post"))
        assertTrue(url.contains("channel_id=$channelId"))
        assertTrue(url.contains("thread_id=$threadId"))
        assertTrue(url.contains("order=$order"))
        assertTrue(url.contains("page=$page"))
    }

    @Test
    fun testApiBasicsComplete() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val order = "desc"
        val page = "2"
        val postId = "3333333333333333"
        val time = "1234567890"
        val pageSize = "20"
        val api = GetThreadPostApi.create(channelId, threadId, order, page, postId, time, pageSize)

        // Test API properties
        assertEquals(HttpMethod.Get, api.method)
        val url = api.url.toString()
        assertTrue(url.contains("https://www.kookapp.cn/api/v3/thread/post"))
        assertTrue(url.contains("channel_id=$channelId"))
        assertTrue(url.contains("thread_id=$threadId"))
        assertTrue(url.contains("order=$order"))
        assertTrue(url.contains("page=$page"))
        assertTrue(url.contains("post_id=$postId"))
        assertTrue(url.contains("time=$time"))
        assertTrue(url.contains("page_size=$pageSize"))
    }

    @Test
    fun testUrlConstructionWithOptionalParams() {
        val channelId = "test_channel_id"
        val threadId = "test_thread_id"
        
        // Test with minimal parameters
        val api1 = GetThreadPostApi.create(channelId, threadId, "asc", "1")
        val url1 = api1.url.toString()
        assertTrue(url1.contains("channel_id=test_channel_id"))
        assertTrue(url1.contains("thread_id=test_thread_id"))
        assertTrue(url1.contains("order=asc"))
        assertTrue(url1.contains("page=1"))
        assertFalse(url1.contains("post_id"))
        assertFalse(url1.contains("time"))
        assertFalse(url1.contains("page_size"))
        
        // Test with some optional parameters
        val api2 = GetThreadPostApi.create(channelId, threadId, "desc", "2", "post_123", "1234567890", null)
        val url2 = api2.url.toString()
        assertTrue(url2.contains("channel_id=test_channel_id"))
        assertTrue(url2.contains("thread_id=test_thread_id"))
        assertTrue(url2.contains("order=desc"))
        assertTrue(url2.contains("page=2"))
        assertTrue(url2.contains("post_id=post_123"))
        assertTrue(url2.contains("time=1234567890"))
        assertFalse(url2.contains("page_size"))
    }

    @Test
    fun testRequestExecution() = runTest {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        val api = GetThreadPostApi.create(channelId, threadId, "asc", "1")

        val mockEngine = MockEngine { request ->
            // Verify request URL and method
            assertEquals(HttpMethod.Get, request.method)
            assertTrue(request.url.toString().contains("thread/post"))
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
        
        // Verify data structure exists
        val data = apiResult.data
        assertNotNull(data)
    }

    @Test
    fun testPostOrderTypeEnum() {
        // Test PostOrderType enum
        assertEquals("asc", PostOrderType.ASC.value)
        assertEquals("desc", PostOrderType.DESC.value)
    }

    @Test
    fun testFactoryMethods() {
        val channelId = "test_channel_123"
        val threadId = "test_thread_456"
        val order = "asc"
        val page = "1"
        
        // Test minimal create
        val api1 = GetThreadPostApi.create(channelId, threadId, order, page)
        assertNotNull(api1)
        val url1 = api1.url.toString()
        assertTrue(url1.contains("channel_id=$channelId"))
        assertTrue(url1.contains("thread_id=$threadId"))
        assertTrue(url1.contains("order=$order"))
        assertTrue(url1.contains("page=$page"))
        
        // Test create with all parameters
        val api2 = GetThreadPostApi.create(channelId, threadId, "desc", "2", "post_id", "time", "10")
        assertNotNull(api2)
        val url2 = api2.url.toString()
        assertTrue(url2.contains("channel_id=$channelId"))
        assertTrue(url2.contains("thread_id=$threadId"))
        assertTrue(url2.contains("order=desc"))
        assertTrue(url2.contains("page=2"))
        assertTrue(url2.contains("post_id=post_id"))
        assertTrue(url2.contains("time=time"))
        assertTrue(url2.contains("page_size=10"))
    }

    @Test
    fun testErrorResponseHandling() = runTest {
        val channelId = "invalid_channel"
        val threadId = "invalid_thread"
        val api = GetThreadPostApi.create(channelId, threadId, "asc", "1")

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
        assertNotNull(GetThreadPostApi.create(channelId, threadId, "asc", "1"))
        assertNotNull(GetThreadPostApi.create(channelId, threadId, "desc", "10"))
        
        // Test with optional parameters
        assertNotNull(GetThreadPostApi.create(channelId, threadId, "asc", "1", "post_id"))
        assertNotNull(GetThreadPostApi.create(channelId, threadId, "desc", "1", null, "time"))
        assertNotNull(GetThreadPostApi.create(channelId, threadId, "asc", "1", null, null, "20"))
        
        // Test with edge case values
        assertNotNull(GetThreadPostApi.create("", "", "", ""))
    }

    @Test
    fun testOrderValues() {
        val channelId = "1111111111111111"
        val threadId = "2222222222222222"
        
        // Test with valid order values
        val ascApi = GetThreadPostApi.create(channelId, threadId, "asc", "1")
        assertTrue(ascApi.url.toString().contains("order=asc"))
        
        val descApi = GetThreadPostApi.create(channelId, threadId, "desc", "1")
        assertTrue(descApi.url.toString().contains("order=desc"))
        
        // Test with enum values
        val enumAscApi = GetThreadPostApi.create(channelId, threadId, PostOrderType.ASC.value, "1")
        assertTrue(enumAscApi.url.toString().contains("order=asc"))
        
        val enumDescApi = GetThreadPostApi.create(channelId, threadId, PostOrderType.DESC.value, "1")
        assertTrue(enumDescApi.url.toString().contains("order=desc"))
    }

    private fun createSuccessResponseJson(): String {
        return """{
  "code": 0,
  "message": "操作成功",
  "data": {
    "meta": {
      "total": 3,
      "page": 1,
      "page_size": 10,
      "page_total": 1
    },
    "items": [
      {
        "id": "xxxx",
        "reply_id": "0",
        "thread_id": "xxxxx",
        "is_updated": false,
        "mention": [],
        "mention_all": false,
        "mention_here": false,
        "content": "",
        "mention_part": [],
        "mention_role_part": [],
        "channel_part": [],
        "item_part": [],
        "belong_to_post_id": "0",
        "create_time": 1111111111,
        "user": {},
        "replies": []
      }
    ]
  }
}"""
    }
}
