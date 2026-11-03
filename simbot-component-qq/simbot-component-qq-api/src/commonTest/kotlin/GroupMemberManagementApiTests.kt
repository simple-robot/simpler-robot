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

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import love.forte.simbot.common.serialization.guessSerializer
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.group.*
import love.forte.simbot.qguild.api.requestData
import love.forte.simbot.qguild.common.QQ
import love.forte.simbot.qguild.event.GroupMessageAuthorRole
import love.forte.simbot.qguild.model.group.*
import kotlin.test.*
import kotlin.time.Instant

class GroupMemberManagementApiTests {
    private val json = QQ.DefaultJson
    private val memberJson =
        """{"member_openid":"member","username":"成员","member_role":"admin","bot":false,"joined_at":"2026-08-20T09:15:00.123456789+08:00"}"""
    private val blacklistUserJson =
        """{"member_openid":"blocked","username":"用户","banned_at":"2026-08-20T09:15:00+08:00","bot":true,"union_openid":"union"}"""

    @Test
    fun routesAndQueries() {
        val list = GetGroupMemberListApi.create("group", "a+b&c/=?")
        val member = GetGroupMemberInfoApi.create("group", "member")
        val remove = BatchRemoveGroupMembersApi.create("group", listOf("member"))
        val blacklist = GetGroupMemberBlacklistApi.create("group", "next", 100)
        val modify = ModifyGroupMemberBlacklistApi.create("group", ModifyGroupMemberBlacklistOp.ADD, listOf("member"))
        assertEquals(HttpMethod.Get, list.method)
        assertEquals(HttpMethod.Get, member.method)
        assertEquals(HttpMethod.Post, remove.method)
        assertEquals(HttpMethod.Get, blacklist.method)
        assertEquals(HttpMethod.Post, modify.method)
        assertEquals("/v2/groups/group/members", list.url.encodedPath)
        assertEquals("/v2/groups/group/members/member", member.url.encodedPath)
        assertEquals("/v2/groups/group/batch_remove_members", remove.url.encodedPath)
        assertEquals("/v2/groups/group/member_blacklist", blacklist.url.encodedPath)
        assertEquals(blacklist.url.encodedPath, modify.url.encodedPath)
        assertEquals("a+b&c/=?", list.url.parameters["cursor"])
        assertNull(list.url.parameters["limit"])
        assertEquals("next", blacklist.url.parameters["cursor"])
        assertEquals("100", blacklist.url.parameters["limit"])
        assertNull(GetGroupMemberListApi.create("group").url.parameters["cursor"])
        assertNull(GetGroupMemberBlacklistApi.create("group").url.parameters["limit"])
        assertEquals("", GetGroupMemberBlacklistApi.create("group", "").url.parameters["cursor"])
        assertNull(list.body)
        assertNull(member.body)
        assertNull(blacklist.body)
    }

    @Test
    fun memberAndBlacklistModelsPreserveProtocolValues() {
        val member = json.decodeFromString(GroupMemberInfo.serializer(), memberJson)
        assertEquals("member", member.memberOpenid)
        assertEquals("成员", member.username)
        assertEquals(GroupMessageAuthorRole.ADMIN, member.memberRole)
        assertEquals(Instant.parse("2026-08-20T01:15:00.123456789Z"), member.joinedAt)
        assertFalse(member.bot)
        assertNull(member.unionOpenid)
        val withUnion = json.decodeFromString(
            GroupMemberInfo.serializer(),
            memberJson.dropLast(1) + """, "union_openid":"union"}"""
        )
        assertEquals("union", withUnion.unionOpenid)
        for ((value, known) in listOf(
            "member" to GroupMessageAuthorRole.MEMBER,
            "owner" to GroupMessageAuthorRole.OWNER,
            "admin" to GroupMessageAuthorRole.ADMIN
        )) {
            val role = json.decodeFromString(GroupMessageAuthorRole.serializer(), "\"$value\"")
            assertEquals(known, role)
            assertEquals(JsonPrimitive(value), json.encodeToJsonElement(GroupMessageAuthorRole.serializer(), role))
        }
        val user = json.decodeFromString(GroupBlacklistUser.serializer(), blacklistUserJson)
        assertEquals("blocked", user.memberOpenid)
        assertEquals("union", user.unionOpenid)
        assertTrue(user.bot)
        assertEquals(Instant.parse("2026-08-20T01:15:00Z"), user.bannedAt)
        val withoutUnion = json.decodeFromString(
            GroupBlacklistUser.serializer(),
            blacklistUserJson.replace(",\"union_openid\":\"union\"", "")
        )
        assertNull(withoutUnion.unionOpenid)
    }

    @Test
    fun requiredResultFieldsCannotSilentlyBecomeSuccessOrEmptyResults() {
        assertFailsWith<SerializationException> { json.decodeFromString(GroupMembersRemoveResult.serializer(), "{}") }
        assertFailsWith<SerializationException> { json.decodeFromString(GroupBlacklistModifyResult.serializer(), "{}") }
        assertFailsWith<SerializationException> { json.decodeFromString(GroupMemberInfo.serializer(), "{}") }
        assertFailsWith<SerializationException> { json.decodeFromString(GroupMemberPage.serializer(), "{}") }
        assertFailsWith<SerializationException> { json.decodeFromString(GroupBlacklistPage.serializer(), "{}") }
        val result = json.decodeFromString(
            GroupMembersRemoveResult.serializer(),
            """{"remove_members_result":"future_status","add_to_member_blacklist_fail_openids":["member"]}"""
        )
        assertEquals("future_status", result.removeMembersResult)
        assertEquals(listOf("member"), result.addToMemberBlacklistFailOpenids)
    }

    @Test
    fun requestBodiesAreStableAndSnapshotInput() {
        val input = mutableListOf("b", "a", "b")
        val remove = BatchRemoveGroupMembersApi.create("group", input, true)
        val modify = ModifyGroupMemberBlacklistApi.create("group", ModifyGroupMemberBlacklistOp.DEL, input)
        input.clear()
        for (defaults in listOf(false, true)) for (nulls in listOf(false, true)) {
            val encoder = Json(json) { encodeDefaults = defaults; explicitNulls = nulls }
            val removal = encodeBody(remove.body, encoder)
            assertEquals(JsonArray(listOf("b", "a", "b").map(::JsonPrimitive)), removal["member_openids"])
            assertEquals(JsonPrimitive(true), removal["add_to_member_blacklist"])
            val update = encodeBody(modify.body, encoder)
            assertEquals(removal["member_openids"], update["member_openids"])
            assertEquals(JsonPrimitive("del"), update["op"])
            assertEquals(
                JsonPrimitive("add"),
                encodeBody(
                    ModifyGroupMemberBlacklistApi.create(
                        "group",
                        ModifyGroupMemberBlacklistOp.ADD,
                        listOf("a")
                    ).body, encoder
                )["op"]
            )
            assertEquals(
                JsonPrimitive(false),
                encodeBody(
                    BatchRemoveGroupMembersApi.create("group", listOf("a")).body,
                    encoder
                )["add_to_member_blacklist"]
            )
        }
    }

    @Test
    fun batchBoundariesAreCheckedWithoutSplitting() {
        val twenty = List(20) { "member-$it" }
        assertEquals(
            20,
            encodeBody(BatchRemoveGroupMembersApi.create("group", twenty).body)["member_openids"]!!.jsonArray.size
        )
        assertEquals(
            20,
            encodeBody(
                ModifyGroupMemberBlacklistApi.create(
                    "group",
                    ModifyGroupMemberBlacklistOp.ADD,
                    twenty
                ).body
            )["member_openids"]!!.jsonArray.size
        )
        assertFailsWith<IllegalArgumentException> { BatchRemoveGroupMembersApi.create(" ", twenty) }
        assertFailsWith<IllegalArgumentException> {
            ModifyGroupMemberBlacklistApi.create(
                " ",
                ModifyGroupMemberBlacklistOp.DEL,
                twenty
            )
        }
    }

    @Test
    fun memberFlowIsColdAndContinuesAfterEmptyPage() = runTest {
        val cursors = mutableListOf<String?>()
        val flow = GetGroupMemberListApi.createFlow("group") {
            val cursor = url.parameters["cursor"]
            cursors += cursor
            memberPage(if (cursor == null) "" else memberJson, if (cursor == null) "next" else "")
        }
        assertTrue(cursors.isEmpty())
        repeat(2) { assertEquals(listOf("member"), flow.toList().map { it.memberOpenid }) }
        assertEquals(listOf(null, "next", null, "next"), cursors)
    }

    @Test
    fun blacklistFlowIsColdAndPreservesLimitAndStartingCursor() = runTest {
        val cursors = mutableListOf<String?>()
        val flow = GetGroupMemberBlacklistApi.createFlow("group", "start", 37) {
            val cursor = url.parameters["cursor"]
            cursors += cursor
            assertEquals("37", url.parameters["limit"])
            blacklistPage(if (cursor == "start") "" else blacklistUserJson, if (cursor == "start") "next" else "")
        }
        assertTrue(cursors.isEmpty())
        repeat(2) { assertEquals(listOf("blocked"), flow.toList().map { it.memberOpenid }) }
        assertEquals(listOf<String?>("start", "next", "start", "next"), cursors)
    }

    @Test
    fun flowsRejectCyclicCursors() = runTest {
        var members = 0
        assertFailsWith<IllegalStateException> {
            GetGroupMemberListApi.createFlow("group") {
                members++
                memberPage("", "same")
            }.toList()
        }
        assertEquals(2, members)
        var blacklist = 0
        assertFailsWith<IllegalStateException> {
            GetGroupMemberBlacklistApi.createFlow("group", "a") {
                blacklist++
                blacklistPage("", if (url.parameters["cursor"] == "a") "b" else "a")
            }.toList()
        }
        assertEquals(2, blacklist)
    }

    @Test
    fun flowsStopWhenCollectionIsCancelled() = runTest {
        var members = 0
        GetGroupMemberListApi.createFlow("group") {
            members++
            memberPage(memberJson, "next")
        }.take(1).toList()
        var blacklist = 0
        GetGroupMemberBlacklistApi.createFlow("group") {
            blacklist++
            blacklistPage(blacklistUserJson, "next")
        }.take(1).toList()
        assertEquals(1, members)
        assertEquals(1, blacklist)
    }

    @Test
    fun flowsPropagateRequestFailures() = runTest {
        val failure = IllegalStateException("request failed")
        assertSame(
            failure,
            assertFailsWith<IllegalStateException> {
                GetGroupMemberListApi.createFlow("group") { throw failure }.toList()
            })
        assertSame(
            failure,
            assertFailsWith<IllegalStateException> {
                GetGroupMemberBlacklistApi.createFlow("group") { throw failure }.toList()
            })
    }

    @Test
    fun allApisUseExistingRequestPipelineAndReturnPartialFailures() = runTest {
        val paths = mutableListOf<String>()
        val client = HttpClient(MockEngine { request ->
            val path = request.url.encodedPath
            paths += path
            val content = when {
                path.endsWith("/members/member") -> memberJson
                path.endsWith("/members") -> """{"members":[$memberJson],"next_cursor":""}"""
                path.endsWith("/batch_remove_members") -> {
                    val body = json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals(JsonPrimitive(true), body["add_to_member_blacklist"])
                    """{"remove_members_result":"success","add_to_member_blacklist_fail_openids":["member"]}"""
                }

                request.method == HttpMethod.Get -> """{"users":[$blacklistUserJson],"next_cursor":""}"""
                else -> {
                    val body = json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals(JsonPrimitive("add"), body["op"])
                    """{"fail_openids":["member"]}"""
                }
            }
            respond(content)
        })
        try {
            assertEquals(
                "member",
                GetGroupMemberListApi.create("group").requestData(client, "test").members.single().memberOpenid
            )
            assertEquals(
                "member",
                GetGroupMemberInfoApi.create("group", "member").requestData(client, "test").memberOpenid
            )
            val removal = BatchRemoveGroupMembersApi.create("group", listOf("member"), true).requestData(client, "test")
            assertEquals("success", removal.removeMembersResult)
            assertEquals(listOf("member"), removal.addToMemberBlacklistFailOpenids)
            assertEquals(
                "blocked",
                GetGroupMemberBlacklistApi.create("group").requestData(client, "test").users.single().memberOpenid
            )
            assertEquals(
                listOf("member"),
                ModifyGroupMemberBlacklistApi.create("group", ModifyGroupMemberBlacklistOp.ADD, listOf("member"))
                    .requestData(client, "test").failOpenids
            )
            assertEquals(5, paths.size)
        } finally {
            client.close()
        }
    }

    @Test
    fun permissionErrorsAreNotConvertedToEmptyOrSuccessfulResults() = runTest {
        var requests = 0
        val client = HttpClient(MockEngine {
            requests++
            respond("""{"code":11253,"message":"no permission"}""", HttpStatusCode.Forbidden)
        })
        try {
            val apis: List<QQGuildApi<*>> = listOf(
                GetGroupMemberListApi.create("group"),
                GetGroupMemberInfoApi.create("group", "member"),
                BatchRemoveGroupMembersApi.create("group", listOf("member")),
                GetGroupMemberBlacklistApi.create("group"),
                ModifyGroupMemberBlacklistApi.create("group", ModifyGroupMemberBlacklistOp.DEL, listOf("member"))
            )
            for (api in apis) {
                val error = assertFailsWith<QQGuildApiException> { api.requestData(client, "test") }
                assertEquals(11253, error.info?.code)
                assertEquals(403, error.value)
            }
            assertEquals(5, requests)
        } finally {
            client.close()
        }
    }

    private fun memberPage(items: String, cursor: String): GroupMemberPage =
        json.decodeFromString(GroupMemberPage.serializer(), """{"members":[$items],"next_cursor":"$cursor"}""")

    private fun blacklistPage(items: String, cursor: String): GroupBlacklistPage =
        json.decodeFromString(GroupBlacklistPage.serializer(), """{"users":[$items],"next_cursor":"$cursor"}""")

    private fun encodeBody(body: Any, encoder: Json = json): JsonObject =
        encoder.parseToJsonElement(
            encoder.encodeToString(
                guessSerializer(body, encoder.serializersModule),
                body
            )
        ).jsonObject
}
