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

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.event.GroupMemberAdd
import love.forte.simbot.qguild.event.GroupMemberRemove
import love.forte.simbot.qguild.event.InteractionCreate
import love.forte.simbot.qguild.event.resolveDispatchSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GroupManagementAndInteractionTests {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun groupMemberAddDispatchResolveTest() {
        val event = decodeDispatch<GroupMemberAdd>(
            """
                {
                  "op": 0,
                  "s": 1,
                  "t": "GROUP_MEMBER_ADD",
                  "id": "event-id",
                  "d": {
                    "group_openid": "group-openid",
                    "member_openid": "member-openid",
                    "timestamp": "2026-07-07T12:00:00+08:00"
                  }
                }
            """.trimIndent()
        )

        assertEquals("event-id", event.id)
        assertEquals(1, event.seq)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.memberOpenid)
        assertEquals("2026-07-07T12:00:00+08:00", event.data.timestamp)
    }

    @Test
    fun groupMemberRemoveDispatchResolveTest() {
        val event = decodeDispatch<GroupMemberRemove>(
            """
                {
                  "op": 0,
                  "s": 2,
                  "t": "GROUP_MEMBER_REMOVE",
                  "id": "event-id",
                  "d": {
                    "group_openid": "group-openid",
                    "member_openid": "member-openid",
                    "timestamp": "2026-07-07T12:00:00+08:00"
                  }
                }
            """.trimIndent()
        )

        assertEquals("event-id", event.id)
        assertEquals(2, event.seq)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.memberOpenid)
    }

    @Test
    fun interactionCreateDispatchResolveTest() {
        val event = decodeDispatch<InteractionCreate>(
            """
                {
                  "op": 0,
                  "s": 3,
                  "t": "INTERACTION_CREATE",
                  "id": "INTERACTION_CREATE:interaction-id",
                  "d": {
                    "application_id": "102076256",
                    "chat_type": 1,
                    "data": {
                      "resolved": {
                        "button_data": "confirm",
                        "button_id": "btn_6"
                      },
                      "type": 11
                    },
                    "group_member_openid": "member-openid",
                    "group_openid": "group-openid",
                    "id": "interaction-id",
                    "scene": "group",
                    "timestamp": "2026-07-07T12:00:00+08:00",
                    "type": 11,
                    "version": 1
                  }
                }
            """.trimIndent()
        )

        assertEquals("INTERACTION_CREATE:interaction-id", event.id)
        assertEquals(3, event.seq)
        assertEquals("interaction-id", event.data.id)
        assertEquals(11, event.data.type)
        assertEquals("group", event.data.scene)
        assertEquals(1, event.data.chatType)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.groupMemberOpenid)
        assertEquals("confirm", event.data.data.resolved.buttonData)
        assertEquals("btn_6", event.data.data.resolved.buttonId)
    }

    private inline fun <reified T> decodeDispatch(source: String): T {
        val element = json.parseToJsonElement(source)
        val serializer = assertNotNull(resolveDispatchSerializer(element.jsonObject))
        val dispatch = json.decodeFromJsonElement(serializer, element)
        return assertIs<T>(dispatch)
    }
}
