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
import kotlinx.serialization.json.Json
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.api.requestData
import love.forte.simbot.kook.api.requestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [GetBlacklistListApi] focusing on API structure and response serialization.
 * 
 * Reference: https://developer.kookapp.cn/doc/http/blacklist#获取黑名单列表
 * 
 * @author ForteScarlet
 */
class GetBlacklistListApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @Test
    fun testApiStructure() {
        val guildId = "test_guild_123"
        val api = GetBlacklistListApi.create(guildId)
        
        // Test basic API properties
        assertEquals(HttpMethod.Get, api.method)
        assertTrue(api.url.toString().contains("blacklist/list"))
        assertEquals(guildId, api.url.parameters["guild_id"])
        
        // Test URL path segments
        val url = api.url
        assertTrue(url.pathSegments.contains("blacklist"))
        assertTrue(url.pathSegments.contains("list"))
    }
    
    @Test
    fun testApiStructureWithPagination() {
        val guildId = "test_guild_456"
        val page = 2
        val pageSize = 25
        val api = GetBlacklistListApi.create(guildId, page, pageSize)
        
        assertEquals(HttpMethod.Get, api.method)
        assertEquals(guildId, api.url.parameters["guild_id"])
        assertEquals(page.toString(), api.url.parameters["page"])
        assertEquals(pageSize.toString(), api.url.parameters["page_size"])
    }
    
    @Test
    fun testApiFactory() {
        val guildId = "factory_test_guild"
        val api = GetBlacklistListApi.create(guildId)
        
        assertNotNull(api)
        assertEquals(HttpMethod.Get, api.method)
        assertEquals(guildId, api.url.parameters["guild_id"])
    }
    
    @Test
    fun testDeserializeBlacklistResponse() {
        // Using official KOOK API documentation example from:
        // https://developer.kookapp.cn/doc/http/blacklist#获取黑名单列表
        //language=json
        val apiResponseJson = """{
            "items": [
                {
                    "user_id": "26954***",
                    "created_time": 1640340668000,
                    "remark": "***",
                    "user": {
                        "id": "26954***",
                        "username": "***",
                        "identify_num": "2826",
                        "online": true,
                        "status": 1,
                        "avatar": "**",
                        "vip_avatar": "**",
                        "banner": "",
                        "nickname": "***",
                        "roles": [],
                        "is_vip": false,
                        "bot": false
                    }
                }
            ],
            "meta": {
                "page": 1,
                "page_total": 1,
                "page_size": 50,
                "total": 1
            },
            "sort": {}
        }"""
        
        val listData = json.decodeFromString(ListData.serializer(BlacklistItem.serializer()), apiResponseJson)
        
        assertNotNull(listData)
        assertEquals(1, listData.items.size)
        assertEquals(1, listData.meta.page)
        assertEquals(1, listData.meta.pageTotal)
        assertEquals(50, listData.meta.pageSize)
        assertEquals(1, listData.meta.total)
        
        val item = listData.items[0]
        assertEquals("26954***", item.userId)
        assertEquals(1640340668000L, item.createdTime)
        assertEquals("***", item.remark)
        
        val user = item.user
        assertEquals("26954***", user.id)
        assertEquals("***", user.username)
        assertEquals("2826", user.identifyNum)
        assertTrue(user.isOnline)
        assertEquals(1, user.status)
        assertEquals("**", user.avatar)
        assertEquals("***", user.nickname)
        assertEquals(false, user.isBot)
    }
    
    @Test
    fun testApiResponseDeserializationStrategy() {
        val api = GetBlacklistListApi.create("test_guild")
        val serializer = api.resultDeserializationStrategy
        
        assertNotNull(serializer)
        
        // Test with empty response
        val emptyResponse = """{
            "items": [],
            "meta": {
                "page": 1,
                "page_total": 0,
                "page_size": 50,
                "total": 0
            },
            "sort": {}
        }"""
        val emptyData = json.decodeFromString(serializer, emptyResponse)
        assertNotNull(emptyData)
        assertTrue(emptyData.items.isEmpty())
        assertEquals(0, emptyData.meta.total)
    }

    @Test
    fun testApiRequestResult() = runTest {
        val guildId = "test_guild_789"
        val api = GetBlacklistListApi.create(guildId)
        val authorization = "Bot test_token"
        val responseData = """{
            "items": [
                {
                    "user_id": "26954123",
                    "created_time": 1640340668000,
                    "remark": "Test blacklist reason",
                    "user": {
                        "id": "26954123",
                        "username": "testuser",
                        "identify_num": "2826",
                        "online": true,
                        "os": "Websocket",
                        "status": 1,
                        "avatar": "avatar_url",
                        "vip_avatar": "vip_avatar_url",
                        "banner": "",
                        "nickname": "Test User",
                        "roles": [],
                        "is_vip": false,
                        "bot": false
                    }
                }
            ],
            "meta": {
                "page": 1,
                "page_total": 1,
                "page_size": 50,
                "total": 1
            },
            "sort": {}
        }"""
        
        // Wrap in KOOK API result format
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": $responseData
        }"""
        
        val mockEngine = MockEngine { _ ->
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
        val guildId = "test_guild_999"
        val api = GetBlacklistListApi.create(guildId)
        val authorization = "Bot test_token"
        val responseData = """{
            "items": [
                {
                    "user_id": "26954456",
                    "created_time": 1640340668000,
                    "remark": "Spam user",
                    "user": {
                        "id": "26954456",
                        "username": "spammer",
                        "identify_num": "9999",
                        "online": false,
                        "status": 0,
                        "avatar": "spam_avatar",
                        "vip_avatar": "",
                        "banner": "",
                        "nickname": "Spam User",
                        "roles": [],
                        "is_vip": false,
                        "bot": false
                    }
                }
            ],
            "meta": {
                "page": 1,
                "page_total": 1,
                "page_size": 50,
                "total": 1
            },
            "sort": {}
        }"""
        
        // Wrap in KOOK API result format
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": $responseData
        }"""
        
        val mockEngine = MockEngine { _ ->
            respond(
                content = apiResultJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        
        val client = HttpClient(mockEngine)
        val listData = api.requestData(client, authorization)
        
        assertNotNull(listData)
        assertEquals(1, listData.items.size)
        assertEquals(1, listData.meta.total)
        
        val item = listData.items[0]
        assertEquals("26954456", item.userId)
        assertEquals(1640340668000L, item.createdTime)
        assertEquals("Spam user", item.remark)
        
        val user = item.user
        assertEquals("26954456", user.id)
        assertEquals("spammer", user.username)
        assertEquals("9999", user.identifyNum)
        assertEquals(false, user.isOnline)
    }
    
    @Test
    fun testApiRequestDataEmpty() = runTest {
        val api = GetBlacklistListApi.create("empty_guild")
        val authorization = "Bot test_token"
        
        // Test empty response
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {
                "items": [],
                "meta": {
                    "page": 1,
                    "page_total": 0,
                    "page_size": 50,
                    "total": 0
                },
                "sort": {}
            }
        }"""
        
        val mockEngine = MockEngine { _ ->
            respond(
                content = apiResultJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        
        val client = HttpClient(mockEngine)
        val listData = api.requestData(client, authorization)
        
        assertNotNull(listData)
        assertTrue(listData.items.isEmpty())
        assertEquals(0, listData.meta.total)
    }
}
