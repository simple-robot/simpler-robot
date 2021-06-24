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

package love.test

import kotlinx.serialization.decodeFromString
import love.forte.simbot.component.kaiheila.api.ListResp
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildApiRespSort
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildListRespData
import love.forte.simbot.component.kaiheila.khlJson
import org.jetbrains.annotations.TestOnly
import kotlin.test.Test

class GuildJsonTest {
    @Test
    @TestOnly
    fun decode() {
        val sortId = 1
        val data = khlJson.decodeFromString<ListResp<GuildListRespData, GuildApiRespSort>>("""
        {
            "code": 0,
            "message": "操作成功",
            "data": {
                "items": [
                    {
                        "id": "xxx",
                        "name": "test",
                        "topic": "",
                        "master_id": "xxx",
                        "icon": "",
                        "notify_type": 2,
                        "region": "beijing",
                        "enable_open": 0,
                        "open_id": "0",
                        "default_channel_id": "xxx",
                        "welcome_channel_id": "xxx"
                    }
                ],
                "meta": {
                    "page": 1,
                    "page_total": 1,
                    "page_size": 100,
                    "total": 2
                },
                "sort": {
                    "id": $sortId
                }
            }
        }
    """.trimIndent())

        // println(data)
        // println(data.data.sort)
        // println(data.data.sort?.javaClass)
        assert(data.data.sort?.id == sortId) { "sort's id != $sortId." }
    }



    @Test
    fun instanceEncode() {





    }

}


