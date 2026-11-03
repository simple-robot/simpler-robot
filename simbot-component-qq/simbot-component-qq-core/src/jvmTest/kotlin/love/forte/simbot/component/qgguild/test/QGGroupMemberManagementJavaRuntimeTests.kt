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
import love.forte.simbot.component.qguild.group.QGGroupRole
import kotlin.test.*

class QGGroupMemberManagementJavaRuntimeTests : AbstractInteractionTests() {
    @Test
    fun javaBlockingEntrypointsExecuteRequests() = runTest {
        var queries = 0
        var writes = 0
        val app = app { request ->
            if (request.method == HttpMethod.Get) {
                queries++
                respondOk("""{"member_openid":"member","username":"成员","member_role":"owner","bot":false,"joined_at":"2026-08-20T01:15:00Z"}""")
            } else {
                writes++
                if (request.url.encodedPath.endsWith("/batch_remove_members")) respondOk("""{"remove_members_result":"success","add_to_member_blacklist_fail_openids":[]}""")
                else respondOk("""{"fail_openids":[]}""")
            }
        }
        val bot = app.bot()
        try {
            val group = bot.groupRelation.group("group".ID)!!
            val member = withContext(Dispatchers.Default) {
                QGGroupMemberManagementJavaCalls.exercise(group, bot.groupRelation, "member".ID)
            }
            assertEquals("member".ID, member.id)
            assertEquals(QGGroupRole.OWNER, member.memberRole)
            assertEquals(5, queries)
            assertEquals(6, writes)
        } finally { app.cancel(); bot.cancel() }
    }
}
