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

package love.test.channel

import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import love.forte.simbot.kaiheila.api.ApiData
import love.forte.simbot.kaiheila.api.ListResp
import love.forte.simbot.kaiheila.api.doRequest
import love.forte.simbot.kaiheila.api.v3.V3
import love.forte.simbot.kaiheila.api.v3.channel.ChannelInfo
import love.forte.simbot.kaiheila.api.v3.channel.ChannelListReq
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ChannelListTest {


    @Test
    fun listTest() = runBlocking {
        val guildId = "6865507942900765" // GuildApiTest().guildList().items[0].id

        val client = HttpClient() // ktor client
        val token = "token"

        val data: ListResp<ChannelInfo, ApiData.Resp.EmptySort> = ChannelListReq(guildId).doRequest(V3, client, token)

        println(data)

        println("-0-")

        data.forEach { it: ChannelInfo ->
            println(it)
        }


    }

}