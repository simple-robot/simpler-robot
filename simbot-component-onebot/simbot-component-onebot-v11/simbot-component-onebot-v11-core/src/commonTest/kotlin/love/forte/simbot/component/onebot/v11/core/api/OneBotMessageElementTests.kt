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
import kotlinx.serialization.modules.plus
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.message.segment.OneBotAt
import love.forte.simbot.component.onebot.v11.message.segment.add
import love.forte.simbot.message.At
import love.forte.simbot.message.Messages
import love.forte.simbot.message.buildMessages
import love.forte.simbot.message.encodeMessagesToString
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class OneBotMessageElementTests {

    @Test
    fun elementSerializationTest() {
        val json = Json(OneBot11.DefaultJson) {
            serializersModule = Messages.standardSerializersModule + serializersModule
        }

        val msgList = buildMessages {
            add(At(123.ID))
            add(OneBotAt.create(1.ID))
        }

        println(json.encodeMessagesToString(msgList))
    }

}
