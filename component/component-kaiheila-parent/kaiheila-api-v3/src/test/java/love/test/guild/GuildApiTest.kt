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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.v3.V3
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildListReq
import love.forte.simbot.component.kaiheila.api.v3.guild.isAsc
import love.test.GatewayApiConstant
import love.test.client
import org.junit.jupiter.api.Test


/**
 *
 * @author ForteScarlet
 */
class GuildApiTest {

    internal suspend fun guildList() = GuildListReq.SortById.Asc.doRequest(
        api = V3,
        client = client,
        token = GatewayApiConstant.token
    ).data

    @Test
    fun guildListTest() = runBlocking {
        val guildList = guildList()

        println(guildList)
        println(guildList.items.size)
        for (item in guildList.items) {
            println(item)
        }
        println(guildList.meta)
        println(guildList.sort)
        println(guildList.sort.id)
        println(guildList.sort.isAsc)
    }


}