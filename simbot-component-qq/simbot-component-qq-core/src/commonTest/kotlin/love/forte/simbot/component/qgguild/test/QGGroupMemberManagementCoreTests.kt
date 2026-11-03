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
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.group.QGGroupMemberWithInfo
import love.forte.simbot.component.qguild.group.QGGroupRole
import love.forte.simbot.qguild.QQGuildApiException
import kotlin.test.*
import kotlin.time.Instant

/** 群成员资料、列表、批量移除及黑名单操作的组件对接测试。 */
class QGGroupMemberManagementCoreTests : AbstractInteractionTests() {
    private fun member(id: String = "member", name: String = "成员"): String =
        """{"member_openid":"$id","username":"$name","member_role":"admin","bot":false,"joined_at":"2026-08-20T09:15:00+08:00","union_openid":"union"}"""
    private val blocked = """{"member_openid":"blocked","username":"用户","banned_at":"2026-08-20T09:15:00+08:00","bot":false}"""

    @Test
    fun queriesProduceIndependentSnapshotsAndReuseMemberMuteOperations() = runTest {
        var queries = 0
        val paths = mutableListOf<String>()
        val app = app { request ->
            paths += request.url.encodedPath
            when {
                request.url.encodedPath.endsWith("/info") -> respondOk("""{"group_openid":"group","group_name":"群","group_finger_memo":"","group_class_text":"","group_tags":[],"group_member_num":2}""")
                request.url.encodedPath.endsWith("/restrict_chat_setting") -> respondOk("{}")
                else -> respondOk(member(name = "name-${++queries}"))
            }
        }
        val bot = app.bot()
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val result = assertIs<QGGroupMemberWithInfo>(group.member("member".ID))
            assertEquals("member", result.id.toString())
            assertEquals("group", result.groupId.toString())
            assertEquals("name-1", result.name)
            assertEquals(QGGroupRole.ADMIN, result.memberRole)
            assertEquals("union", result.unionOpenid)
            assertFalse(result.isBot)
            assertEquals(Instant.parse("2026-08-20T01:15:00Z").toEpochMilliseconds(), result.source.joinedAt.toEpochMilliseconds())
            assertEquals("name-2", group.memberInfo("member".ID).username)
            assertEquals("name-3", group.memberWithInfo("member".ID).name)
            assertEquals("name-4", bot.groupRelation.memberInfo(group.id, result.id).username)
            assertEquals("name-5", bot.groupRelation.memberWithInfo(group.id, result.id).name)
            val withInfo = group.includeInfo()
            assertEquals("name-6", withInfo.memberWithInfo(result.id).name)
            result.unmute()
            assertEquals("/v2/groups/group/restrict_chat_setting", paths.last())
            assertEquals("name-1", result.name)
        } finally { app.cancel(); bot.cancel() }
    }

    @Test
    fun memberAndBlacklistCollectionsAreLazyAndFollowEmptyPages() = runTest {
        val requests = mutableListOf<String>()
        val app = app { request ->
            val path = request.url.encodedPath
            requests += path
            val next = request.url.parameters["cursor"]
            if (path.endsWith("/members")) {
                respondOk(if (next == null) """{"members":[],"next_cursor":"next"}""" else """{"members":[${member()}],"next_cursor":""}""")
            } else {
                respondOk(if (next == null) """{"users":[],"next_cursor":"next"}""" else """{"users":[$blocked],"next_cursor":""}""")
            }
        }
        val bot = app.bot()
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val members = group.members
            val blacklist = group.blacklist
            assertTrue(requests.isEmpty())
            repeat(2) {
                assertIs<QGGroupMemberWithInfo>(members.asFlow().toList().single())
                assertEquals("blocked", blacklist.asFlow().toList().single().memberOpenid)
            }
            assertEquals(8, requests.size)
            assertEquals("group".ID, bot.groupRelation.members(group.id).asFlow().toList().single().groupId)
            assertEquals("blocked", bot.groupRelation.blacklist(group.id).asFlow().toList().single().memberOpenid)
            assertEquals(12, requests.size)
        } finally { app.cancel(); bot.cancel() }
    }

    @Test
    fun collectionCancellationStopsFurtherRequests() = runTest {
        var requests = 0
        val app = app { request ->
            requests++
            if (request.url.encodedPath.endsWith("/members")) respondOk("""{"members":[${member()}],"next_cursor":"next"}""")
            else respondOk("""{"users":[$blocked],"next_cursor":"next"}""")
        }
        val bot = app.bot()
        try {
            val group = bot.groupRelation.group("group".ID)!!
            group.members.asFlow().take(1).toList()
            group.blacklist.asFlow().take(1).toList()
            assertEquals(2, requests)
        } finally { app.cancel(); bot.cancel() }
    }

    @Test
    fun batchOperationsPreserveResultsAndDelegateUpperLimitsToPlatform() = runTest {
        val bodies = mutableListOf<JsonObject>()
        val app = app { request ->
            assertEquals(HttpMethod.Post, request.method)
            bodies += Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            if (request.url.encodedPath.endsWith("/batch_remove_members")) {
                respondOk("""{"remove_members_result":"success","add_to_member_blacklist_fail_openids":["member"]}""")
            } else respondOk("""{"fail_openids":["member"]}""")
        }
        val bot = app.bot()
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val ids = listOf("member".ID, "other".ID, "member".ID)
            val removal = group.removeMembers(ids, true)
            assertEquals("success", removal.removeMembersResult)
            assertEquals(listOf("member"), removal.addToMemberBlacklistFailOpenids)
            assertEquals(JsonPrimitive(true), bodies.last()["add_to_member_blacklist"])
            assertEquals(listOf("member", "other", "member"), bodies.last().getValue("member_openids").jsonArray.map { it.jsonPrimitive.content })
            bot.groupRelation.removeMembers(group.id, ids)
            assertEquals(JsonPrimitive(false), bodies.last()["add_to_member_blacklist"])
            assertEquals(listOf("member"), group.addToBlacklist(ids).failOpenids)
            assertEquals(JsonPrimitive("add"), bodies.last()["op"])
            group.removeFromBlacklist(ids)
            assertEquals(JsonPrimitive("del"), bodies.last()["op"])
            bot.groupRelation.addToBlacklist(group.id, ids)
            bot.groupRelation.removeFromBlacklist(group.id, ids)
            val large = List(21) { "member".ID }
            group.removeMembers(large)
            group.addToBlacklist(large)
            assertEquals(21, bodies.last().getValue("member_openids").jsonArray.size)
            assertEquals(8, bodies.size)
            assertFailsWith<IllegalArgumentException> { group.removeMembers(emptyList()) }
            assertFailsWith<IllegalArgumentException> { group.addToBlacklist(listOf(" ".ID)) }
            assertFailsWith<IllegalArgumentException> { bot.groupRelation.removeFromBlacklist(" ".ID, ids) }
            assertEquals(8, bodies.size)
        } finally { app.cancel(); bot.cancel() }
    }

    @Test
    fun platformErrorsAndMismatchedMemberIdentityArePropagated() = runTest {
        var mismatch = true
        val app = app {
            if (mismatch) respondOk(member("other"))
            else respond("""{"code":11253,"message":"no permission"}""", HttpStatusCode.Forbidden)
        }
        val bot = app.bot()
        try {
            val group = bot.groupRelation.group("group".ID)!!
            assertFailsWith<IllegalStateException> { group.member("member".ID) }
            mismatch = false
            assertEquals(11253, assertFailsWith<QQGuildApiException> { group.member("member".ID) }.info?.code)
            assertFailsWith<QQGuildApiException> { group.members.asFlow().toList() }
            assertFailsWith<QQGuildApiException> { group.blacklist.asFlow().toList() }
            assertFailsWith<QQGuildApiException> { group.removeMembers(listOf("member".ID)) }
            assertFailsWith<QQGuildApiException> { group.addToBlacklist(listOf("member".ID)) }
            assertFailsWith<IllegalArgumentException> { group.member(" ".ID) }
        } finally { app.cancel(); bot.cancel() }
    }
}
