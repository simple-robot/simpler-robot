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

import io.ktor.http.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.common.serialization.guessSerializer
import love.forte.simbot.qguild.api.group.*
import love.forte.simbot.qguild.common.QQ
import love.forte.simbot.qguild.model.group.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class GroupManagementApiTests {
    @Test
    fun routesAndPagination() {
        val group = "group-openid"
        val info = GetGroupInfoApi.create(group)
        val bot = GetGroupBotStateApi.create(group)
        val requests = GetGroupJoinRequestListApi.create(group, "next-cursor", 50)
        val mute = GetGroupMuteSettingApi.create(group)

        assertEquals(HttpMethod.Get, info.method)
        assertEquals("/v2/groups/$group/info", info.url.encodedPath)
        assertEquals("/v2/groups/$group/bot_state", bot.url.encodedPath)
        assertEquals("/v2/groups/$group/join_request_list", requests.url.encodedPath)
        assertEquals("next-cursor", requests.url.parameters["cursor"])
        assertEquals("50", requests.url.parameters["limit"])
        assertEquals("/v2/groups/$group/restrict_chat_setting", mute.url.encodedPath)
        assertNull(GetGroupJoinRequestListApi.create(group).url.parameters["cursor"])
        assertNull(GetGroupJoinRequestListApi.create(group).url.parameters["limit"])
    }

    @Test
    fun approvalAndMuteRequestBodies() {
        val approval = ApproveGroupJoinRequestApi.create(
            "group",
            "member",
            ApproveGroupJoinRequestOp.DECLINE,
            "request",
            "reason",
            true
        )
        assertEquals(HttpMethod.Post, approval.method)
        assertEquals("/v2/groups/group/approval_join_request/member", approval.url.encodedPath)
        val approvalJson = encodeBody(approval.body)
        assertEquals(JsonPrimitive("decline"), approvalJson["op"])
        assertEquals(JsonPrimitive("request"), approvalJson["join_request_id"])
        assertEquals(JsonPrimitive("reason"), approvalJson["reject_reason"])
        assertEquals(JsonPrimitive(true), approvalJson["add_to_member_blacklist"])
        val approveOnly = encodeBody(
            ApproveGroupJoinRequestApi.create("group", "member", ApproveGroupJoinRequestOp.APPROVE).body
        )
        assertNull(approveOnly["reject_reason"])
        assertNull(approveOnly["add_to_member_blacklist"])

        val mute = SetGroupMuteSettingApi.create(
            "group",
            SetMemberMuteState.of(SetGroupMuteSettingOp.DEL, "member")
        )
        assertEquals(HttpMethod.Post, mute.method)
        assertEquals("/v2/groups/group/restrict_chat_setting", mute.url.encodedPath)
        val item = encodeBody(mute.body).getValue("members").jsonArray.single().jsonObject
        assertEquals(JsonPrimitive("del"), item["op"])
        assertEquals(JsonPrimitive("member"), item["member_openid"])
        assertNull(item["mute_expire_at"])
    }

    @Test
    fun officialResponseShapes() {
        val json = QQ.DefaultJson
        val info = json.decodeFromString(
            GroupInfo.serializer(),
            """{"group_openid":"group","group_name":"读书会","group_finger_memo":"简介","group_class_text":"文化","group_tags":["阅读"],"group_member_num":256}"""
        )
        assertEquals("读书会", info.groupName)
        assertEquals(256, info.groupMemberNum)

        val state = json.decodeFromString(
            GroupBotState.serializer(),
            """{"member_openid":"bot","joined_at":"2025-06-15T14:30:00+08:00","allow_proactive_msg":false,"recv_msg_setting":"only_mention","member_role":"member"}"""
        )
        assertEquals("only_mention", state.recvMsgSetting)
        assertEquals(Instant.parse("2025-06-15T06:30:00Z"), state.joinedAt)

        val page = json.decodeFromString(
            GroupJoinRequestPage.serializer(),
            """{"list":[{"join_request_id":"request","member_openid":"member","username":"申请人","apply_at":"2026-08-05T14:19:09+08:00","apply_source":"self_apply","bot":false,"verify_info":{"method":"admin_review_qa","review_qa_list":[{"question":"问题","answer":"答案"}]}}],"next_cursor":"next"}"""
        )
        assertEquals("next", page.nextCursor)
        assertEquals(GroupJoinApplySource.SelfApply, page.list.single().applySource)
        assertEquals(GroupJoinVerifyMethod.AdminReviewQa, page.list.single().verifyInfo?.method)
        assertEquals(Instant.parse("2026-08-05T06:19:09Z"), page.list.single().applyAt)
        assertEquals("答案", page.list.single().verifyInfo?.reviewQaList?.single()?.answer)

        val setting = json.decodeFromString(
            GroupMuteSetting.serializer(),
            """{"global_rule":{"mode":"schedule","schedule_rules":[{"task_id":"once","start_at":"2026-07-22T10:44:00+08:00","end_at":"2026-07-22T11:44:00+08:00","enabled":false}],"recurring_rules":[{"task_id":"repeat","weekdays":[1,7],"start_time":"13:05","end_time":"14:05","enabled":true}]},"members":[{"member_openid":"member","mute_expire_at":"2026-08-05T11:23:04+08:00","username":"成员"}]}"""
        )
        assertEquals(GroupGlobalMuteMode.Schedule, setting.globalRule.mode)
        assertEquals(Instant.parse("2026-07-22T02:44:00Z"), setting.globalRule.scheduleRules.single().startAt)
        assertEquals(listOf(1, 7), setting.globalRule.recurringRules.single().weekdays)
        assertEquals("member", setting.members.single().memberOpenid)
        assertEquals(Instant.parse("2026-08-05T03:23:04Z"), setting.members.single().muteExpireAt)
    }

    @Test
    fun openValueClassesPreserveUnknownValues() {
        val json = QQ.DefaultJson
        val method = json.decodeFromString(GroupJoinVerifyMethod.serializer(), "\"future_method\"")
        val mode = json.decodeFromString(GroupGlobalMuteMode.serializer(), "\"future_mode\"")
        val source = json.decodeFromString(GroupJoinApplySource.serializer(), "\"future_source\"")
        assertEquals("future_method", method.value)
        assertEquals("future_mode", mode.value)
        assertEquals("future_source", source.value)
        assertEquals(JsonPrimitive("future_method"), json.parseToJsonElement(json.encodeToString(method)))
        assertEquals(JsonPrimitive("future_mode"), json.parseToJsonElement(json.encodeToString(mode)))
        assertEquals(JsonPrimitive("future_source"), json.parseToJsonElement(json.encodeToString(source)))
    }

    @Test
    fun joinRequestFlowUsesCursorEvenAfterEmptyPage() = runTest {
        val json = QQ.DefaultJson
        val cursors = mutableListOf<String?>()
        val records = GetGroupJoinRequestListApi.createFlow("group") {
            val cursor = url.parameters["cursor"]
            cursors += cursor
            val source = if (cursor == null) {
                """{"list":[],"next_cursor":"next"}"""
            } else {
                """{"list":[{"join_request_id":"request","member_openid":"member","username":"申请人","apply_at":"2026-08-05T14:19:09+08:00","apply_source":"self_apply","bot":false}],"next_cursor":""}"""
            }
            json.decodeFromString(GroupJoinRequestPage.serializer(), source)
        }.toList()

        assertEquals(listOf(null, "next"), cursors)
        assertEquals(listOf("request"), records.map { it.joinRequestId })
    }

    @Test
    fun joinRequestFlowRejectsRepeatedCursor() = runTest {
        assertFailsWith<IllegalStateException> {
            GetGroupJoinRequestListApi.createFlow("group") {
                QQ.DefaultJson.decodeFromString(
                    GroupJoinRequestPage.serializer(),
                    """{"list":[],"next_cursor":"repeat"}"""
                )
            }.toList()
        }
    }
}

private fun encodeBody(body: Any) =
    QQ.DefaultJson.parseToJsonElement(
        QQ.DefaultJson.encodeToString(guessSerializer(body, QQ.DefaultJson.serializersModule), body)
    ).jsonObject
