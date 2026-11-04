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
import love.forte.simbot.qguild.model.group.*
import kotlin.test.*
import kotlin.time.Instant

class GroupJoinApprovalStrategyApiTests {
    private val json = QQ.DefaultJson
    private val timestamp = "2026-08-05T15:30:16.123456789+08:00"
    private val state = """{"is_enable":"off","expire_at":"$timestamp"}"""
    private val created = """{"strategy_id":"strategy","is_enable":"on","expire_at":"$timestamp"}"""
    private val whitelist = """{"strategy_id":"strategy","whitelist_user_count":2,"updated_at":"$timestamp"}"""
    private val strategy = """{"strategy_id":"strategy","group_ids":["10123499"],"whitelist_user_count":2,"is_enable":"on","expire_at":"$timestamp","created_at":"$timestamp","updated_at":"$timestamp"}"""
    private val path = "/v2/groups/join_approval_strategy"

    @Test
    fun methodsRoutesAndQueries() {
        val list = GetGroupJoinApprovalStrategyListApi.create("a+b&c/=?", 51)
        assertEquals(HttpMethod.Get, list.method)
        assertEquals(path, list.url.encodedPath)
        assertEquals("a+b&c/=?", list.url.parameters["cursor"])
        assertEquals("51", list.url.parameters["limit"])
        assertNull(list.body)
        assertNull(GetGroupJoinApprovalStrategyListApi.create().url.parameters["cursor"])
        assertEquals("", GetGroupJoinApprovalStrategyListApi.create("").url.parameters["cursor"])
        val create = CreateGroupJoinApprovalStrategyApi.create { addGroupOpenid("group") }
        val modify = ModifyGroupJoinApprovalStrategyApi.create("strategy") { isEnable = false }
        val delete = DeleteGroupJoinApprovalStrategyApi.create("strategy")
        val execute = ExecuteGroupJoinApprovalStrategyApi.create("strategy")
        val users = ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.ADD, listOf("1234567"))
        assertEquals(path, create.url.encodedPath)
        assertEquals(HttpMethod.Post, create.method)
        assertEquals("$path/strategy", modify.url.encodedPath)
        assertEquals(HttpMethod.Patch, modify.method)
        assertEquals("$path/strategy", delete.url.encodedPath)
        assertEquals(HttpMethod.Delete, delete.method)
        assertEquals("$path/strategy/execute", execute.url.encodedPath)
        assertEquals(HttpMethod.Post, execute.method)
        assertEquals("$path/strategy/whitelist_users", users.url.encodedPath)
        assertEquals(HttpMethod.Post, users.method)
    }

    @Test
    fun optionalFieldsAreOmittedAndExplicitFalseIsSentWithAnyJsonConfiguration() {
        for (defaults in listOf(false, true)) for (nulls in listOf(false, true)) {
            val encoder = Json { encodeDefaults = defaults; explicitNulls = nulls }
            val create = CreateGroupJoinApprovalStrategyApi.create { addGroupId("9223372036854775807") }
            val body = encodeBody(create.body, encoder)
            assertEquals(setOf("group_ids"), body.keys)
            assertEquals(JsonPrimitive("9223372036854775807"), body.getValue("group_ids").jsonArray.single())
            val modify = ModifyGroupJoinApprovalStrategyApi.create("strategy") {
                isEnable = false
                remark = ""
                expireAt = Instant.parse(timestamp)
            }
            val update = encodeBody(modify.body, encoder)
            assertEquals(setOf("is_enable", "remark", "expire_at"), update.keys)
            assertEquals(JsonPrimitive("off"), update["is_enable"])
            assertEquals(JsonPrimitive(""), update["remark"])
            assertEquals(Instant.parse(timestamp), Instant.parse(update.getValue("expire_at").jsonPrimitive.content))
            assertEquals(JsonPrimitive("on"), encodeBody(CreateGroupJoinApprovalStrategyApi.create {
                addGroupOpenid("group")
                isEnable = true
            }.body, encoder)["is_enable"])
            val action = ModifyGroupJoinApprovalStrategyApi.create("strategy") {
                groupAction {
                    op = GroupJoinApprovalStrategyOp.DEL
                    addGroupId("123456789")
                }
            }
            val actionBody = encodeBody(action.body, encoder).getValue("group_action").jsonObject
            assertEquals(setOf("op", "group_ids"), actionBody.keys)
            assertEquals(JsonPrimitive("del"), actionBody["op"])
        }
    }

    @Test
    fun collectionsAreSnapshotsAndUpperLimitsAreNotEnforcedLocally() {
        val input = mutableListOf("b", "a", "b")
        val builder = GroupJoinApprovalStrategyCreate.builder().addGroupOpenids(input)
        val request = CreateGroupJoinApprovalStrategyApi.create(builder.build())
        val users = ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.ADD, input)
        val actionBuilder = GroupJoinApprovalStrategyGroupAction.builder().op(GroupJoinApprovalStrategyOp.ADD).addGroupIds(listOf("2", "1", "2"))
        val action = actionBuilder.build()
        actionBuilder.clearGroupIds()
        input.clear()
        builder.clearGroupOpenids()
        assertEquals(listOf("b", "a", "b"), request.body.groupOpenids)
        assertEquals(listOf("2", "1", "2"), action.groupIds)
        assertEquals(JsonArray(listOf("b", "a", "b").map(::JsonPrimitive)), encodeBody(users.body)["whitelist_users"])
        val manyGroups = CreateGroupJoinApprovalStrategyApi.create {
            addGroupOpenids(List(101) { "group-$it" })
            remark = "备".repeat(256)
        }
        assertEquals(101, manyGroups.body.groupOpenids!!.size)
        assertEquals(10001, encodeBody(ModifyGroupJoinApprovalStrategyWhitelistApi.create(
            "strategy", GroupJoinApprovalStrategyOp.DEL, List(10001) { "$it" }
        ).body).getValue("whitelist_users").jsonArray.size)
    }

    @Test
    fun groupIdsArePassedThroughAsStrings() {
        val ids = listOf("00123456", "arbitrary-id", "18446744073709551616")
        val create = CreateGroupJoinApprovalStrategyApi.create { addGroupIds(ids) }
        val action = GroupJoinApprovalStrategyGroupAction {
            op = GroupJoinApprovalStrategyOp.ADD
            addGroupIds(ids)
        }
        val modify = ModifyGroupJoinApprovalStrategyApi.create("strategy") { groupAction = action }
        val expected = JsonArray(ids.map(::JsonPrimitive))
        assertEquals(expected, encodeBody(create.body)["group_ids"])
        assertEquals(expected, encodeBody(modify.body).getValue("group_action").jsonObject["group_ids"])
        for (invalid in listOf("", " ", "\t")) {
            assertFailsWith<IllegalArgumentException> {
                GroupJoinApprovalStrategyGroupAction {
                    op = GroupJoinApprovalStrategyOp.ADD
                    addGroupId(invalid)
                }
            }
        }
    }

    @Test
    fun invalidLowerBoundsAndFieldCombinationsAreRejected() {
        assertFailsWith<IllegalArgumentException> { GroupJoinApprovalStrategyCreate.builder().build() }
        assertFailsWith<IllegalArgumentException> { CreateGroupJoinApprovalStrategyApi.create { addGroupOpenid(" ") } }
        assertFailsWith<IllegalArgumentException> { CreateGroupJoinApprovalStrategyApi.create { addGroupOpenid("group"); addGroupId("1") } }
        assertFailsWith<IllegalArgumentException> { CreateGroupJoinApprovalStrategyApi.create { addGroupId("") } }
        assertFailsWith<IllegalArgumentException> { CreateGroupJoinApprovalStrategyApi.create { addGroupId(" ") } }
        assertFailsWith<IllegalArgumentException> { GroupJoinApprovalStrategyModify.builder().build() }
        assertFailsWith<IllegalArgumentException> { GroupJoinApprovalStrategyGroupAction.builder().addGroupId("1").build() }
        assertFailsWith<IllegalArgumentException> { GroupJoinApprovalStrategyGroupAction.builder().op(GroupJoinApprovalStrategyOp.ADD).build() }
        assertFailsWith<IllegalArgumentException> { ModifyGroupJoinApprovalStrategyApi.create(" ") { isEnable = false } }
        assertFailsWith<IllegalArgumentException> { DeleteGroupJoinApprovalStrategyApi.create(" ") }
        assertFailsWith<IllegalArgumentException> { ExecuteGroupJoinApprovalStrategyApi.create(" ") }
        assertFailsWith<IllegalArgumentException> { GetGroupJoinApprovalStrategyListApi.create(limit = 0) }
        assertFailsWith<IllegalArgumentException> { ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.ADD, emptyList()) }
        assertFailsWith<IllegalArgumentException> { ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.DEL, listOf(" ")) }
        assertFailsWith<IllegalArgumentException> { ModifyGroupJoinApprovalStrategyWhitelistApi.create(" ", GroupJoinApprovalStrategyOp.ADD, listOf("1")) }
        assertFailsWith<IllegalArgumentException> {
            json.decodeFromString(GroupJoinApprovalStrategyCreate.serializer(), """{"group_ids":[]}""")
        }
    }

    @Test
    fun officialResponseShapesAndTimestampPrecision() {
        val strict = Json { ignoreUnknownKeys = true }
        val list = strict.decodeFromString(GroupJoinApprovalStrategyPage.serializer(), """{"strategies":[$strategy],"next_cursor":""}""")
        val item = list.strategies.single()
        assertEquals(listOf("10123499"), item.groupIds)
        assertNull(item.groupOpenids)
        assertNull(item.remark)
        assertTrue(item.isEnable)
        assertEquals(Instant.parse(timestamp), item.expireAt)
        assertEquals(item.expireAt, item.createdAt)
        assertEquals(item.createdAt, item.updatedAt)
        assertEquals(
            JsonArray(listOf(JsonPrimitive("10123499"))),
            strict.encodeToJsonElement(GroupJoinApprovalStrategy.serializer(), item).jsonObject["group_ids"]
        )
        assertTrue(strict.decodeFromString(GroupJoinApprovalStrategyCreated.serializer(), created).isEnable)
        assertFalse(strict.decodeFromString(GroupJoinApprovalStrategyModified.serializer(), state).isEnable)
        assertEquals(2, strict.decodeFromString(GroupJoinApprovalStrategyWhitelistModified.serializer(), whitelist).whitelistUserCount)
        assertFailsWith<SerializationException> { strict.decodeFromString(GroupJoinApprovalStrategyModified.serializer(), state.replace("off", "future")) }
        assertFailsWith<SerializationException> { strict.decodeFromString(GroupJoinApprovalStrategyModified.serializer(), "{}") }
        assertFailsWith<IllegalArgumentException> { strict.decodeFromString(GroupJoinApprovalStrategyModified.serializer(), state.replace(timestamp, "")) }
    }

    @Test
    fun responseGroupIdentifiersPreserveMissingNullAndEmptyValues() {
        val strict = Json { ignoreUnknownKeys = true }
        val fields = strict.parseToJsonElement(strategy).jsonObject - "group_ids"
        fun decode(groups: Map<String, JsonElement>): GroupJoinApprovalStrategy =
            strict.decodeFromJsonElement(GroupJoinApprovalStrategy.serializer(), JsonObject(fields + groups))

        val missing = decode(emptyMap())
        assertNull(missing.groupOpenids)
        assertNull(missing.groupIds)

        val openids = decode(mapOf("group_openids" to JsonArray(listOf(JsonPrimitive("group")))))
        assertEquals(listOf("group"), openids.groupOpenids)
        assertNull(openids.groupIds)

        val nulls = decode(mapOf("group_openids" to JsonNull, "group_ids" to JsonNull))
        assertNull(nulls.groupOpenids)
        assertNull(nulls.groupIds)

        val empty = decode(mapOf("group_openids" to JsonArray(emptyList()), "group_ids" to JsonArray(emptyList())))
        assertEquals(emptyList(), empty.groupOpenids)
        assertEquals(emptyList(), empty.groupIds)
    }

    @Test
    fun paginationIsColdAndContinuesAfterEmptyPages() = runTest {
        val cursors = mutableListOf<String?>()
        val flow = GetGroupJoinApprovalStrategyListApi.createFlow("start", 37) {
            val cursor = url.parameters["cursor"]
            cursors += cursor
            assertEquals("37", url.parameters["limit"])
            if (cursor == "start") page("", "next") else page(strategy, "")
        }
        assertTrue(cursors.isEmpty())
        repeat(2) { assertEquals(listOf("strategy"), flow.toList().map { it.strategyId }) }
        assertEquals(listOf<String?>("start", "next", "start", "next"), cursors)
    }

    @Test
    fun paginationFollowsReturnedCursorAndPropagatesFailures() = runTest {
        val nextCursors = listOf("start", "other", "start", "")
        val requestedCursors = mutableListOf<String?>()
        GetGroupJoinApprovalStrategyListApi.createFlow("start") {
            requestedCursors += url.parameters["cursor"]
            page("", nextCursors[requestedCursors.lastIndex])
        }.toList()
        assertEquals(listOf<String?>("start", "start", "other", "start"), requestedCursors)
        val failure = IllegalStateException("request failed")
        assertSame(failure, assertFailsWith<IllegalStateException> {
            GetGroupJoinApprovalStrategyListApi.createFlow { throw failure }.toList()
        })
        var requests = 0
        GetGroupJoinApprovalStrategyListApi.createFlow {
            requests++
            page(strategy, "next")
        }.take(1).toList()
        assertEquals(1, requests)
    }

    @Test
    fun allSixApisUseTheRequestPipeline() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val client = HttpClient(MockEngine { request ->
            methods += request.method
            val content = when {
                request.method == HttpMethod.Get -> """{"strategies":[$strategy],"next_cursor":""}"""
                request.method == HttpMethod.Patch -> {
                    assertEquals(JsonPrimitive("off"), json.parseToJsonElement((request.body as TextContent).text).jsonObject["is_enable"])
                    state
                }
                request.url.encodedPath.endsWith("/whitelist_users") -> {
                    val body = json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals(JsonPrimitive("del"), body["op"])
                    assertEquals(JsonArray(listOf(JsonPrimitive("1234567"))), body["whitelist_users"])
                    whitelist
                }
                request.url.encodedPath.endsWith("/execute") -> "{}"
                request.method == HttpMethod.Delete -> ""
                else -> created
            }
            respond(content)
        })
        try {
            assertEquals("strategy", GetGroupJoinApprovalStrategyListApi.create().requestData(client, "test").strategies.single().strategyId)
            assertEquals("strategy", CreateGroupJoinApprovalStrategyApi.create { addGroupId("123") }.requestData(client, "test").strategyId)
            assertFalse(ModifyGroupJoinApprovalStrategyApi.create("strategy") { isEnable = false }.requestData(client, "test").isEnable)
            assertEquals(Unit, DeleteGroupJoinApprovalStrategyApi.create("strategy").requestData(client, "test"))
            assertEquals(Unit, ExecuteGroupJoinApprovalStrategyApi.create("strategy").requestData(client, "test"))
            assertEquals(2, ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.DEL, listOf("1234567")).requestData(client, "test").whitelistUserCount)
            assertEquals(listOf(HttpMethod.Get, HttpMethod.Post, HttpMethod.Patch, HttpMethod.Delete, HttpMethod.Post, HttpMethod.Post), methods)
        } finally {
            client.close()
        }
    }

    @Test
    fun permissionFailuresArePropagatedForEveryApi() = runTest {
        val client = HttpClient(MockEngine {
            respond("""{"code":11253,"message":"no permission"}""", HttpStatusCode.Forbidden)
        })
        try {
            val apis: List<QQGuildApi<*>> = listOf(
                GetGroupJoinApprovalStrategyListApi.create(),
                CreateGroupJoinApprovalStrategyApi.create { addGroupId("123") },
                ModifyGroupJoinApprovalStrategyApi.create("strategy") { isEnable = false },
                DeleteGroupJoinApprovalStrategyApi.create("strategy"),
                ExecuteGroupJoinApprovalStrategyApi.create("strategy"),
                ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.ADD, listOf("1234567"))
            )
            for (api in apis) {
                val error = assertFailsWith<QQGuildApiException> { api.requestData(client, "test") }
                assertEquals(11253, error.info?.code)
                assertEquals(403, error.value)
            }
        } finally {
            client.close()
        }
    }

    private fun page(items: String, cursor: String): GroupJoinApprovalStrategyPage =
        json.decodeFromString(GroupJoinApprovalStrategyPage.serializer(), """{"strategies":[$items],"next_cursor":"$cursor"}""")

    private fun encodeBody(body: Any, encoder: Json = json): JsonObject =
        encoder.parseToJsonElement(encoder.encodeToString(guessSerializer(body, encoder.serializersModule), body)).jsonObject
}
