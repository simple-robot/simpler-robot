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
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import love.forte.simbot.common.id.LongID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.group.*
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGBotMemberImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupAuthorImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupMemberImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.GroupMessageCreate
import love.forte.simbot.qguild.model.group.GroupBotState
import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Instant

/**
 * 验证禁言入口的请求、批次边界、时间构造和异常语义。
 */
class QGGroupMuteCoreTests : AbstractInteractionTests() {
    private val now = Instant.parse("2026-10-11T00:00:00.123456789Z")
    private val expireAt = now + 1.hours
    private val setting = """{"global_rule":{"mode":"always"},"members":[{"member_openid":"member","mute_expire_at":"2026-10-11T01:00:00.123456789Z","username":"成员"}]}"""

    /**
     * 成员查询每次请求所属群；记录缺失不受全员禁言模式影响。
     */
    @Test
    fun queriesAreFreshAndMemberAbsenceIsDistinctFromGlobalMute() = runTest {
        val paths = mutableListOf<String>()
        val app = app { request ->
            assertEquals(HttpMethod.Get, request.method)
            paths += request.url.encodedPath
            respondOk(setting)
        }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!
            repeat(2) {
                assertEquals("always", group.muteSetting().globalRule.mode.value)
                assertEquals(expireAt, bot.groupRelation.muteSetting("group".ID).members.single().muteExpireAt)
                assertEquals(expireAt, QGGroupMemberImpl(bot, group.id, "member".ID).muteState()?.muteExpireAt)
                assertNull(QGGroupMemberImpl(bot, group.id, "other".ID).muteState())
            }
            assertEquals(List(8) { "/v2/groups/group/restrict_chat_setting" }, paths)
            assertNull(group.member("member".ID))
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 普通成员、消息作者与机器人成员使用自身所属群及群内 ID，均复用三个明确操作。
     */
    @Test
    fun everyMemberImplementationUsesItsOwnGroupAndMemberIdentity() = runTest {
        val bodies = mutableListOf<JsonObject>()
        val paths = mutableListOf<String>()
        var queries = 0
        val app = app { request ->
            if (request.method == HttpMethod.Get) {
                queries++
                return@app respondOk(setting)
            }
            assertEquals(HttpMethod.Post, request.method)
            paths += request.url.encodedPath
            bodies += Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            respond("", HttpStatusCode.NoContent)
        }
        val bot = app.bot() as QGBotImpl
        try {
            val author = Json.decodeFromString(
                GroupMessageCreate.Author.serializer(), """{"member_openid":"member","member_role":"admin","username":"作者"}"""
            )
            val state = Json.decodeFromString(
                GroupBotState.serializer(),
                """{"member_openid":"member","joined_at":"2026-10-01T00:00:00Z","allow_proactive_msg":false,"recv_msg_setting":"only_mention","member_role":"admin"}"""
            )
            val members = listOf(
                QGGroupMemberImpl(bot, "group-0".ID, "member".ID),
                QGGroupAuthorImpl(bot, "group-1".ID, author),
                QGBotMemberImpl(bot, "group-2".ID, state),
            )
            members.forEach { member ->
                member.addMute(expireAt)
                member.updateMute(expireAt)
                member.unmute()
                member.addMuteAtMilliseconds(expireAt.toEpochMilliseconds())
                member.updateMuteAtMilliseconds(expireAt.toEpochMilliseconds())
                member.addMuteFor(1.hours, now)
                member.updateMuteFor(1.hours, now)
            }
            assertEquals(21, bodies.size)
            members.forEachIndexed { index, _ ->
                assertEquals(List(7) { "/v2/groups/group-$index/restrict_chat_setting" }, paths.drop(index * 7).take(7))
                val items = bodies.drop(index * 7).take(7).map { it.getValue("members").jsonArray.single().jsonObject }
                assertEquals(listOf("add", "update", "del", "add", "update", "add", "update"), items.map { it.getValue("op").jsonPrimitive.content })
                items.forEach { assertEquals("member", it.getValue("member_openid").jsonPrimitive.content) }
                assertEquals("", items[2].getValue("mute_expire_at").jsonPrimitive.content)
                assertEquals(expireAt.toString(), items[0].getValue("mute_expire_at").jsonPrimitive.content)
            }
            members.forEach { assertEquals(expireAt, it.muteState()?.muteExpireAt) }
            assertEquals(3, queries)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 富化群的禁言查询和提交仍委托原群句柄，已有信息快照不引入额外查询。
     */
    @Test
    fun enrichedGroupRetainsMuteOperations() = runTest {
        val paths = mutableListOf<String>()
        val app = app { request ->
            paths += request.url.encodedPath
            when {
                request.url.encodedPath.endsWith("/info") ->
                    respondOk("""{"group_openid":"group","group_name":"群","group_finger_memo":"","group_class_text":"","group_tags":[],"group_member_num":1}""")
                request.method == HttpMethod.Get -> respondOk(setting)
                else -> respondOk("{}")
            }
        }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!.includeInfo()
            assertEquals("always", group.muteSetting().globalRule.mode.value)
            group.addMemberMute("member".ID, expireAt)
            group.updateMemberMute("member".ID, expireAt)
            group.unmuteMember("member".ID)
            assertEquals(listOf("/v2/groups/group/info") + List(4) { "/v2/groups/group/restrict_chat_setting" }, paths)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 群和群关系的单成员便利入口各发一次请求；混合批次保留各操作。
     */
    @Test
    fun groupAndRelationHelpersShareBatchSubmission() = runTest {
        val batches = mutableListOf<JsonArray>()
        val app = app { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/v2/groups/group/restrict_chat_setting", request.url.encodedPath)
            batches += Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject.getValue("members").jsonArray
            respondOk("{}")
        }
        val bot = app.bot() as QGBotImpl
        try {
            val relation = bot.groupRelation
            val group = relation.group("group".ID)!!
            group.addMemberMute("member".ID, expireAt)
            group.updateMemberMute("member".ID, expireAt)
            group.unmuteMember("member".ID)
            group.addMemberMuteAtMilliseconds("member".ID, expireAt.toEpochMilliseconds())
            group.updateMemberMuteAtMilliseconds("member".ID, expireAt.toEpochMilliseconds())
            group.addMemberMuteFor("member".ID, 1.hours, now)
            group.updateMemberMuteFor("member".ID, 1.hours, now)
            relation.addMemberMute(group.id, "member".ID, expireAt)
            relation.updateMemberMute(group.id, "member".ID, expireAt)
            relation.unmuteMember(group.id, "member".ID)
            relation.addMemberMuteAtMilliseconds(group.id, "member".ID, expireAt.toEpochMilliseconds())
            relation.updateMemberMuteAtMilliseconds(group.id, "member".ID, expireAt.toEpochMilliseconds())
            relation.addMemberMuteFor(group.id, "member".ID, 1.hours, now)
            relation.updateMemberMuteFor(group.id, "member".ID, 1.hours, now)
            val changes = listOf(
                QGGroupMemberMuteChange.add("a".ID, expireAt),
                QGGroupMemberMuteChange.update("b".ID, expireAt),
                QGGroupMemberMuteChange.remove("c".ID),
            )
            group.setMemberMutes(changes)
            relation.setMemberMutes(group.id, changes)
            assertEquals(16, batches.size)
            assertTrue(batches.take(14).all { it.size == 1 })
            assertEquals(List(2) { listOf("add", "update", "del") }, batches.takeLast(2).map { batch ->
                batch.map { it.jsonObject.getValue("op").jsonPrimitive.content }
            })
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 整个非法批次在请求前失败，跨 ID 类型按 literal 判断重复。
     */
    @Test
    fun invalidInputsNeverSendRequests() = runTest {
        var requests = 0
        val app = app { requests++; respondOk("{}") }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val invalid = listOf(
                emptyList(),
                List(21) { QGGroupMemberMuteChange.remove("member-$it".ID) },
                listOf(QGGroupMemberMuteChange.remove("".ID)),
                listOf(QGGroupMemberMuteChange.remove(" \t".ID)),
                listOf(QGGroupMemberMuteChange.add("same".ID, expireAt), QGGroupMemberMuteChange.remove("same".ID)),
                listOf(QGGroupMemberMuteChange.remove("42".ID), QGGroupMemberMuteChange.remove(42L.ID)),
            )
            invalid.forEach { changes ->
                assertFailsWith<IllegalArgumentException> { group.setMemberMutes(changes) }
                assertFailsWith<IllegalArgumentException> { bot.groupRelation.setMemberMutes(group.id, changes) }
            }
            assertFailsWith<IllegalArgumentException> {
                bot.groupRelation.setMemberMutes(" ".ID, listOf(QGGroupMemberMuteChange.remove("member".ID)))
            }
            assertFailsWith<IllegalArgumentException> { bot.groupRelation.muteSetting("".ID) }
            assertFailsWith<IllegalArgumentException> { QGGroupMemberImpl(bot, group.id, " ".ID).muteState() }
            assertEquals(0, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 20 项被一次接受，发送阶段更改源列表不会改变已固定的请求体。
     */
    @Test
    fun twentyChangesUseOneStableRequestSnapshot() = runTest {
        val changes = MutableList<QGGroupMemberMuteChange>(20) { QGGroupMemberMuteChange.remove("member-$it".ID) }
        var requests = 0
        val app = app { request ->
            requests++
            changes.clear()
            val members = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject.getValue("members").jsonArray
            assertEquals(20, members.size)
            assertEquals("member-19", members.last().jsonObject.getValue("member_openid").jsonPrimitive.content)
            respondOk("{}")
        }
        val bot = app.bot() as QGBotImpl
        try {
            bot.groupRelation.setMemberMutes("group".ID, changes)
            assertTrue(changes.isEmpty())
            assertEquals(1, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 所有入口透传平台错误；设置失败不预查询、不重试。
     */
    @Test
    fun platformFailuresPropagateWithoutQueriesOrRetries() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val app = app { request ->
            methods += request.method
            respond("""{"code":11253,"message":"permission denied"}""", HttpStatusCode.Forbidden)
        }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val member = QGGroupMemberImpl(bot, group.id, "member".ID)
            val calls: List<suspend () -> Unit> = listOf(
                { group.muteSetting() },
                { bot.groupRelation.muteSetting(group.id) },
                { member.muteState() },
                { member.addMute(expireAt) },
                { member.updateMute(expireAt) },
                { member.unmute() },
                { group.addMemberMute("member".ID, expireAt) },
                { bot.groupRelation.unmuteMember(group.id, "member".ID) },
            )
            calls.forEach { call ->
                assertEquals(11253, assertFailsWith<QQGuildApiException> { call() }.info?.code)
            }
            assertEquals(List(3) { HttpMethod.Get } + List(5) { HttpMethod.Post }, methods)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 网络异常保留类型和信息，查询不能将失败解释为无禁言记录。
     */
    @Test
    fun networkFailuresAreNotConvertedToAbsence() = runTest {
        val failure = IllegalStateException("network failure")
        var requests = 0
        val app = app { requests++; throw failure }
        val bot = app.bot() as QGBotImpl
        try {
            val member = QGGroupMemberImpl(bot, "group".ID, "member".ID)
            assertEquals(failure.message, assertFailsWith<IllegalStateException> { member.muteState() }.message)
            assertEquals(failure.message, assertFailsWith<IllegalStateException> { member.unmute() }.message)
            assertEquals(2, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 工厂保留到期时间的精度，重用变更不会改变时间。
     */
    @Test
    fun expirationFactoriesPreserveTimeAndReusedChange() = runTest {
        assertEquals(expireAt, QGGroupMemberMuteChange.add("member".ID, expireAt).muteExpireAt)
        assertEquals(expireAt, QGGroupMemberMuteChange.update("member".ID, expireAt).muteExpireAt)
        val millis = expireAt.toEpochMilliseconds()
        assertEquals(millis, QGGroupMemberMuteChange.addAtMilliseconds("member".ID, millis).muteExpireAt.toEpochMilliseconds())
        assertEquals(millis, QGGroupMemberMuteChange.updateAtMilliseconds("member".ID, millis).muteExpireAt.toEpochMilliseconds())
        val change = QGGroupMemberMuteChange.add("member".ID, expireAt)
        val times = mutableListOf<String>()
        val app = app { request ->
            val member = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject.getValue("members").jsonArray.single().jsonObject
            times += member.getValue("mute_expire_at").jsonPrimitive.content
            respondOk("{}")
        }
        val bot = app.bot() as QGBotImpl
        try {
            repeat(2) { bot.groupRelation.setMemberMutes("group".ID, listOf(change)) }
            assertEquals(List(2) { expireAt.toString() }, times)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    /**
     * 无效时长和超出范围的到期时间均被拒绝，精确抵达上界仍可提交。
     */
    @Test
    fun durationValidationRejectsInfinityNonPositiveAndClamping() = runTest {
        val maximum = Instant.fromEpochSeconds(Long.MAX_VALUE)
        val invalid = listOf(Duration.ZERO, -1.hours, Duration.INFINITE, -Duration.INFINITE)
        var requests = 0
        var requestedExpireAt: String? = null
        val app = app { request ->
            requests++
            val member = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject.getValue("members").jsonArray.single().jsonObject
            requestedExpireAt = member.getValue("mute_expire_at").jsonPrimitive.content
            respondOk("{}")
        }
        val bot = app.bot() as QGBotImpl
        try {
            val member = QGGroupMemberImpl(bot, "group".ID, "member".ID)
            val group = bot.groupRelation.group(member.groupId)!!
            invalid.forEach { duration ->
                assertFailsWith<IllegalArgumentException> { member.addMuteFor(duration, now) }
                assertFailsWith<IllegalArgumentException> { member.updateMuteFor(duration, now) }
                assertFailsWith<IllegalArgumentException> { group.addMemberMuteFor(member.id, duration, now) }
                assertFailsWith<IllegalArgumentException> { group.updateMemberMuteFor(member.id, duration, now) }
                assertFailsWith<IllegalArgumentException> { bot.groupRelation.addMemberMuteFor(group.id, member.id, duration, now) }
                assertFailsWith<IllegalArgumentException> { bot.groupRelation.updateMemberMuteFor(group.id, member.id, duration, now) }
            }
            assertFailsWith<IllegalArgumentException> { member.addMuteFor(1.nanoseconds, maximum) }
            assertFailsWith<IllegalArgumentException> {
                member.addMuteFor(2.nanoseconds, maximum - 1.nanoseconds)
            }
            assertEquals(0, requests)
            member.addMuteFor(1.nanoseconds, maximum - 1.nanoseconds)
            assertEquals(1, requests)
            assertEquals(maximum.toString(), requestedExpireAt)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
