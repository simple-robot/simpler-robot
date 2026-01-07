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

package love.forte.simbot.component.event

import kotlinx.serialization.json.Json
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.event.*
import kotlin.test.Test
import kotlin.test.assertIs

/**
 * Tests for KOOK guild member events deserialization.
 * 
 * Reference: https://developer.kookapp.cn/doc/event/guild-member
 *
 * @author ForteScarlet
 */
class MemberEventsDeserializeTests {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun testDeserializeJoinedGuildEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163000000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "joined_guild",
      "body": {
        "user_id": "3891000000",
        "joined_at": 1612774315000
      }
    },
    "msg_id": "bcc9abbd-xxxx-61c6a976be5d",
    "msg_timestamp": 1612774315732,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 15
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<JoinedGuildEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeExitedGuildEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163000000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "exited_guild",
      "body": {
        "user_id": "3891000000",
        "exited_at": 1612774287628
      }
    },
    "msg_id": "ecec53c4-xxxx-16226c48487b",
    "msg_timestamp": 1612774287636,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 14
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<ExitedGuildEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeUpdatedGuildMemberEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163000000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "updated_guild_member",
      "body": {
        "user_id": "3891600000",
        "nickname": "new_nick"
      }
    },
    "msg_id": "d22ae13c-xxxxxx-71f8398e16b5",
    "msg_timestamp": 1612774472181,
    "nonce": "",
    "verify_token": "xxxxx"
  },
  "sn": 17
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<UpdatedGuildMemberEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeGuildMemberOnlineEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "PERSON",
    "type": 255,
    "target_id": "2862900000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "guild_member_online",
      "body": {
        "user_id": "2418200000",
        "event_time": 1612930480315,
        "guilds": ["601638990000000"]
      }
    },
    "msg_id": "35f19bd2-xxxx-3eef019abb84",
    "msg_timestamp": 1612930480347,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 72
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<GuildMemberOnlineEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeGuildMemberOfflineEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "PERSON",
    "type": 255,
    "target_id": "2862900000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "guild_member_offline",
      "body": {
        "user_id": "2418200000",
        "event_time": 1612938960033,
        "guilds": ["601638990000000"]
      }
    },
    "msg_id": "35f19bd2-xxxx-3eef019abb84",
    "msg_timestamp": 1612938960033,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 74
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<GuildMemberOfflineEventExtra>(deserialized.data.extra)
    }
}
