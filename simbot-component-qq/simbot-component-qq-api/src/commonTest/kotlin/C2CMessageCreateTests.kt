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

package test

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.qguild.event.resolveDispatchSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class C2CMessageCreateTests {
    @Test
    fun c2cMessageCreateDeserializesMenuSwitchScene() {
        val raw = """{"op":0,"s":1,"t":"C2C_MESSAGE_CREATE","id":"event-id","d":{"id":"message-id","author":{"user_openid":"user-openid"},"content":"","timestamp":"2026-09-05T12:00:00+08:00","message_type":0,"message_scene":{"source":"custom_menu","ext":["search=1"]}}}"""
        val json = Json { ignoreUnknownKeys = true }
        val element = json.decodeFromString(JsonElement.serializer(), raw).jsonObject
        val serializer = assertNotNull(resolveDispatchSerializer(element))
        val event = assertIs<C2CMessageCreate>(json.decodeFromJsonElement(serializer, element))
        assertEquals(0, event.data.messageType)
        assertEquals("custom_menu", event.data.messageScene?.source)
        assertEquals(listOf("search=1"), event.data.messageScene?.ext)
    }
}
