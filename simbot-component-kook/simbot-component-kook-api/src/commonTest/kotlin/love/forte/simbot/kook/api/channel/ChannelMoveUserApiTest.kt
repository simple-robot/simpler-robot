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

package love.forte.simbot.kook.api.channel

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
 * Tests for [ChannelMoveUserApi] focusing on API structure and serialization.
 * 
 * @author ForteScarlet
 */
class ChannelMoveUserApiTest {

    @Test
    fun testApiBasics() {
        val targetId = "voice_channel_123"
        val userIds = listOf("user_1", "user_2", "user_3")
        val api = ChannelMoveUserApi.create(targetId, userIds)
        
        // Test API properties
        assertEquals(HttpMethod.Post, api.method)
        assertTrue(api.url.toString().contains("channel/move-user"))
        
        // Test body content
        val body = api.body
        assertNotNull(body, "Body should not be null")
    }
    
    @Test
    fun testUrlConstruction() {
        val api = ChannelMoveUserApi.create("test_channel", listOf("test_user"))
        val url = api.url.toString()
        
        // Verify URL contains correct Kook API base and path
        assertEquals("https://www.kookapp.cn/api/v3/channel/move-user", url)
    }
    
    @Test
    fun testRequestBodyNotNull() {
        val targetId = "voice_channel_456"
        val userIds = listOf("user_123", "user_456")
        val api = ChannelMoveUserApi.create(targetId, userIds)
        
        val body = api.body
        assertNotNull(body, "Body should not be null")
        
        // Verify that the body object exists and is not null
        assertTrue(body.toString().isNotEmpty())
    }
    
    @Test
    fun testMultipleApiInstances() {
        val api1 = ChannelMoveUserApi.create("channel1", listOf("user1"))
        val api2 = ChannelMoveUserApi.create("channel2", listOf("user2"))
        
        // Verify that different instances have different bodies
        val body1 = api1.body
        val body2 = api2.body
        
        assertNotNull(body1)
        assertNotNull(body2)
        
        // Bodies should be different objects (different parameters)
        assertTrue(body1 !== body2, "Different API instances should have different body objects")
    }
    
    @Test
    fun testApiFactory() {
        val targetId = "factory_test_channel"
        val userIds = listOf("factory_test_user1", "factory_test_user2")
        
        // Test factory method with List
        val api = ChannelMoveUserApi.create(targetId, userIds)
        assertNotNull(api)
        
        // Verify the created API has the correct properties
        assertEquals(HttpMethod.Post, api.method)
        assertNotNull(api.body)
        assertNotNull(api.url)
    }
    
    @Test
    fun testVarargsFactory() {
        val targetId = "varargs_test_channel"
        
        // Test factory method with varargs
        val api = ChannelMoveUserApi.create(targetId, "user1", "user2", "user3")
        assertNotNull(api)
        
        // Verify the created API has the correct properties
        assertEquals(HttpMethod.Post, api.method)
        assertNotNull(api.body)
        assertNotNull(api.url)
        assertTrue(api.url.toString().contains("channel/move-user"))
    }
    
    @Test
    fun testApiPathCorrectness() {
        val api = ChannelMoveUserApi.create("test", listOf("test"))
        val url = api.url
        
        // Check that the URL path is correct
        assertTrue(url.pathSegments.contains("channel"))
        assertTrue(url.pathSegments.contains("move-user"))
        
        // Verify it's a POST API
        assertEquals(HttpMethod.Post, api.method)
    }
    
    @Test
    fun testBodyNotNull() {
        val api = ChannelMoveUserApi.create("channel_test", listOf("user_test"))
        
        // Body should never be null for this API
        assertNotNull(api.body)
        
        // Multiple calls to body should return the same non-null value
        val body1 = api.body
        val body2 = api.body
        assertNotNull(body1)
        assertNotNull(body2)
    }
    
    @Test
    fun testSingleUserList() {
        val targetId = "single_user_channel"
        val userId = "single_user"
        val api = ChannelMoveUserApi.create(targetId, listOf(userId))
        
        // Verify API works with single user in list
        assertNotNull(api.body)
        assertEquals(HttpMethod.Post, api.method)
        assertTrue(api.url.toString().contains("channel/move-user"))
    }
    
    @Test
    fun testMultipleUsers() {
        val targetId = "multi_user_channel"
        val userIds = listOf("user1", "user2", "user3", "user4", "user5")
        val api = ChannelMoveUserApi.create(targetId, userIds)
        
        // Verify API works with multiple users
        assertNotNull(api.body)
        assertEquals(HttpMethod.Post, api.method)
        assertTrue(api.url.toString().contains("channel/move-user"))
    }
    
    @Test
    fun testEmptyUserList() {
        val targetId = "empty_user_channel"
        val userIds = emptyList<String>()
        val api = ChannelMoveUserApi.create(targetId, userIds)
        
        // Verify API works with empty user list (edge case)
        assertNotNull(api.body)
        assertEquals(HttpMethod.Post, api.method)
        assertTrue(api.url.toString().contains("channel/move-user"))
    }
    
    @Test
    fun testApiRequestResult() = runTest {
        val targetId = "voice_channel_123"
        val userIds = listOf("user_1", "user_2")
        val api = ChannelMoveUserApi.create(targetId, userIds)
        val authorization = "Bot test_token"
        
        // Mock response data - array of objects as per KOOK API documentation
        val responseData = """[
            {
                "id": "user_1",
                "username": "TestUser1",
                "identify_num": "1234",
                "online": true,
                "status": 1
            },
            {
                "id": "user_2", 
                "username": "TestUser2",
                "identify_num": "5678",
                "online": true,
                "status": 1
            }
        ]"""
        
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
        val targetId = "voice_channel_456"  
        val userIds = listOf("user_3", "user_4", "user_5")
        val api = ChannelMoveUserApi.create(targetId, userIds)
        val authorization = "Bot test_token"
        
        // Mock response data - array of JsonElement objects
        val responseData = """[
            {
                "id": "user_3",
                "username": "TestUser3",
                "identify_num": "9999",
                "online": true,
                "status": 1
            },
            {
                "id": "user_4",
                "username": "TestUser4", 
                "identify_num": "8888",
                "online": true,
                "status": 1
            },
            {
                "id": "user_5",
                "username": "TestUser5",
                "identify_num": "7777", 
                "online": false,
                "status": 0
            }
        ]"""
        
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
        api.requestData(client, authorization)
    }
}
