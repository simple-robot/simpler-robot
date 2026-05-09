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

package love.forte.simbot.kook.api.guild

import kotlinx.serialization.json.Json
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.objects.SimpleGuild
import love.forte.simbot.kook.objects.SimpleGuildWithRolesAndChannels
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for guild API responses serialization/deserialization.
 *
 * Reference: https://developer.kookapp.cn/doc/http/guild
 *
 * @author ForteScarlet
 */
class GuildApiResponseTests {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @Test
    fun testDeserializeGuildListResponse() {
        // Extract just the data portion for ListData deserialization
        val dataJson = """{
    "items": [
      {
        "id": "6016389000000",
        "name": "测试频道",
        "topic": "",
        "user_id": "2418000000",
        "icon": "https://img.kookapp.cn/icons/2021-01/xxxx.jpg/icon",
        "notify_type": 2,
        "region": "beijing",
        "enable_open": 1,
        "open_id": "17811111",
        "default_channel_id": "6016400000000000",
        "welcome_channel_id": "6016400000000000"
      }
    ],
    "meta": {
      "page": 1,
      "page_total": 1,
      "page_size": 50,
      "total": 1
    }
  }"""

        val listDataDeserializer = ListData.serializer(SimpleGuild.serializer())
        val response = json.decodeFromString(listDataDeserializer, dataJson)

        assertNotNull(response)
        assertEquals(1, response.items.size)
        assertEquals("6016389000000", response.items[0].id)
        assertEquals("测试频道", response.items[0].name)
        assertEquals("2418000000", response.items[0].userId)
        assertEquals(1, response.meta.page)
        assertEquals(1, response.meta.pageTotal)
    }

    @Test
    fun testDeserializeGuildViewResponse() {
        //language=json
        val rawJson = """{
  "id": "6016389000000",
  "name": "测试频道",
  "topic": "频道的说明",
  "user_id": "2418000000",
  "icon": "https://img.kookapp.cn/icons/2021-01/xxxx.jpg/icon",
  "notify_type": 2,
  "region": "beijing",
  "enable_open": 1,
  "open_id": "17811111",
  "default_channel_id": "6016400000000000",
  "welcome_channel_id": "6016400000000000",
  "roles": [
    {
      "role_id": 114514,
      "name": "测试",
      "color": 0,
      "position": 5,
      "hoist": 0,
      "mentionable": 0,
      "permissions": 1400928815
    }
  ],
  "channels": [
    {
      "id": "6016400000000000",
      "name": "文字频道",
      "user_id": "2418000000",
      "guild_id": "6016389000000",
      "topic": "",
      "is_category": 0,
      "parent_id": "",
      "level": 1,
      "slow_mode": 0,
      "slow_mode_reply": 0,
      "type": 1,
      "permission_overwrites": [],
      "permission_users": [],
      "permission_sync": 1,
      "has_password": false
    }
  ]
}"""

        val guild = json.decodeFromString(SimpleGuildWithRolesAndChannels.serializer(), rawJson)

        assertNotNull(guild)
        assertEquals("6016389000000", guild.id)
        assertEquals("测试频道", guild.name)
        assertEquals("频道的说明", guild.topic)
        assertEquals("2418000000", guild.userId)
        assertEquals(1, guild.roles.size)
        assertEquals(114514, guild.roles[0].roleId)
        assertEquals("测试", guild.roles[0].name)
        assertEquals(1, guild.channels.size)
        assertEquals("6016400000000000", guild.channels[0].id)
        assertEquals("文字频道", guild.channels[0].name)
    }
}
