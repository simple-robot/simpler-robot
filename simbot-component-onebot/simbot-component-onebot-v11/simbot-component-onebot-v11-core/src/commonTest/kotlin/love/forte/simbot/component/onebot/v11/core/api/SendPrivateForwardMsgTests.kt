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
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.api.nonstandard.OneBotNonStandardApi
import love.forte.simbot.component.onebot.v11.core.api.nonstandard.SendPrivateForwardMsgApi
import love.forte.simbot.component.onebot.v11.message.segment.OneBotForwardNode
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 测试 API [SendPrivateForwardMsgApi]
 * @author ForteScarlet
 */
@OptIn(OneBotNonStandardApi::class)
class SendPrivateForwardMsgTests {

    private val json = OneBot11.DefaultJson

    /**
     * 测试 llonebot 供应商的私聊合并转发消息
     *
     * 参考 .local/send_private_forward_msg_api_llonebot.md 的实现
     */
    @Test
    fun testLLOneBotApi() = runTest {
        val userId = 379450326.ID
        val nickname = "测试昵称"

        // 创建一个消息节点
        val node = OneBotForwardNode.create(
            userId = userId,
            nickname = nickname,
            content = listOf(OneBotText.create("测试消息"))
        )

        // 创建 API
        val api = SendPrivateForwardMsgApi.create(userId, listOf(node))

        val mockEngine = MockEngine { request ->
            // 验证请求 URL
            assertEquals("send_private_forward_msg", request.url.segments.last())

            // 解析请求体，验证序列化结果
            val requestBody = request.body.toByteArray().decodeToString()
            val requestJson = json.parseToJsonElement(requestBody).jsonObject

            // 验证 user_id
            val userIdValue = requestJson["user_id"]?.jsonPrimitive?.content
            assertEquals(userId.toString(), userIdValue)

            // 验证 messages 结构
            val messages = requestJson["messages"]?.jsonArray
            assertNotNull(messages)
            assertEquals(1, messages.size)

            val nodeJson = messages[0].jsonObject
            assertEquals("node", nodeJson["type"]?.jsonPrimitive?.content)

            val nodeData = nodeJson["data"]?.jsonObject
            assertNotNull(nodeData)

            val nodeContent = nodeData["content"]?.jsonArray
            assertNotNull(nodeContent)
            assertEquals(1, nodeContent.size)

            val textSegment = nodeContent[0].jsonObject
            assertEquals("text", textSegment["type"]?.jsonPrimitive?.content)
            assertEquals("测试消息", textSegment["data"]?.jsonObject?.get("text")?.jsonPrimitive?.content)

            // 返回 llonebot 格式的响应
            respondOk(
                """
                {
                    "status": "ok",
                    "retcode": 0,
                    "data": {
                        "message_id": 1934956788
                    },
                    "message": "",
                    "wording": ""
                }
                """.trimIndent()
            )
        }

        val client = HttpClient(mockEngine)

        // 执行请求并验证结果
        val result = api.requestData(client, "http://127.0.0.1:8080/")

        assertEquals("1934956788", result.messageId.literal)
    }

    /**
     * 测试 language-onebot 供应商的私聊合并转发消息
     *
     * 参考 .local/send_private_forward_msg_api_languageonebot.md 的实现
     */
    @Test
    fun testLanguageOneBotApi() = runTest {
        val userId = 379450326.ID
        val nickname = "测试昵称"

        // 创建一个消息节点
        val node = OneBotForwardNode.create(
            userId = userId,
            nickname = nickname,
            content = listOf(OneBotText.create("测试消息"))
        )

        // 创建 API
        val api = SendPrivateForwardMsgApi.create(userId, listOf(node))

        val mockEngine = MockEngine { request ->
            // 验证请求 URL
            assertEquals("send_private_forward_msg", request.url.segments.last())

            // 解析请求体，验证序列化结果
            val requestBody = request.body.toByteArray().decodeToString()
            val requestJson = json.parseToJsonElement(requestBody).jsonObject

            // 验证 user_id
            val userIdValue = requestJson["user_id"]?.jsonPrimitive?.content
            assertEquals(userId.toString(), userIdValue)

            // 验证 messages 结构
            val messages = requestJson["messages"]?.jsonArray
            assertNotNull(messages)
            assertEquals(1, messages.size)

            val nodeJson = messages[0].jsonObject
            assertEquals("node", nodeJson["type"]?.jsonPrimitive?.content)

            val nodeData = nodeJson["data"]?.jsonObject
            assertNotNull(nodeData)

            // language-onebot 使用 user_id 和 nickname
            val userIdInNode = nodeData["user_id"]?.jsonPrimitive?.content
            assertEquals(userId.toString(), userIdInNode)

            val nicknameValue = nodeData["nickname"]?.jsonPrimitive?.content
            assertEquals(nickname, nicknameValue)

            val nodeContent = nodeData["content"]?.jsonArray
            assertNotNull(nodeContent)
            assertEquals(1, nodeContent.size)

            val textSegment = nodeContent[0].jsonObject
            assertEquals("text", textSegment["type"]?.jsonPrimitive?.content)
            assertEquals("测试消息", textSegment["data"]?.jsonObject?.get("text")?.jsonPrimitive?.content)

            // 返回 language-onebot 格式的响应，与 llonebot 相比，多了 forward_id
            // 但 SendPrivateForwardMsgApi 使用的是 SendMsgResult，所以只有 message_id 会被解析
            respondOk(
                """
                {
                    "status": "ok",
                    "retcode": 0,
                    "data": {
                        "message_id": 76543210,
                        "forward_id": "abcdefghijklmnopqrstuvwxyz"
                    }
                }
                """.trimIndent()
            )
        }

        val client = HttpClient(mockEngine)

        // 执行请求并验证结果
        val result = api.requestData(client, "http://127.0.0.1:8080/")

        assertEquals("76543210", result.messageId.literal)
    }
}
