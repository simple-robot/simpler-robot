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

package love.forte.simbot.kook.api.category

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
 * Tests for [GetCategoryListApi].
 *
 * @author Forte
 * @since 4.3.0
 */
class GetCategoryListApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1111111111111111"
        val api = GetCategoryListApi.create(channelId)

        // Test API properties
        assertEquals(HttpMethod.Get, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/category/list?channel_id=$channelId", api.url.toString())
    }

    @Test
    fun testUrlConstruction() {
        val channelId = "test_channel_id"
        val api = GetCategoryListApi.create(channelId)
        val url = api.url.toString()

        // Verify URL contains correct Kook API base and path with query parameter
        assertEquals("https://www.kookapp.cn/api/v3/category/list?channel_id=test_channel_id", url)
    }

    @Test
    fun testRequestExecution() = runTest {
        val channelId = "1111111111111111"
        val api = GetCategoryListApi.create(channelId)

        val mockEngine = MockEngine { request ->
            // Verify request URL and method
            assertEquals(HttpMethod.Get, request.method)
            assertTrue(request.url.toString().contains("category/list"))
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
    fun testSuccessfulResponseDeserialization() {
        // Based on official documentation example
        val successResponseJson = createSuccessResponseJson()

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)

        assertNotNull(apiResult)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)

        val categoryListData = apiResult.parseData(json, CategoryListData.serializer())
        assertNotNull(categoryListData)
        
        val categories = categoryListData.list
        assertEquals(3, categories.size)
        
        // Test first category
        val firstCategory = categories[0]
        assertEquals("6", firstCategory.id)
        assertEquals("test", firstCategory.name)
        assertEquals(0, firstCategory.allow)
        assertEquals(0, firstCategory.deny)
        assertEquals(0, firstCategory.roles.size)
        
        // Test second category with roles
        val secondCategory = categories[1]
        assertEquals("7", secondCategory.id)
        assertEquals("test", secondCategory.name)
        assertEquals(0, secondCategory.allow)
        assertEquals(2048, secondCategory.deny)
        assertEquals(2, secondCategory.roles.size)
        
        // Test role details
        val userRole = secondCategory.roles[0]
        assertEquals("user", userRole.type)
        assertEquals(0, userRole.roleId)
        assertEquals("111111", userRole.userId)
        assertEquals(2048, userRole.allow)
        
        val roleRole = secondCategory.roles[1]
        assertEquals("role", roleRole.type)
        assertEquals(11111, roleRole.roleId)
        assertEquals("", roleRole.userId)
        assertEquals(2048, roleRole.allow)
    }

    @Test
    fun testErrorResponseHandling() = runTest {
        val channelId = "invalid_channel"
        val api = GetCategoryListApi.create(channelId)

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
    fun testFactoryMethod() {
        val channelId = "test_channel_123"
        val api = GetCategoryListApi.create(channelId)
        
        assertNotNull(api)
        assertTrue(api.url.toString().contains("channel_id=$channelId"))
    }

    private fun createSuccessResponseJson(): String {
        return """{
  "code": 0,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": "6",
        "name": "test",
        "allow": 0,
        "deny": 0,
        "roles": []
      },
      {
        "id": "7",
        "name": "test",
        "allow": 0,
        "deny": 2048,
        "roles": [
          {
            "type": "user",
            "role_id": 0,
            "user_id": "111111",
            "allow": 2048
          },
          {
            "type": "role",
            "role_id": 11111,
            "user_id": "",
            "allow": 2048
          }
        ]
      },
      {
        "id": "8",
        "name": "test2",
        "allow": 0,
        "deny": 0,
        "roles": []
      }
    ]
  }
}"""
    }
}
