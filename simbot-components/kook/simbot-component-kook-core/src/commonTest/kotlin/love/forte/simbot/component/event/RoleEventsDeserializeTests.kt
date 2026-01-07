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
 * Tests for KOOK guild role events deserialization.
 * 
 * Reference: https://developer.kookapp.cn/doc/event/guild-role
 *
 * @author ForteScarlet
 */
class RoleEventsDeserializeTests {
    private val json = Json(Kook.DEFAULT_JSON) {
        prettyPrint = true
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun testDeserializeAddedRoleEvent() {
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
      "type": "added_role",
      "body": {
        "role_id": 11111,
        "name": "新角色",
        "color": 0,
        "position": 5,
        "hoist": 0,
        "mentionable": 0,
        "permissions": 142924296
      }
    },
    "msg_id": "c804a6a6-xxxx-e041ec3bb887",
    "msg_timestamp": 1613998615411,
    "nonce": "",
    "verify_token": "xxxx"
  },
  "sn": 186
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<AddedRoleEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeDeletedRoleEvent() {
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
      "type": "deleted_role",
      "body": {
        "role_id": 11111,
        "name": "新角色",
        "color": 0,
        "position": 5,
        "hoist": 0,
        "mentionable": 0,
        "permissions": 142924296
      }
    },
    "msg_id": "61f1dc5a-xxxx-47776deacd0c",
    "msg_timestamp": 1614054458292,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 192
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<DeletedRoleEventExtra>(deserialized.data.extra)
    }

    @Test
    fun testDeserializeUpdatedRoleEvent() {
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
      "type": "updated_role",
      "body": {
        "role_id": 599,
        "name": "新角色xxx",
        "color": 0,
        "position": 6,
        "hoist": 0,
        "mentionable": 0,
        "permissions": 142924316
      }
    },
    "msg_id": "a1071327-xxxx-8fec42ae74bd",
    "msg_timestamp": 1614054560619,
    "nonce": "",
    "verify_token": "xxx"
  },
  "sn": 194
}"""

        val ser = Signal.Event.serializer(SystemExtra.serializer())
        val deserialized = json.decodeFromString(ser, rawJson)
        assertIs<UpdatedRoleEventExtra>(deserialized.data.extra)
    }
}
