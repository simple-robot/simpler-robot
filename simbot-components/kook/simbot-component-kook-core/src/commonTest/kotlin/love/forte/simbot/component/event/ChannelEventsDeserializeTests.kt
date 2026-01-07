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
 * Tests for channel events deserialization.
 * 
 * Reference: https://developer.kookapp.cn/doc/event/channel
 * 
 * @author ForteScarlet
 */
class ChannelEventsDeserializeTests {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun testDeserializeChannelAddedEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "1234",
    "author_id": "1",
    "content": "[sys]",
    "extra": {
      "type": "added_channel",
      "body": {
        "id": "5678", 
        "name": "test",
        "user_id": "1234",
        "guild_id": "1234",
        "guild_type": 0,
        "is_category": 0,
        "parent_id": "9012",
        "level": 200,
        "slow_mode": 0,
        "slow_mode_reply": 0,
        "topic": "",
        "type": 1,
        "permission_overwrites": [
          {
            "role_id": 0,
            "allow": 0,
            "deny": 0
          }
        ],
        "permission_users": [],
        "permission_sync": 1,
        "mode": 0,
        "has_password": false,
        "last_msg_content": "welcome",
        "last_msg_id": "",
        "sync_guild_region": 0,
        "region": "",
        "default_layout": 1,
        "sort_order": 1,
        "user_setting": {
          "follow_channel": 1
        }
      }
    },
    "msg_id": "abc",
    "msg_timestamp": 1000000,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "123",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 1
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<AddedChannelEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeChannelDeletedEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "123456",
    "author_id": "1",
    "content": "[msg]",
    "extra": {
      "type": "deleted_channel",
      "body": {
        "id": "111222",
        "deleted_at": 1234567890,
        "type": 1
      }
    },
    "msg_id": "abc-def",
    "msg_timestamp": 1234567890,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "token123",
    "encryptKey": "key123",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 1
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)

        assertIs<DeletedChannelEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeChannelUpdatedEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "6016389000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "updated_channel",
      "body": {
        "id": "53002000000000",
        "name": "更新后的频道",
        "user_id": "2418239356",
        "guild_id": "6016389000000",
        "guild_type": 0,
        "is_category": 0,
        "parent_id": "6016400000000000",
        "level": 12,
        "slow_mode": 0,
        "slow_mode_reply": 0,
        "topic": "更新后的频道的说明",
        "type": 1,
        "permission_overwrites": [
          {
            "role_id": 0,
            "allow": 0,
            "deny": 0
          }
        ],
        "permission_users": [],
        "permission_sync": 1,
        "mode": 0,
        "has_password": false,
        "last_msg_content": "",
        "last_msg_id": "",
        "sync_guild_region": 0,
        "region": "",
        "default_layout": 1,
        "sort_order": 1,
        "user_setting": {
          "follow_channel": 1
        }
      }
    },
    "msg_id": "70b60e52-xxx-4e8d0a995b1a",
    "msg_timestamp": 1612776265417,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 21
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<UpdatedChannelEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeAddedReactionEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163800000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "added_reaction",
      "body": {
        "channel_id": "5823400000000",
        "channel_type": 1,
        "emoji": {
          "id": "[#128226;]",
          "name": "[#128226;]"
        },
        "user_id": "2418000000",
        "msg_id": "59def270-xxxx-xxxx-xxxx-8db935e054a1"
      }
    },
    "msg_id": "xxxx-xxxx-xxxx",
    "msg_timestamp": 1612703779612,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 1
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<AddedReactionEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeDeletedReactionEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163800000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "deleted_reaction",
      "body": {
        "channel_id": "5823400000000",
        "channel_type": 1,
        "emoji": {
          "id": "[#128226;]",
          "name": "[#128226;]"
        },
        "user_id": "2418000000",
        "msg_id": "59def270-xxxx-xxxx-xxxx-8db935e054a1"
      }
    },
    "msg_id": "abd87680-xxxx-xxxx-xxxx-a56107a48e12",
    "msg_timestamp": 1612703776899,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 1
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<DeletedReactionEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeUpdatedMessageEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163899100000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "updated_message",
      "body": {
        "channel_id": "5823470000000",
        "content": "1aaaaaa",
        "mention": [],
        "mention_all": false,
        "mention_here": false,
        "mention_roles": [],
        "updated_at": 1612703810779,
        "msg_id": "59def270-xxx-8db935e054a1",
        "channel_type": 1
      }
    },
    "msg_id": "cc523bf1-xxx-88c31ebc9fba",
    "msg_timestamp": 1612703810791,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 3
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<UpdatedMessageEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeDeletedMessageEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "60163000000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "deleted_message",
      "body": {
        "channel_id": "58234000000",
        "msg_id": "59def270-xxxx-8db935e054a1",
        "channel_type": 1
      }
    },
    "msg_id": "63d6a934-xxxx-a1f02c255213",
    "msg_timestamp": 1612704007683,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 5
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<DeletedMessageEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializePinnedMessageEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "xxx",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "pinned_message",
      "body": {
        "channel_id": "xxxx",
        "operator_id": "2418200000",
        "msg_id": "4d5ef7ae-xxxx-b03e57cdf2e9",
        "channel_type": 1
      }
    },
    "msg_id": "d2bad9e9-xxxx-265f74dc35bb",
    "msg_timestamp": 1614054656178,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 196
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<PinnedMessageEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeUnpinnedMessageEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "GROUP",
    "type": 255,
    "target_id": "xxx",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "unpinned_message",
      "body": {
        "channel_id": "xxxxx",
        "operator_id": "2418200000",
        "msg_id": "4d5ef7ae-xxxx-b03e57cdf2e9",
        "channel_type": 1
      }
    },
    "msg_id": "9b269469-xxxx-9e526ea8f7f0",
    "msg_timestamp": 1614054894618,
    "nonce": "",
    "from_type": 1
  },
  "extra": {
    "verifyToken": "xxxx",
    "encryptKey": "456",
    "callbackUrl": "",
    "intent": -1
  },
  "sn": 197
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<UnpinnedMessageEventExtra>(deserialized.data.extra)
    }
}
