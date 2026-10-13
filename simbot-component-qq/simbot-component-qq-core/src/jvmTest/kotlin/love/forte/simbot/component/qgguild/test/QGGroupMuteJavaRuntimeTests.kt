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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupMemberImpl
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 使用实际 Java 调用验证生成的阻塞入口能执行相同的请求。
 */
class QGGroupMuteJavaRuntimeTests : AbstractInteractionTests() {
    /**
     * 三层 Java 入口均可发送请求，并从成员查询中返回原始模型。
     */
    @Test
    fun javaBlockingCallsExecuteRequests() = runTest {
        var queries = 0
        var writes = 0
        val app = app { request ->
            assertEquals("/v2/groups/group/restrict_chat_setting", request.url.encodedPath)
            if (request.method == HttpMethod.Get) {
                queries++
                respondOk("""{"global_rule":{"mode":"none"},"members":[{"member_openid":"member","mute_expire_at":"2026-10-11T01:00:00Z","username":"成员"}]}""")
            } else {
                assertEquals(HttpMethod.Post, request.method)
                writes++
                respondOk("{}")
            }
        }
        val bot = app.bot() as QGBotImpl
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val member = QGGroupMemberImpl(bot, group.id, "member".ID)
            val result = withContext(Dispatchers.Default) {
                QGGroupMuteJavaTests.exercise(member, group, bot.groupRelation, 1791676800000L)
            }
            assertEquals("member", result.memberOpenid)
            assertEquals(3, queries)
            assertEquals(11, writes)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
