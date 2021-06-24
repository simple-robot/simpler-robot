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

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.ListResp
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.khlJson
import kotlin.test.Test


@Serializable
data class User(val name: String, val age: Int) : ApiData.Resp.Data


/**
 *
 * @author ForteScarlet
 */
class JsonSerTest {

    @Test
    fun objectTest() {

        val json = """
            {
                "code": 0,
                "message": "",
                "data": {
                    "name": "forte",
                    "age": 12
                }
            }
        """.trimIndent()

        val deserializer = ObjectResp.serializer(User.serializer())

        val resp = khlJson.decodeFromString(deserializer, json)

        println(resp::class)
        println(resp)
        println(resp.data)

    }

    @Test
    fun listTest() {

        val json = """
            {
                "code": 0,
                "message": "",
                "data": {
                    "items": [
                        {
                            "name": "forte",
                            "age": 12
                        },
                        {
                            "name": "forte",
                            "age": 12
                        }
                    ],
                    "meta": {
                      "page": 1,
                      "page_total": 10,
                      "page_size": 10,
                      "total": 100
                    }
                }
            }
        """.trimIndent()

        //  items: List<RESP> = emptyList(),
        //     val meta: RespMeta,
        //     val sort: SORT? = nu



        val resp = khlJson.decodeFromString(ListResp.serializer(User.serializer(), Int.serializer()), json)

        println(resp::class)
        println(resp)
        println(resp.data)
        println(resp.data.sort)
        println(resp.data.meta)
        println(resp.data.items)

    }

}