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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.json.Json
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.message.segment.OneBotAt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotFace
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class OneBotMessageOutgoingTests {
    private val json: Json = OneBot11.DefaultJson

    @Test
    fun serializationTest() {
        with(OneBotMessageOutgoing.create("[CQ:at,code=123]")) {
            val jsonStr = json.encodeToString(OneBotMessageOutgoing.serializer(), this)
            assertEquals("\"[CQ:at,code=123]\"", jsonStr)
        }

        with(
            OneBotMessageOutgoing.create(
                listOf(
                    OneBotAt.create("123"),
                    OneBotFace.create("456".ID)
                )
            )
        ) {
            val jsonStr = json.encodeToString(OneBotMessageOutgoing.serializer(), this)
            assertEquals(
                """[{"type":"at","data":{"qq":"123"}},{"type":"face","data":{"id":"456"}}]""",
                jsonStr
            )
        }
    }

}
