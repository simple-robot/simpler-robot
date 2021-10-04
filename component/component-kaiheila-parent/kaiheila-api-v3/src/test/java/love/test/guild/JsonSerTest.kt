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

package love.test.guild

import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.BaseRespData
import love.forte.simbot.component.kaiheila.api.ListResp
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.khlJson
import kotlin.test.Test


@Serializable
data class User(val name: String, val age: Int) : BaseRespData()


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
                    },
                    "sort": {}
                }
            }
        """.trimIndent()

        //  items: List<RESP> = emptyList(),
        //     val meta: RespMeta,
        //     val sort: SORT? = nu



        val resp = khlJson.decodeFromString(ListResp.serializer(User.serializer(), ApiData.Resp.EmptySort.serializer()), json)

        println(resp::class)
        println(resp)
        println(resp.data)
        println(resp.data.sort)
        println(resp.data.meta)
        println(resp.data.items)

    }


    @Test
    fun jsonTest1() {
        val json = "{\"code\":0,\"message\":\"操作成功\",\"data\":{\"items\":[{\"id\":\"6865507942900765\",\"name\":\"空羽龙月的方舟课堂\",\"topic\":\"\",\"master_id\":\"886154643\",\"is_master\":false,\"icon\":\"https://img.kaiheila.cn/icons/2021-06/0oDNdfaNqK06j06j.png/icon\",\"notify_type\":2,\"region\":\"beijing\",\"enable_open\":false,\"open_id\":\"0\",\"default_channel_id\":\"7566099004366572\",\"welcome_channel_id\":\"0\"}],\"meta\":{\"page\":1,\"page_total\":1,\"page_size\":100,\"total\":1},\"sort\":{\"id\":1}}}\n"

        val element = khlJson.parseToJsonElement(json)
        println(element)

    }

}