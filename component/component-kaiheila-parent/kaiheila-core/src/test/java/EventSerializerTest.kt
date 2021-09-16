/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */


import kotlinx.serialization.ExperimentalSerializationApi
import love.forte.simbot.component.kaiheila.event.Signal_0
import love.forte.simbot.component.kaiheila.event.SimpleEvent
import love.forte.simbot.component.kaiheila.event.message.FileEventExtra
import love.forte.simbot.component.kaiheila.khlJson
import love.forte.simbot.component.kaiheila.objects.UserImpl
import org.junit.jupiter.api.Test

/**
 *
 * @author ForteScarlet
 */
class EventSerializerTest {

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun test() {
        val serializer = SimpleEvent.serializer(FileEventExtra.serializer())

        // val fileEvent = khlJson.decodeFromString<SimpleEvent<FileEventExtra>>(fileJson)
        val signal0 = khlJson.decodeFromString(Signal_0.serializer(), fileJson)
        val element = signal0.d
        println(element)
        val event = khlJson.decodeFromJsonElement(serializer, element)

        println(event)


    }


    @Test
    fun test2() {
        val user = khlJson.decodeFromString(UserImpl.serializer(), userJson)

        println(user)

    }

}

val userJson = """
    {
                    "id": "2418200000",
                    "username": "tz-un",
                    "identify_num": "5618",
                    "online": false,
                    "os": "Websocket",
                    "status": 1,
                    "mobile_verified": false,
                    "avatar": "https://img.kaiheila.cn/avatars/2020-02/xxxx.jpg/icon",
                    "tag_info": {
                        "color": "#6666CC",
                        "text": "开黑啦"
                    },
                    "nickname": "12316993",
                    "roles": [
                        111,
                        112
                    ]
                }
""".trimIndent()


internal val kMarkdownJson = """
    {
        "s": 0,
        "d": {
            "channel_type": "GROUP",
            "type": 9,
            "target_id": "48818200000000000",
            "author_id": "2418200000",
            "content": "*Hello World*",
            "extra": {
                "type": 9,
                "guild_id": "6016389914000000",
                "channel_name": "123123",
                "mention": [],
                "mention_all": false,
                "mention_roles": [],
                "mention_here": false,
                "nav_channels": [],
                "code": "",
                "author": {
                    "id": "2418200000",
                    "username": "tz-un",
                    "identify_num": "5618",
                    "online": false,
                    "os": "Websocket",
                    "status": 1,
                    "avatar": "https://img.kaiheila.cn/avatars/2020-02/xxxx.jpg/icon",
                    "tag_info": {
                        "color": "#6666CC",
                        "text": "开黑啦"
                    },
                    "nickname": "12316993",
                    "roles": [
                        111,
                        112
                    ]
                },
                "kmarkdown": {
                    "raw_content": "Hello World",
                    "mention_part": [],
                    "mention_role_part": []
                }
            },
            "msg_id": "789c0b23-xxxx-f7ae1a946f11",
            "msg_timestamp": 1613996877757,
            "nonce": "",
            "verify_token": "xxx"
        },
        "sn": 181
    }
""".trimIndent()


internal val fileJson = """
    {
        "s": 0,
        "d": {
            "channel_type": "GROUP",
            "type": 4,
            "target_id": "xxxx",
            "author_id": "xxxx",
            "content": "https://img.kaiheila.cn/attachments/2020-12/11/asd.txt",
            "msg_id": "67637d4c-xxxx-xxxx-xxxx-xxxxx",
            "msg_timestamp": 1607679683305,
            "nonce": "",
            "extra": {
                "type": 4,
                "guild_id": "xxxx",
                "code": "",
                "attachments": {
                    "type": "file",
                    "url": "https://img.kaiheila.cn/attachments/2020-12/11/asd.txt",
                    "name": "voice-message.txt",
                    "file_type": "text/plain",
                    "size": 7320
                },
                "author": {
                    "online": false,
                    "os": "Websocket",
                    "status": 1,
                    "identify_num": "xxxx",
                    "mobile_verified": false,
                    "avatar": "https://img.kaiheila.cn/avatars/2020-11/asd.jpg/icon",
                    "username": "xxxx",
                    "id": "xxxx",
                    "nickname": "xxxx",
                    "roles": []
                }
            }
        },
        "sn": 2587
    }
""".trimIndent()
