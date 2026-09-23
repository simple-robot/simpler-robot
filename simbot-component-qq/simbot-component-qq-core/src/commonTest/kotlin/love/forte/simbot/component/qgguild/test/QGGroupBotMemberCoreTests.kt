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
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.group.QGGroupRole
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.model.group.GroupBotReceiveMessageSetting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/**
 * 验证机器人群内成员快照的查询路径与身份语义。
 */
class QGGroupBotMemberCoreTests : AbstractInteractionTests() {
    @Test
    fun botMemberUsesQueriedGroupScopedState() = runTest {
        val paths = mutableListOf<String>()
        val app = app { request ->
            assertEquals(HttpMethod.Get, request.method)
            paths += request.url.encodedPath
            assertEquals("/v2/groups/group/bot_state", request.url.encodedPath)
            respondOk(
                """{"member_openid":"group-bot-member","joined_at":"2025-06-15T14:30:00+08:00","allow_proactive_msg":false,"recv_msg_setting":"only_mention","member_role":"admin"}"""
            )
        }
        val bot = app.bot() as QGBotImpl

        try {
            val group = bot.groupRelation.group("group".ID)!!
            assertEquals(emptyList(), paths)

            val member = group.botAsMember()
            assertEquals("group".ID, member.groupId)
            assertEquals("group-bot-member".ID, member.id)
            assertNotEquals(bot.id, member.id)
            assertEquals(QGGroupRole.ADMIN, member.memberRole)
            assertEquals(member.botState.joinedAt.toEpochMilliseconds(), member.joinTime.milliseconds)
            assertEquals(false, member.allowProactiveMsg)
            assertEquals(GroupBotReceiveMessageSetting.OnlyMention, member.recvMsgSetting)
            assertEquals(listOf("/v2/groups/group/bot_state"), paths)

            val state = group.botState()
            assertEquals("group-bot-member", state.memberOpenid)
            assertEquals("group-bot-member", bot.groupRelation.groupBotState("group".ID).memberOpenid)
            assertEquals(List(3) { "/v2/groups/group/bot_state" }, paths)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }

    @Test
    fun botMemberPropagatesPlatformFailure() = runTest {
        val app = app { request ->
            assertEquals("/v2/groups/group/bot_state", request.url.encodedPath)
            respond("""{"code":11253,"message":"not on whitelist"}""", HttpStatusCode.Forbidden)
        }
        val bot = app.bot() as QGBotImpl

        try {
            val group = bot.groupRelation.group("group".ID)!!
            val failure = assertFailsWith<QQGuildApiException> { group.botAsMember() }
            assertEquals(11253, failure.info?.code)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
