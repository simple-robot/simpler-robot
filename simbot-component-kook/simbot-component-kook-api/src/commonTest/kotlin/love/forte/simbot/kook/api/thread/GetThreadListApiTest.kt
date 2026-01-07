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
 * Tests for [GetThreadListApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class GetThreadListApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasicsMinimal() {
        val channelId = "1111111111111111"
        val api = GetThreadListApi.create(channelId)

        // Test API properties
        assertEquals(HttpMethod.Get, api.method)
        val expectedUrl = "https://www.kookapp.cn/api/v3/thread/list?channel_id=$channelId"
        assertEquals(expectedUrl, api.url.toString())
    }

    @Test
    fun testApiBasicsComplete() {
        val channelId = "1111111111111111"
        val categoryId = "2222222222222222"
        val sort = 1
        val pageSize = 20
        val time = 1234567890L
        val api = GetThreadListApi.create(channelId, categoryId, sort, pageSize, time)

        // Test API properties
        assertEquals(HttpMethod.Get, api.method)
        val url = api.url.toString()
        assertTrue(url.contains("https://www.kookapp.cn/api/v3/thread/list"))
        assertTrue(url.contains("channel_id=$channelId"))
        assertTrue(url.contains("category_id=$categoryId"))
        assertTrue(url.contains("sort=$sort"))
        assertTrue(url.contains("page_size=$pageSize"))
        assertTrue(url.contains("time=$time"))
    }

    @Test
    fun testUrlConstructionWithOptionalParams() {
        val channelId = "test_channel_id"
        
        // Test with no optional parameters
        val api1 = GetThreadListApi.create(channelId)
        val url1 = api1.url.toString()
        assertTrue(url1.contains("channel_id=test_channel_id"))
        assertFalse(url1.contains("category_id"))
        assertFalse(url1.contains("sort"))
        
        // Test with some optional parameters
        val api2 = GetThreadListApi.create(channelId, categoryId = "cat_123", sort = 2)
        val url2 = api2.url.toString()
        assertTrue(url2.contains("channel_id=test_channel_id"))
        assertTrue(url2.contains("category_id=cat_123"))
        assertTrue(url2.contains("sort=2"))
    }

    @Test
    fun testRequestExecution() = runTest {
        val channelId = "1111111111111111"
        val api = GetThreadListApi.create(channelId)

        val mockEngine = MockEngine { request ->
            // Verify request URL and method
            assertEquals(HttpMethod.Get, request.method)
            assertTrue(request.url.toString().contains("thread/list"))
            assertTrue(request.url.toString().contains("channel_id=$channelId"))
            
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
    fun testSortTypeEnum() {
        // Test ThreadSortType enum
        assertEquals(1, ThreadSortType.LATEST_REPLY.value)
        assertEquals(2, ThreadSortType.LATEST_CREATE.value)
    }

    @Test
    fun testFactoryMethods() {
        val channelId = "test_channel_123"
        
        // Test minimal create
        val api1 = GetThreadListApi.create(channelId)
        assertNotNull(api1)
        assertTrue(api1.url.toString().contains("channel_id=$channelId"))
        
        // Test create with all parameters
        val api2 = GetThreadListApi.create(channelId, "cat_id", 1, 30, 1234567890L)
        assertNotNull(api2)
        val url = api2.url.toString()
        assertTrue(url.contains("channel_id=$channelId"))
        assertTrue(url.contains("category_id=cat_id"))
        assertTrue(url.contains("sort=1"))
        assertTrue(url.contains("page_size=30"))
        assertTrue(url.contains("time=1234567890"))
    }

    @Test
    fun testErrorResponseHandling() = runTest {
        val channelId = "invalid_channel"
        val api = GetThreadListApi.create(channelId)

        val mockEngine = MockEngine { request ->
            respond(
                content = """{"code": 40000, "message": "频道不存在", "data": {}}""",
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
        
        // Should not throw with valid parameters
        assertNotNull(GetThreadListApi.create(channelId))
        assertNotNull(GetThreadListApi.create(channelId, "cat_id"))
        assertNotNull(GetThreadListApi.create(channelId, null, 1))
        assertNotNull(GetThreadListApi.create(channelId, null, null, 50))
        
        // Test with edge case values
        assertNotNull(GetThreadListApi.create(channelId, null, 0, -1, -1L))
    }

    private fun createSuccessResponseJson(): String {
        return """{
  "code": 0,
  "message": "操作成功",
  "data": {
    "items": [
      {
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
        "preview_content": "机器人新闻播报：今日要闻test@",
        "user": {},
        "category": {
          "id": "",
          "name": "",
          "allow": 0,
          "deny": 0
        },
        "tags": [],
        "latest_active_time": 11111111111,
        "create_time": 11111111111,
        "is_updated": false,
        "content_deleted": false,
        "content_deleted_type": 0,
        "collect_num": 0,
        "post_count": 0
      }
    ]
  }
}"""
    }
}
