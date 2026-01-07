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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.requestResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for [VoiceJoinApi]
 */
class VoiceJoinApiTest {
    private val json = Json(Kook.DEFAULT_JSON) {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun testApiBasics() {
        val channelId = "1234567890"
        val api = VoiceJoinApi.create(channelId)

        assertEquals(HttpMethod.Post, api.method)
        assertEquals("https://www.kookapp.cn/api/v3/voice/join", api.url.toString())
        assertNotNull(api.body)
    }

    @Test
    fun testRequestBodyStructure() = runTest {
        val channelId = "1234567890"
        val audioSsrc = "1111"
        val audioPt = "111"
        val rtcpMux = true
        val password = "test123"
        val api = VoiceJoinApi.create(channelId, audioSsrc, audioPt, rtcpMux, password)

        var capturedRequestBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedRequestBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"code": 0, "message": "操作成功", "data": {"ip": "127.0.0.1", "port": "1000", "rtcp_port": 10001, "rtcp_mux": false, "bitrate": 48000, "audio_ssrc": "1111", "audio_pt": "111"}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        api.requestResult(client, "Bot test-token")

        assertNotNull(capturedRequestBody)
        val requestJson = json.parseToJsonElement(capturedRequestBody).jsonObject

        assertEquals(channelId, requestJson["channel_id"]?.jsonPrimitive?.content)
        assertEquals(audioSsrc, requestJson["audio_ssrc"]?.jsonPrimitive?.content)
        assertEquals(audioPt, requestJson["audio_pt"]?.jsonPrimitive?.content)
        assertEquals(rtcpMux, requestJson["rtcp_mux"]?.jsonPrimitive?.content?.toBoolean())
        assertEquals(password, requestJson["password"]?.jsonPrimitive?.content)
    }

    @Test
    fun testSuccessfulResponseDeserialization() {
        val successResponseJson = """{
            "code": 0,
            "message": "操作成功",
            "data": {
                "ip": "127.0.0.1",
                "port": "1000",
                "rtcp_port": 10001,
                "rtcp_mux": false,
                "bitrate": 48000,
                "audio_ssrc": "1111",
                "audio_pt": "111"
            }
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), successResponseJson)
        assertEquals(0, apiResult.code)
        assertEquals("操作成功", apiResult.message)
        assertNotNull(apiResult.data)
    }

    @Test
    fun testErrorResponseDeserialization() {
        val errorResponseJson = """{
            "code": 40000,
            "message": "频道不存在"
        }"""

        val apiResult = json.decodeFromString(ApiResult.serializer(), errorResponseJson)
        assertEquals(40000, apiResult.code)
        assertEquals("频道不存在", apiResult.message)
    }

    @Test
    fun testApiFactory() {
        val channelId = "test_channel"
        
        // Test simple factory
        val api1 = VoiceJoinApi.create(channelId)
        assertNotNull(api1)
        assertEquals(HttpMethod.Post, api1.method)

        // Test factory with all parameters
        val api2 = VoiceJoinApi.create(channelId, "2222", "222", false, "secret")
        assertNotNull(api2)
        assertEquals(HttpMethod.Post, api2.method)
    }

    @Test
    fun testRealisticScenario() = runTest {
        val channelId = "real_channel_id"
        val api = VoiceJoinApi.create(channelId, "1234", "112", true)

        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/api/v3/voice/join", request.url.encodedPath)

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {
                        "ip": "192.168.1.100",
                        "port": "5000",
                        "rtcp_port": 5001,
                        "rtcp_mux": true,
                        "bitrate": 64000,
                        "audio_ssrc": "1234",
                        "audio_pt": "112"
                    }
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine)
        val result = api.requestResult(client, "Bot test-token")
        val joinResult = result.parseDataOrThrow(deserializationStrategy = api.resultDeserializationStrategy)

        assertEquals("192.168.1.100", joinResult.ip)
        assertEquals("5000", joinResult.port)
        assertEquals(5001, joinResult.rtcpPort)
        assertEquals(true, joinResult.rtcpMux)
        assertEquals(64000, joinResult.bitrate)
        assertEquals("1234", joinResult.audioSsrc)
        assertEquals("112", joinResult.audioPt)
    }
}
