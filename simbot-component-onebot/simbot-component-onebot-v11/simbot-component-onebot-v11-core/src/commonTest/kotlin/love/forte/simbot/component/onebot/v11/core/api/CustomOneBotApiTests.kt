/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalCustomOneBotApi::class)
class CustomOneBotApiTests {
    private val json = OneBot11.DefaultJson

    /**
     * 仿照一个标准API的格式定制一个[CustomOneBotApi]，
     * 确保效果相同。
     */
    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun testCustomBotApi() = runTest {
        val msgId = 123.ID
        val standApi = GetMsgApi.create(msgId)

        @Serializable
        data class TestBody(@SerialName("message_id") val messageId: ID)

        val customApi = CustomOneBotApi(action = "get_msg") {
            body = TestBody(msgId)
            dataDeserializer(GetMsgResult.serializer())
        }

        val mockEngine = MockEngine { requestData ->
            val action = requestData.url.segments.last()
            assertEquals("get_msg", action, "action must be `get_msg`, but $action")

            val body = requestData.body.toByteArray().decodeToString()
            val obj = json.parseToJsonElement(body)
            val msgIdPrimitive = obj.jsonObject["message_id"]?.jsonPrimitive
            assertNotNull(msgIdPrimitive, "message_id must not null.")
            assertTrue(msgIdPrimitive.isString, "message_id must be string.")
            assertEquals(
                msgIdPrimitive.content,
                msgId.literal,
                "message_id must be $msgId, but ${msgIdPrimitive.content}"
            )

            respond(
                """{
                |"retcode":0,
                |"status":"ok",
                |"data":{
                |   "time":1677077777,
                |   "message_type":"private",
                |   "message_id":"123",
                |   "real_id":123456,
                |   "message":[{"type":"text","data":{"text":"Hello, World!"}}],
                |   "sender":{
                |       "user_id":123456,
                |       "nickname":"测试账号",
                |       "age":18,
                |       "sex":"male",
                |       "level":1,
                |       "role":"admin",
                |       "title":"管理员"
                |       }
                |   }
                |}""".trimMargin()
            )
        }

        val client = HttpClient(mockEngine)

        listOf(standApi, customApi).forEach { api ->
            val result = api.requestData(client, "https://ob11.example.com", "access_token")

            assertEquals(result.realId.value, 123456)
            assertEquals(result.messageType, "private")
            assertEquals(result.messageId.value, 123)
            assertEquals(1, result.message.size)
            val message = result.message.first()
            assertIs<OneBotText>(message)
            assertEquals(message.data.text, "Hello, World!")
            val sender = result.sender
            assertEquals(123456, sender["user_id"]?.jsonPrimitive?.intOrNull)
            assertEquals("测试账号", sender["nickname"]?.jsonPrimitive?.content)
            assertEquals(18, sender["age"]?.jsonPrimitive?.intOrNull)
            assertEquals("male", sender["sex"]?.jsonPrimitive?.content)
            assertEquals(1, sender["level"]?.jsonPrimitive?.intOrNull)
            assertEquals("admin", sender["role"]?.jsonPrimitive?.content)
            assertEquals("管理员", sender["title"]?.jsonPrimitive?.content)
        }
    }

    /**
     * 测试使用builder模式（Java风格API）创建CustomOneBotApi
     */
    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun testBuilderPattern() = runTest {
        val msgId = 123.ID

        @Serializable
        data class TestBody(@SerialName("message_id") val messageId: ID)

        val customApi = CustomOneBotApi.builder<GetMsgResult>("get_msg")
            .body(TestBody(msgId))
            .dataDeserializer(GetMsgResult.serializer())
            .build()

        val mockEngine = MockEngine { requestData ->
            val action = requestData.url.segments.last()
            assertEquals("get_msg", action, "action must be `get_msg`, but $action")

            val body = requestData.body.toByteArray().decodeToString()
            val obj = json.parseToJsonElement(body)
            val msgIdPrimitive = obj.jsonObject["message_id"]?.jsonPrimitive
            assertNotNull(msgIdPrimitive, "message_id must not null.")
            assertTrue(msgIdPrimitive.isString, "message_id must be string.")
            assertEquals(
                msgIdPrimitive.content,
                msgId.literal,
                "message_id must be $msgId, but ${msgIdPrimitive.content}"
            )

            respond(
                """{
                |"retcode":0,
                |"status":"ok",
                |"data":{
                |   "time":1677077777,
                |   "message_type":"private",
                |   "message_id":"123",
                |   "real_id":123456,
                |   "message":[{"type":"text","data":{"text":"Hello, World!"}}],
                |   "sender":{
                |       "user_id":123456,
                |       "nickname":"测试账号",
                |       "age":18,
                |       "sex":"male",
                |       "level":1,
                |       "role":"admin",
                |       "title":"管理员"
                |       }
                |   }
                |}""".trimMargin()
            )
        }

        val client = HttpClient(mockEngine)
        val result = customApi.requestData(client, "https://ob11.example.com", "access_token")

        assertEquals(result.realId.value, 123456)
        assertEquals(result.messageType, "private")
        assertEquals(result.messageId.value, 123)
        assertEquals(1, result.message.size)
        val message = result.message.first()
        assertIs<OneBotText>(message)
        assertEquals(message.data.text, "Hello, World!")
    }

    /**
     * 测试使用不同的HTTP方法
     */
    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun testHttpMethods() = runTest {
        // Test GET method with builder
        val getApi = CustomOneBotApi("test_get", HttpMethod.Get) {
            deserializer { OneBotApiResult(0, "ok", Unit) }
        }

        assertEquals(HttpMethod.Get, getApi.method)
        assertEquals("test_get", getApi.action)
        assertNull(getApi.body)

        // Test POST method (default) with DSL
        val postApi = CustomOneBotApi("test_post") {
            body = "test body"
            deserializer {
                OneBotApiResult(0, "ok", Unit)
            }
        }

        assertEquals(HttpMethod.Post, postApi.method)
        assertEquals("test_post", postApi.action)
        assertEquals("test body", postApi.body)

        // Verify GET request throws error when trying to set body
        assertFailsWith<IllegalArgumentException> {
            CustomOneBotApi.builder<Unit>("test_invalid", HttpMethod.Get)
                .body("this should fail")
                .build()
        }
    }

    /**
     * 测试使用各种类型的请求体
     */
    @Test
    fun testVariousBodyTypes() = runTest {
        // JSON string body
        val jsonStringBody = """{"key":"value"}"""
        val jsonStringApi = CustomOneBotApi("test_json_string") {
            body = jsonStringBody
            deserializer {
                OneBotApiResult(0, "ok", Unit)
            }
        }
        assertEquals(jsonStringBody, jsonStringApi.body)

        // Serializable object body
        @Serializable
        data class TestSerializable(val key: String)

        val serializableObject = TestSerializable("value")
        val serializableApi = CustomOneBotApi("test_serializable") {
            body = serializableObject
            deserializer {
                OneBotApiResult(0, "ok", Unit)
            }
        }
        assertEquals(serializableObject, serializableApi.body)

        // Null body
        val nullBodyApi = CustomOneBotApi("test_null_body") {
            body = null
            deserializer {
                OneBotApiResult(0, "ok", Unit)
            }
        }
        assertNull(nullBodyApi.body)
    }

    /**
     * 测试使用自定义反序列化器
     */
    @Test
    fun testCustomDeserializer() = runTest {
        // Create a custom deserializer function
        val customDeserializer = DynamicOneBotApiDeserializer {
            OneBotApiResult(0, "ok", "Custom deserialized result")
        }

        val api = CustomOneBotApi("test_custom_deserializer") {
            body = "test"
            deserializer(customDeserializer)
        }

        // Test the deserialize method
        val result = api.deserialize("""{"retcode":1,"status":"failed","data":null}""")

        assertEquals(0, result.retcode)
        assertEquals("ok", result.status)
        assertEquals("Custom deserialized result", result.data)

        // Test with direct deserializer function
        val directApi = CustomOneBotApi("test_direct_deserializer") {
            body = "test"
            deserializer {
                // Intentionally ignoring the input and returning a fixed result
                OneBotApiResult(0, "ok", "Direct deserialized result")
            }
        }

        val directResult = directApi.deserialize("""{"anything":"here"}""")
        assertEquals("Direct deserialized result", directResult.data)
    }

    /**
     * 测试错误处理场景
     */
    @Test
    fun testErrorHandling() = runTest {
        // Test missing deserializer
        assertFailsWith<IllegalArgumentException> {
            CustomOneBotApi.builder<Unit>("test_no_deserializer")
                .body("test")
                // Intentionally not setting a deserializer
                .build()
        }

        // Test deserializing error response
        val errorHandlingApi = CustomOneBotApi("test_error_handling") {
            body = "test"
            deserializer { raw ->
                val parsedJson = Json.parseToJsonElement(raw).jsonObject
                val retcode = parsedJson["retcode"]?.jsonPrimitive?.intOrNull ?: 0
                val status = parsedJson["status"]?.jsonPrimitive?.content ?: "ok"

                if (retcode != 0) {
                    OneBotApiResult(retcode, status, "Error occurred")
                } else {
                    OneBotApiResult(retcode, status, "Success")
                }
            }
        }

        val successResult = errorHandlingApi.deserialize("""{"retcode":0,"status":"ok","data":null}""")
        assertEquals(0, successResult.retcode)
        assertEquals("Success", successResult.data)

        val errorResult = errorHandlingApi.deserialize("""{"retcode":1,"status":"failed","data":null}""")
        assertEquals(1, errorResult.retcode)
        assertEquals("failed", errorResult.status)
        assertEquals("Error occurred", errorResult.data)
    }
}
