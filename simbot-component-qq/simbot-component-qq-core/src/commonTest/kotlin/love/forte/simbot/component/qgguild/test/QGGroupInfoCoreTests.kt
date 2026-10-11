/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGGroupSendSupportInteractionEvent
import love.forte.simbot.component.qguild.event.QGSendSupportPostSendEvent
import love.forte.simbot.component.qguild.event.QGSendSupportPreSendEvent
import love.forte.simbot.component.qguild.group.QGGroupWithInfo
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupImpl
import love.forte.simbot.component.qguild.internal.message.QGGroupAndC2CMessageContentImpl
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.GroupAtMessageCreate
import kotlin.test.*

/**
 * 验证群信息快照的查询、复用、失败传播及发送上下文。
 */
class QGGroupInfoCoreTests : AbstractInteractionTests() {
    private fun info(name: String, groupId: String = "group"): String =
        """{"group_openid":"$groupId","group_name":"$name","group_finger_memo":"简介","group_class_text":"文化","group_tags":["阅读"],"group_member_num":256}"""

    /**
     * 显式查询刷新快照，复用入口不查询，原句柄保持不变。
     */
    @Test
    fun snapshotsRefreshOnlyOnExplicitQuery() = runTest {
        var requests = 0
        val app = app { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/v2/groups/group/info", request.url.encodedPath)
            respondOk(info("name-${++requests}"))
        }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!
            assertEquals(0, requests)
            val snapshot = group.includeInfo()
            assertEquals("", group.name)
            assertEquals("name-1", snapshot.name)
            assertEquals("简介", snapshot.groupFingerMemo)
            assertEquals("文化", snapshot.groupClassText)
            assertEquals(listOf("阅读"), snapshot.groupTags)
            assertEquals(256, snapshot.groupMemberNum)
            assertSame(snapshot, snapshot.includeInfoOrSelf())
            assertSame(snapshot.groupInfo, snapshot.groupInfoOrQuery())
            assertEquals(1, requests)

            val refreshed = snapshot.includeInfo()
            assertNotSame(snapshot, refreshed)
            assertEquals("name-2", refreshed.name)
            assertEquals("name-1", snapshot.name)
            assertEquals("name-3", snapshot.groupInfo().groupName)
            assertEquals("name-4", group.groupInfoOrQuery().groupName)
            assertEquals("name-5", group.includeInfoOrSelf().name)
            assertEquals("name-6", bot.groupRelation.groupInfo("group".ID).groupName)
            assertEquals(6, requests)
            assertEquals("", group.name)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 拒绝身份不匹配的快照，平台错误不影响已有对象。
     */
    @Test
    fun snapshotIdentityAndPlatformFailuresArePreserved() = runTest {
        var mode = "ok"
        val app = app {
            when (mode) {
                "mismatch" -> respondOk(info("wrong", "other"))
                "failure" -> respond("""{"code":11253,"message":"not on whitelist"}""", HttpStatusCode.Forbidden)
                else -> respondOk(info("original"))
            }
        }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val snapshot = group.includeInfo()
            mode = "mismatch"
            assertEquals("other", group.groupInfo().groupOpenid)
            assertFailsWith<IllegalStateException> { group.includeInfo() }
            mode = "failure"
            val failure = assertFailsWith<QQGuildApiException> { snapshot.includeInfo() }
            assertEquals(11253, failure.info?.code)
            assertEquals("original", snapshot.name)
            assertSame(snapshot, snapshot.includeInfoOrSelf())
            assertEquals("", group.name)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 三种发送入口使用富化对象触发事件，并共享原消息 ID 和发送序号。
     */
    @Test
    fun enrichedSendsPreserveEventIdentityAndSequence() = runTest {
        val events = Channel<QGGroupSendSupportInteractionEvent>(Channel.UNLIMITED)
        var sends = 0
        testInteractionEvent<_, QGGroupSendSupportInteractionEvent>(
            mockClient = { request ->
                when (request.url.encodedPath) {
                    "/v2/groups/group/info" -> respondOk(info("snapshot"))
                    "/v2/groups/group/messages" -> {
                        val fields = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
                        assertEquals("source-message", fields["msg_id"]?.jsonPrimitive?.content)
                        assertEquals((++sends).toString(), fields["msg_seq"]?.jsonPrimitive?.content)
                        respondOk("""{"id":"sent-$sends","timestamp":1698435200}""")
                    }
                    else -> error("Unexpected path: ${request.url.encodedPath}")
                }
            },
            prepare = { bot ->
                val source = Json.decodeFromString(
                    GroupAtMessageCreate.Data.serializer(),
                    """{"id":"source-message","author":{"member_openid":"member"},"content":"received","timestamp":"2023-11-06T13:37:18+08:00","group_openid":"group"}"""
                )
                val group = QGGroupImpl(bot, "group".ID, source, atomic(1), false)
                group to group.includeInfo()
            },
            listener = { _, event, _, _ -> events.send(event) },
        ) { (group, snapshot), _ ->
            snapshot.send("text")
            snapshot.send(Text { "message" })
            snapshot.send(QGGroupAndC2CMessageContentImpl("content".ID, "content", emptyList()))

            // 发送后事件异步分发，使用实际调度器等待，避免测试虚拟时钟提前超时。
            val received = withContext(Dispatchers.Default) {
                withTimeout(5000) { List(6) { events.receive() } }
            }
            assertEquals(3, received.count { it is QGSendSupportPreSendEvent })
            assertEquals(3, received.count { it is QGSendSupportPostSendEvent })
            received.forEach {
                assertSame(snapshot, it.content)
                assertIs<QGGroupWithInfo>(it.content)
                assertEquals("snapshot", it.content.name)
            }
            group.send("original")
            assertEquals(4, sends)
        }
        events.close()
    }
}
