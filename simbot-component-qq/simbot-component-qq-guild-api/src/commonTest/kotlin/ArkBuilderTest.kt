/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package test

import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.message.buildArk
import love.forte.simbot.qguild.model.Message
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ArkBuilderTest {

    @Test
    fun test() {
        val ark = buildArk("0") {
            // 增加kvs
            kvs {
                kv("Key1", "value") // kv
                kv("Key2", "value2") {
                    obj() // empty obj -> { "objKv": [] }
                    obj {
                        kv("objKey1", "valueA")
                        kv("objKey2", "valueB")
                    }
                }
            }
        }

        val j = Json {
            isLenient = true
            prettyPrint = true
        }

        val jsonStr = j.encodeToString(Message.Ark.serializer(), ark)

        println(jsonStr)
    }

    @Test
    fun decodeTest() {
        val jsonStr = """{
        "template_id": 1,
        "kv": [
            {
                "key": "#DESC#",
                "value": "机器人订阅消息"
            },
            {
                "key": "#PROMPT#",
                "value": "XX机器人"
            },
            {
                "key": "#TITLE#",
                "value": "XX机器人消息"
            },
            {
                "key": "#META_URL#",
                "value": "http://domain.com/"
            },
            {
                "key": "#META_LIST#",
                "obj": [
                    {
                        "obj_kv": [
                            {
                                "key": "name",
                                "value": "aaa"
                            },
                            {
                                "key": "age",
                                "value": "3"
                            }
                        ]
                    },
                    {
                        "obj_kv": [
                            {
                                "key": "name",
                                "value": "bbb"
                            },
                            {
                                "key": "age",
                                "value": "4"
                            }
                        ]
                    }
                ]
            }
        ]
    }"""

        val j = Json {
            isLenient = true
        }

        val ark = j.decodeFromString(Message.Ark.serializer(), jsonStr)
        val ark2 = j.decodeFromString(Message.Ark.serializer(), jsonStr)

        println(ark)
        println(ark2)

        println(ark == ark2)
    }

}
