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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QGGroupJoinApprovalStrategyJavaRuntimeTests : AbstractInteractionTests() {
    @Test
    fun javaBlockingAndAsyncCallsUseBotRequests() = runTest {
        var requests = 0
        val time = "2027-08-05T15:30:16+08:00"
        val app = app { request ->
            requests++
            when {
                request.url.encodedPath.endsWith("/whitelist_users") ->
                    respondOk("""{"strategy_id":"created","whitelist_user_count":1,"updated_at":"$time"}""")
                request.method == HttpMethod.Patch ->
                    respondOk("""{"is_enable":"off","expire_at":"$time"}""")
                request.url.encodedPath == "/v2/groups/join_approval_strategy" ->
                    respondOk("""{"strategy_id":"created","is_enable":"on","expire_at":"$time"}""")
                else -> respondOk()
            }
        }
        val bot = app.bot()
        try {
            val result = withContext(Dispatchers.Default) {
                QGGroupJoinApprovalStrategyJavaCalls.exercise(bot.groupJoinApprovalStrategies)
            }
            assertEquals("created", result.id.toString())
            assertTrue(result.isEnable)
            assertEquals(6, requests)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
