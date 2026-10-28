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
import love.forte.simbot.qguild.event.GroupAtMessageCreate
import love.forte.simbot.qguild.event.GroupMessageAuthorRole
import love.forte.simbot.qguild.event.GroupMessageCreate
import love.forte.simbot.qguild.event.resolveDispatchSerializer
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class GroupMessageCreateTests {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * 两种群消息作者均按平台的字段名保留跨应用统一标识。
     */
    @Test
    fun groupAuthorsRetainUnionIdentifiers() {
        val raw = """{"member_openid":"member","id":"user","username":"昵称","union_openid":"union","union_user_account":"account"}"""
        val atAuthor = json.decodeFromString(GroupAtMessageCreate.Author.serializer(), raw)
        val author = json.decodeFromString(GroupMessageCreate.Author.serializer(), raw)
        for (value in listOf(atAuthor, author)) {
            assertEquals("user", value.id)
            assertEquals("昵称", value.username)
            assertEquals("union", value.unionOpenid)
            assertEquals("account", value.unionUserAccount)
        }
        val encoded = listOf(
            json.encodeToString(GroupAtMessageCreate.Author.serializer(), atAuthor),
            json.encodeToString(GroupMessageCreate.Author.serializer(), author),
        )
        for (value in encoded) {
            val fields = json.parseToJsonElement(value).jsonObject
            assertEquals("\"union\"", fields["union_openid"].toString())
            assertEquals("\"account\"", fields["union_user_account"].toString())
            assertFalse("unionOpenid" in fields)
            assertFalse("unionUserAccount" in fields)
        }
    }

    @Test
    fun groupMessageCreateDispatchResolveTest() {
        val raw = """
            {
              "op": 0,
              "s": 1,
              "t": "GROUP_MESSAGE_CREATE",
              "id": "event-id",
              "d": {
                "author": {
                  "member_openid": "member-openid"
                },
                "content": "hello",
                "group_openid": "group-openid",
                "id": "message-id",
                "timestamp": "2023-11-06T13:37:18+08:00"
              }
            }
        """.trimIndent()

        val element = json.decodeFromString(JsonElement.serializer(), raw).jsonObject
        val serializer = assertNotNull(resolveDispatchSerializer(element))
        val event = assertIs<GroupMessageCreate>(json.decodeFromJsonElement(serializer, element))

        assertEquals("event-id", event.id)
        assertEquals(1, event.seq)
        assertEquals("message-id", event.data.id)
        assertEquals("group-openid", event.data.groupOpenid)
        assertEquals("member-openid", event.data.author.memberOpenid)
        assertEquals(GroupMessageAuthorRole.MEMBER, event.data.author.memberRole)
        assertFalse(event.data.author.bot)
    }
}
