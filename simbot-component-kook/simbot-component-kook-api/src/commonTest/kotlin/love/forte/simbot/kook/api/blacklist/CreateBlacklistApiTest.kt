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

package love.forte.simbot.kook.api.blacklist

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.kook.api.requestData
import love.forte.simbot.kook.api.requestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [CreateBlacklistApi] focusing on API structure and request body serialization.
 *
 * Reference: https://developer.kookapp.cn/doc/http/blacklist#加入黑名单
 *
 * @author ForteScarlet
 */
class CreateBlacklistApiTest {
    @Test
    fun testApiStructure() {
        val guildId = "test_guild_123"
        val targetId = "target_user_456"
        val api = CreateBlacklistApi.create(guildId, targetId)

        // Test basic API properties
        assertEquals(HttpMethod.Post, api.method)
        assertTrue(api.url.toString().contains("blacklist/create"))

        // Test URL path segments
        val url = api.url
        assertTrue(url.pathSegments.contains("blacklist"))
        assertTrue(url.pathSegments.contains("create"))

        // Test body is not null
        assertNotNull(api.body)
    }

    @Test
    fun testApiStructureWithOptionalParams() {
        val guildId = "test_guild_789"
        val targetId = "target_user_999"
        val remark = "Test blacklist reason"
        val delMsgDays = 3
        val api = CreateBlacklistApi.create(guildId, targetId, remark, delMsgDays)

        assertEquals(HttpMethod.Post, api.method)
        assertNotNull(api.body)
    }

    @Test
    fun testApiFactory() {
        val guildId = "factory_test_guild"
        val targetId = "factory_test_user"
        val api = CreateBlacklistApi.create(guildId, targetId)

        assertNotNull(api)
        assertEquals(HttpMethod.Post, api.method)
    }

    @Test
    fun testApiFactoryWithAllParams() {
        val guildId = "factory_test_guild_2"
        val targetId = "factory_test_user_2"
        val remark = "Spam behavior"
        val delMsgDays = 7
        val api = CreateBlacklistApi.create(guildId, targetId, remark, delMsgDays)

        assertNotNull(api)
        assertEquals(HttpMethod.Post, api.method)
        assertNotNull(api.body)
    }

    @Test
    fun testApiResponseDeserializationStrategy() {
        val api = CreateBlacklistApi.create("test_guild", "test_user")
        val serializer = api.resultDeserializationStrategy

        assertNotNull(serializer)
    }

    @Test
    fun testApiRequestResult() = runTest {
        val guildId = "test_guild_create"
        val targetId = "test_user_blacklist"
        val api = CreateBlacklistApi.create(guildId, targetId)
        val authorization = "Bot test_token"

        // KOOK API success response for create blacklist
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {}
        }"""

        val mockEngine = MockEngine { request ->
            // Verify request method and URL
            assertEquals(HttpMethod.Post, request.method)
            assertTrue(request.url.toString().contains("blacklist/create"))

            respond(
                content = apiResultJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestResult(client, authorization)

        assertTrue(result.isSuccess)
        assertEquals(0, result.code)
        assertEquals("操作成功", result.message)
        assertNotNull(result.data)
    }

    @Test
    fun testApiRequestData() = runTest {
        val guildId = "test_guild_data"
        val targetId = "test_user_data"
        val remark = "Test reason"
        val delMsgDays = 1
        val api = CreateBlacklistApi.create(guildId, targetId, remark, delMsgDays)
        val authorization = "Bot test_token"

        // KOOK API success response for create blacklist
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {}
        }"""

        val mockEngine = MockEngine { request ->
            // Verify request method and URL
            assertEquals(HttpMethod.Post, request.method)
            assertTrue(request.url.toString().contains("blacklist/create"))

            respond(
                content = apiResultJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestData(client, authorization)

        // For Unit result, we just verify it completes successfully
        assertEquals(Unit, result)
    }

    @Test
    fun testApiRequestMinimalParams() = runTest {
        val guildId = "minimal_guild"
        val targetId = "minimal_user"
        val api = CreateBlacklistApi.create(guildId, targetId)
        val authorization = "Bot test_token"

        // KOOK API success response
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {}
        }"""

        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertTrue(request.url.toString().contains("blacklist/create"))

            respond(
                content = apiResultJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestData(client, authorization)

        assertEquals(Unit, result)
    }
}
