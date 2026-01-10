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

package love.forte.simbot.kook.api.voice

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.request
import love.forte.simbot.kook.api.requestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for [GetVoiceListApi]
 */
class VoiceListApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val api = GetVoiceListApi.create()

        assertEquals(HttpMethod.Get, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/voice/list", api.url.toString())
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        val successResponseJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {
                "items": [
                    {
                        "id": "12345",
                        "guild_id": "12345",
                        "parent_id": "11234",
                        "name": "语音频道"
                    }
                ],
                "meta": {
                    "page": 1,
                    "page_total": 1,
                    "page_size": 50,
                    "total": 1
                },
                "sort": {}
            }
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        assertNotNull(apiResult.data)
    }

    @Test
    fun testEmptyListResponseDeserialization() {
        val emptyResponseJson = """{
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

        val apiResult = json.decodeFromString(ApiResult.serializer(), emptyResponseJson)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        assertNotNull(apiResult.data)
    }

    @Test
    fun testErrorResponseDeserialization() {
        val errorResponseJson = """{
            "code": 40100,
            "message": "无权限访问"
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), errorResponseJson)
        assertEquals(40100, apiResult.code)
        assertEquals("无权限访问", apiResult.message)
    }

    @Test
    fun testApiFactory() = runTest {
        val api1 = GetVoiceListApi.create()
        val api2 = GetVoiceListApi.create(page = 5, pageSize = 10)

        assertNotNull(api1)
        assertNotNull(api2)
        assertEquals(HttpMethod.Get, api1.method)
        assertEquals(HttpMethod.Get, api2.method)

        val mockEngine = MockEngine { request ->
            val parameters = request.url.parameters

            assertEquals("5", parameters["page"])
            assertEquals("10", parameters["page_size"])

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {"items": [],"meta": {"page": 1,"page_total":1,"page_size":50,"total":2},"sort": {"id": 1}}
                }"""
            )
        }

        val client = HttpClient(mockEngine)

        val response = api2.request(client, "Bot test-token")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testRealisticScenario() = runTest {
        val api = GetVoiceListApi.create()

        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/api/v3/voice/list", request.url.encodedPath)

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {
                        "items": [
                            {
                                "id": "voice_channel_1",
                                "guild_id": "guild_123",
                                "parent_id": "category_456",
                                "name": "General Voice"
                            },
                            {
                                "id": "voice_channel_2",
                                "guild_id": "guild_123",
                                "parent_id": "category_456",
                                "name": "Music Room"
                            }
                        ],
                        "meta": {
                            "page": 1,
                            "page_total": 1,
                            "page_size": 50,
                            "total": 2
                        },
                        "sort": {
                            "id": 1
                        }
                    }
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestResult(client, "Bot test-token")
        val listResult = result.parseDataOrThrow(deserializationStrategy = api.resultDeserializationStrategy)

        assertEquals(2, listResult.items.size)

        val firstChannel = listResult.items[0]
        assertEquals("voice_channel_1", firstChannel.id)
        assertEquals("guild_123", firstChannel.guildId)
        assertEquals("category_456", firstChannel.parentId)
        assertEquals("General Voice", firstChannel.name)

        val secondChannel = listResult.items[1]
        assertEquals("voice_channel_2", secondChannel.id)
        assertEquals("guild_123", secondChannel.guildId)
        assertEquals("category_456", secondChannel.parentId)
        assertEquals("Music Room", secondChannel.name)

        assertEquals(1, listResult.meta.page)
        assertEquals(1, listResult.meta.pageTotal)
        assertEquals(50, listResult.meta.pageSize)
        assertEquals(2, listResult.meta.total)

        assertEquals(1, listResult.sort["id"])
    }

    @Test
    fun testMultipleVoiceChannelsDeserialization() {
        val multipleChannelsJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {
                "items": [
                    {
                        "id": "ch1",
                        "guild_id": "g1",
                        "parent_id": "p1",
                        "name": "Channel 1"
                    },
                    {
                        "id": "ch2",
                        "guild_id": "g1",
                        "parent_id": "p1",
                        "name": "Channel 2"
                    },
                    {
                        "id": "ch3",
                        "guild_id": "g2",
                        "parent_id": "p2",
                        "name": "Channel 3"
                    }
                ],
                "meta": {
                    "page": 1,
                    "page_total": 1,
                    "page_size": 50,
                    "total": 3
                },
                "sort": {}
            }
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), multipleChannelsJson)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        assertNotNull(apiResult.data)
    }
}
