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

package love.forte.simbot.kook.api.channel

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.requestData
import love.forte.simbot.kook.api.requestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [GetChannelUserListApi] focusing on API structure and response serialization.
 * 
 * Reference: https://developer.kookapp.cn/doc/http/channel#语音频道用户列表
 * 
 * @author ForteScarlet
 * @since 4.2.0
 */
class GetChannelUserListApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @Test
    fun testApiStructure() {
        val channelId = "test_voice_channel_123"
        val api = GetChannelUserListApi.create(channelId)
        
        // Test basic API properties
        assertEquals(HttpMethod.Get, api.method)
        assertTrue(api.url.toString().contains("channel/user-list"))
        assertEquals(channelId, api.url.parameters["channel_id"])
        
        // Test URL path segments
        val url = api.url
        assertTrue(url.pathSegments.contains("channel"))
        assertTrue(url.pathSegments.contains("user-list"))
    }
    
    @Test
    fun testApiFactory() {
        val channelId = "factory_test_channel"
        val api = GetChannelUserListApi.create(channelId)
        
        assertNotNull(api)
        assertEquals(HttpMethod.Get, api.method)
        assertEquals(channelId, api.url.parameters["channel_id"])
    }
    
    @Test
    fun testDeserializeVoiceChannelUserListResponse() {
        // Using official KOOK API documentation example from:
        // https://developer.kookapp.cn/doc/http/channel#语音频道用户列表
        //language=json
        val apiResponseJson = """[
            {
                "id": "999999999",
                "username": "XXX",
                "identify_num": "9999",
                "online": true,
                "os": "Websocket",
                "status": 1,
                "avatar": "XXX",
                "vip_avatar": "XXX",
                "nickname": "XXX",
                "roles": [4131873],
                "is_vip": false,
                "is_ai_reduce_noise": true,
                "is_personal_card_bg": false,
                "bot": false,
                "mobile_verified": true,
                "joined_at": 1639808384000,
                "active_time": 1639808384000,
                "live_info": {
                    "in_live": false,
                    "audience_count": 0,
                    "live_thumb": "",
                    "live_start_time": 0
                }
            }
        ]"""
        
        val listSerializer = ListSerializer(VoiceChannelUser.serializer())
        val userList = json.decodeFromString(listSerializer, apiResponseJson)
        
        assertNotNull(userList)
        assertEquals(1, userList.size)
        
        val user = userList[0]
        assertEquals("999999999", user.id)
        assertEquals("XXX", user.username)
        assertEquals("9999", user.identifyNum)
        assertTrue(user.isOnline)
        assertEquals("Websocket", user.os)
        assertEquals(1, user.status)
        assertEquals("XXX", user.avatar)
        assertEquals("XXX", user.vipAvatar)
        assertEquals("XXX", user.nickname)
        assertEquals(1, user.roles.size)
        assertEquals(4131873L, user.roles[0])
        assertEquals(false, user.isVip)
        assertTrue(user.isAiReduceNoise)
        assertEquals(false, user.isPersonalCardBg)
        assertEquals(false, user.isBot)
        assertTrue(user.isMobileVerified)
        assertEquals(1639808384000L, user.joinedAt)
        assertEquals(1639808384000L, user.activeTime)
        
        // Test LiveInfo
        val liveInfo = user.liveInfo
        assertNotNull(liveInfo)
        assertEquals(false, liveInfo.inLive)
        assertEquals(0, liveInfo.audienceCount)
        assertEquals("", liveInfo.liveThumb)
        assertEquals(0L, liveInfo.liveStartTime)
    }
    
    @Test
    fun testApiResponseDeserializationStrategy() {
        val api = GetChannelUserListApi.create("test_channel")
        val serializer = api.resultDeserializationStrategy
        
        assertNotNull(serializer)
        
        // Test with empty response
        val emptyResponse = json.decodeFromString(serializer, "[]")
        assertNotNull(emptyResponse)
        assertTrue(emptyResponse.isEmpty())
    }

    
    @Test
    fun testApiRequestResult() = runTest {
        val api = GetChannelUserListApi.create("test_channel")
        val authorization = "Bot test_token"
        val responseData = """[
            {
                "id": "999999999",
                "username": "XXX",
                "identify_num": "9999",
                "online": true,
                "os": "Websocket",
                "status": 1,
                "avatar": "XXX",
                "vip_avatar": "XXX",
                "nickname": "XXX",
                "roles": [4131873],
                "is_vip": false,
                "is_ai_reduce_noise": true,
                "is_personal_card_bg": false,
                "bot": false,
                "mobile_verified": true,
                "joined_at": 1639808384000,
                "active_time": 1639808384000,
                "live_info": {
                    "in_live": false,
                    "audience_count": 0,
                    "live_thumb": "",
                    "live_start_time": 0
                }
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
        val api = GetChannelUserListApi.create("test_channel")
        val authorization = "Bot test_token"
        val responseData = """[
            {
                "id": "999999999",
                "username": "XXX",
                "identify_num": "9999",
                "online": true,
                "os": "Websocket",
                "status": 1,
                "avatar": "XXX",
                "vip_avatar": "XXX",
                "nickname": "XXX",
                "roles": [4131873],
                "is_vip": false,
                "is_ai_reduce_noise": true,
                "is_personal_card_bg": false,
                "bot": false,
                "mobile_verified": true,
                "joined_at": 1639808384000,
                "active_time": 1639808384000,
                "live_info": {
                    "in_live": false,
                    "audience_count": 0,
                    "live_thumb": "",
                    "live_start_time": 0
                }
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
        val userList = api.requestData(client, authorization)
        
        assertNotNull(userList)
        assertEquals(1, userList.size)
        
        val user = userList[0]
        assertEquals("999999999", user.id)
        assertEquals("XXX", user.username)
        assertEquals("9999", user.identifyNum)
        assertTrue(user.isOnline)
        assertEquals("Websocket", user.os)
    }
    
    @Test
    fun testApiRequestDataEmpty() = runTest {
        val api = GetChannelUserListApi.create("empty_channel")
        val authorization = "Bot test_token"
        
        // Test empty response
        val apiResultJson = """{
            "code": 0,
            "message": "操作成功",
            "data": []
        }"""
        
        val mockEngine = MockEngine { _ ->
            respond(
                content = apiResultJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        
        val client = HttpClient(mockEngine)
        val userList = api.requestData(client, authorization)
        
        assertNotNull(userList)
        assertTrue(userList.isEmpty())
    }
}
