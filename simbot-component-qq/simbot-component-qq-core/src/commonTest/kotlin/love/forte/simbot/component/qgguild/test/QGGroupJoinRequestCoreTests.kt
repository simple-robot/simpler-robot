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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.group.QGGroupJoinRequestRejectOption
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 入群申请的 core 入口与审批请求测试。
 */
class QGGroupJoinRequestCoreTests : AbstractInteractionTests() {
    /**
     * 验证群句柄和群关系共用分页与审批行为。
     */
    @Test
    fun groupAndRelationJoinRequestOperations() = runTest {
        val approvalBodies = mutableListOf<JsonObject>()
        var listRequests = 0
        val app = app { request ->
            when {
                request.method == HttpMethod.Get &&
                    request.url.encodedPath == "/v2/groups/group/join_request_list" -> {
                    val cursor = request.url.parameters["cursor"]
                    assertEquals(if (listRequests % 2 == 0) null else "next", cursor)
                    listRequests++
                    if (cursor == null) {
                        respondOk("""{"list":[],"next_cursor":"next"}""")
                    } else {
                        respondOk(
                            """{"list":[{"join_request_id":"request","member_openid":"member","username":"申请人","apply_at":"2026-08-05T14:19:09+08:00","apply_source":"self_apply","bot":false}],"next_cursor":""}"""
                        )
                    }
                }

                request.method == HttpMethod.Post &&
                    request.url.encodedPath == "/v2/groups/group/approval_join_request/member" -> {
                    approvalBodies += Json.parseToJsonElement(
                        request.body.toByteArray().decodeToString()
                    ).jsonObject
                    respondOk()
                }

                else -> error("Unexpected request: ${request.method} ${request.url}")
            }
        }
        val bot = app.bot() as QGBotImpl
        val groupId = "group".ID
        val memberId = "member".ID
        val requestId = "request".ID

        try {
            val group = bot.groupRelation.group(groupId)!!
            val requests = group.joinRequests.asFlow()
            assertEquals(0, listRequests)

            val joinRequest = requests.toList().single()
            assertEquals(2, listRequests)
            assertEquals(requestId, joinRequest.joinRequestId)
            assertEquals(memberId, joinRequest.memberOpenid)
            assertEquals("申请人", joinRequest.username)

            assertEquals(
                listOf(requestId),
                bot.groupRelation.joinRequests(groupId).asFlow().toList().map { it.joinRequestId }
            )
            assertEquals(4, listRequests)

            joinRequest.accept()
            joinRequest.reject(
                QGGroupJoinRequestRejectOption.reason("first"),
                QGGroupJoinRequestRejectOption.reason("second"),
                QGGroupJoinRequestRejectOption.addToBlacklist(),
            )
            bot.groupRelation.approveJoinRequest(groupId, memberId, requestId)
            bot.groupRelation.rejectJoinRequest(
                groupId, memberId, requestId, QGGroupJoinRequestRejectOption.reason("relation")
            )

            assertEquals(listOf("approve", "decline", "approve", "decline"),
                approvalBodies.map { it.getValue("op").jsonPrimitive.content })
            assertEquals(List(4) { "request" },
                approvalBodies.map { it.getValue("join_request_id").jsonPrimitive.content })
            assertEquals("first\nsecond",
                approvalBodies[1].getValue("reject_reason").jsonPrimitive.content)
            assertEquals("true",
                approvalBodies[1].getValue("add_to_member_blacklist").jsonPrimitive.content)
            assertEquals("relation",
                approvalBodies[3].getValue("reject_reason").jsonPrimitive.content)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
