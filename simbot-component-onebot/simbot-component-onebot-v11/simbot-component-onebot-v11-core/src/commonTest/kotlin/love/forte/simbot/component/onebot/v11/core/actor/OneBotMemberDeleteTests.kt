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

package love.forte.simbot.component.onebot.v11.core.actor

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.id.UIntID.Companion.ID
import love.forte.simbot.common.id.ULongID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.actor.internal.OneBotMemberImpl
import love.forte.simbot.component.onebot.v11.core.api.SetGroupKickApi
import love.forte.simbot.component.onebot.v11.core.api.requestData
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class OneBotMemberDeleteTests {
    @Test
    fun oneBotMemberDeleteMarkTest() {
        var mark = OneBotMemberImpl.DeleteMark()
        assertFalse(mark.isRejectRequest)
        assertFalse(mark.isIgnoreFailure)

        mark = mark.rejectRequest()
        assertTrue(mark.isRejectRequest)

        mark = mark.ignoreFailure()
        assertTrue(mark.isIgnoreFailure)
    }

    @Test
    fun oneBotMemberDeleteTest() = runTest {
        HttpClient(
            MockEngine { reqData ->
                val json = reqData.body.toByteArray().decodeToString()
                val obj = Json.decodeFromString(JsonObject.serializer(), json)
                val rejectRequest = obj["reject_add_request"]?.jsonPrimitive?.booleanOrNull
                assertNotNull(rejectRequest)
                assertTrue(rejectRequest)
                respondOk("""{"retcode":0,"status":null,"data":null}""")
            }
        ).use { client ->
            SetGroupKickApi.create(ULong.MAX_VALUE.ID, UInt.MAX_VALUE.ID, rejectAddRequest = true).requestData(
                client,
                "127.0.0.1"
            )
        }

    }

}
