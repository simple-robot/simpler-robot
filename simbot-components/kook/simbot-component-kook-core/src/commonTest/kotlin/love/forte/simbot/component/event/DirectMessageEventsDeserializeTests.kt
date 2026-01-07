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
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.event.DeletedPrivateMessageEventExtra
import love.forte.simbot.kook.event.Signal
import love.forte.simbot.kook.event.SystemExtra
import love.forte.simbot.kook.event.UpdatedPrivateMessageEventExtra
import kotlin.test.Test
import kotlin.test.assertIs

/**
 * Tests for direct message events deserialization.
 * 
 * Reference: https://developer.kookapp.cn/doc/event/direct-message
 * 
 * @author ForteScarlet
 */
class DirectMessageEventsDeserializeTests {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @Test
    fun testDeserializeUpdatedPrivateMessageEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "PERSON",
    "type": 255,
    "target_id": "2418200000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "updated_private_message",
      "body": {
        "msg_id": "67637d4c-xxxx-4fb0-xxxx-b9e745a2baaa",
        "author_id": "2418200000",
        "target_id": "xxxx",
        "content": "修改后的私信消息内容",
        "chat_code": "xxxxxx",
        "updated_at": 1612779153662
      }
    },
    "msg_id": "ac96c4c1-xxxx-4e8d-xxxx-xxxx",
    "msg_timestamp": 1612779153662,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 25
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<UpdatedPrivateMessageEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeDeletedPrivateMessageEvent() {
        //language=json
        val rawJson =
            """{
  "s": 0,
  "d": {
    "channel_type": "PERSON",
    "type": 255,
    "target_id": "2418200000",
    "author_id": "1",
    "content": "[系统消息]",
    "extra": {
      "type": "deleted_private_message",
      "body": {
        "msg_id": "67637d4c-xxxx-4fb0-xxxx-b9e745a2baaa",
        "author_id": "2418200000",
        "target_id": "xxxx",
        "chat_code": "xxxxxx",
        "deleted_at": 1612779200000
      }
    },
    "msg_id": "bc96c4c1-xxxx-4e8d-xxxx-xxxx",
    "msg_timestamp": 1612779200000,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 26
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<DeletedPrivateMessageEventExtra>(deserialized.data.extra)
    }
}
