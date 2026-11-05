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
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.group.create
import love.forte.simbot.component.qguild.group.update
import love.forte.simbot.qguild.QQGuildApiException
import kotlin.test.*
import kotlin.time.Instant

class QGGroupJoinApprovalStrategyCoreTests : AbstractInteractionTests() {
    private val path = "/v2/groups/join_approval_strategy"
    private val time = "2027-08-05T15:30:16+08:00"
    private fun record(id: String): String =
        """{"strategy_id":"$id","group_ids":["00123"],"whitelist_user_count":2,"is_enable":"on","expire_at":"$time","created_at":"$time","updated_at":"$time"}"""

    @Test
    fun creationPreservesReceiptAndBindsAllOperationsToId() = runTest {
        val requests = mutableListOf<String>()
        val app = app { request ->
            requests += request.url.encodedPath
            when {
                request.url.encodedPath == path -> respondOk("""{"strategy_id":"created","is_enable":"on","expire_at":"$time"}""")
                request.method == HttpMethod.Patch -> {
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals(JsonPrimitive("off"), body["is_enable"])
                    respondOk("""{"is_enable":"off","expire_at":"$time"}""")
                }
                request.url.encodedPath.endsWith("/whitelist_users") -> {
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals(JsonArray(listOf(JsonPrimitive("00123"))), body["whitelist_users"])
                    assertEquals(JsonPrimitive(if (requests.size == 3) "add" else "del"), body["op"])
                    respondOk("""{"strategy_id":"created","whitelist_user_count":3,"updated_at":"$time"}""")
                }
                else -> respondOk()
            }
        }
        val bot = app.bot()
        try {
            val manager = bot.groupJoinApprovalStrategies
            manager.handle("known".ID)
            assertTrue(requests.isEmpty())
            val created = manager.create { addGroupId("00123") }
            assertEquals(1, requests.size)
            assertEquals("created", created.id.toString())
            assertTrue(created.isEnable)
            assertEquals(Instant.parse(time), created.expireAt)
            assertEquals("created", created.source.strategyId)
            assertFalse(created.update { isEnable = false }.isEnable)
            assertEquals(3, created.addWhitelistUsers(listOf("00123".ID)).whitelistUserCount)
            created.removeWhitelistUsers(listOf("00123".ID))
            created.execute()
            created.delete()
            assertTrue(created.source.isEnable)
            assertEquals(listOf(path, "$path/created", "$path/created/whitelist_users", "$path/created/whitelist_users", "$path/created/execute", "$path/created"), requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun listIsColdRecollectableAndSupportsSingleEmptyPage() = runTest {
        val cursors = mutableListOf<String?>()
        val app = app { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("3", request.url.parameters["limit"])
            val cursor = request.url.parameters["cursor"]
            cursors += cursor
            if (cursor == "start") respondOk("""{"strategies":[],"next_cursor":"next"}""")
            else respondOk("""{"strategies":[${record("listed")}],"next_cursor":""}""")
        }
        val bot = app.bot()
        try {
            val manager = bot.groupJoinApprovalStrategies
            val list = manager.list("start", 3)
            assertTrue(cursors.isEmpty())
            repeat(2) {
                val item = list.asFlow().toList().single()
                assertEquals("listed", item.id.toString())
                assertEquals(listOf("00123".ID), item.groupIds)
                assertNull(item.groupOpenids)
                assertEquals(2, item.whitelistUserCount)
                assertEquals(Instant.parse(time), item.createdAt)
            }
            assertEquals(listOf<String?>("start", "next", "start", "next"), cursors)
            assertTrue(manager.list("start", 3, false).asFlow().toList().isEmpty())
            assertEquals(5, cursors.size)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun recordsOperateOnSnapshotIdAndPreserveSnapshot() = runTest {
        val paths = mutableListOf<String>()
        val app = app { request ->
            paths += request.url.encodedPath
            if (request.method == HttpMethod.Get) respondOk("""{"strategies":[${record("listed")}],"next_cursor":""}""")
            else if (request.method == HttpMethod.Patch) respondOk("""{"is_enable":"off","expire_at":"$time"}""")
            else respondOk()
        }
        val bot = app.bot()
        try {
            val item = bot.groupJoinApprovalStrategies.list().asFlow().toList().single()
            item.update { isEnable = false }
            item.execute()
            item.delete()
            assertTrue(item.isEnable)
            assertEquals(listOf(path, "$path/listed", "$path/listed/execute", "$path/listed"), paths)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun repeatedCursorsAreForwardedAndCollectionCanStopEarly() = runTest {
        var requests = 0
        val app = app { request ->
            assertEquals("same", request.url.parameters["cursor"])
            requests++
            respondOk("""{"strategies":[${record("listed")}],"next_cursor":"same"}""")
        }
        val bot = app.bot()
        try {
            val records = bot.groupJoinApprovalStrategies.list("same").asFlow().take(2).toList()
            assertEquals(2, records.size)
            assertEquals(2, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun failuresAndCancellationFollowDeleteOptions() = runTest {
        var cancel = false
        val app = app {
            if (cancel) throw CancellationException("cancelled")
            respond("""{"code":11253,"message":"no permission"}""", HttpStatusCode.Forbidden)
        }
        val bot = app.bot()
        try {
            val manager = bot.groupJoinApprovalStrategies
            assertFailsWith<IllegalArgumentException> { manager.handle(" ".ID) }
            val handle = manager.handle("known".ID)
            assertFailsWith<QQGuildApiException> { handle.execute() }
            assertFailsWith<QQGuildApiException> { handle.delete() }
            handle.delete(StandardDeleteOption.IGNORE_ON_FAILURE)
            assertFailsWith<QQGuildApiException> { manager.list().asFlow().toList() }
            cancel = true
            assertFailsWith<CancellationException> { handle.delete(StandardDeleteOption.IGNORE_ON_FAILURE) }
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
