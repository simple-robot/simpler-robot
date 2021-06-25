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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.v3.V3
import love.forte.simbot.component.kaiheila.api.v3.channel.ChannelListReq
import love.test.GatewayApiConstant
import love.test.client
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ChannelListTest {


    @Test
    fun listTest() = runBlocking {
        val guildId = "6865507942900765" // GuildApiTest().guildList().items[0].id

        val data = ChannelListReq(guildId).doRequest(V3, client, GatewayApiConstant.token).data

        println(data)

        println("-0-")

        data.forEach {
            println(it)
        }


    }

}