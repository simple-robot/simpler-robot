/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     GuildMuteListTest.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.test

import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.v3.V3
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildMuteListReq
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class GuildMuteListTest {

    @Test
    fun guildMuteListTestByDetail() = runBlocking {
        val muteList = GuildMuteListReq.Detail(guildId)
            .doRequest(V3, client, GatewayApiConstant.token).data!!

        println(muteList)
    }

    @Test
    @Suppress("DEPRECATION")
    fun guildMuteListTestBySimple() = runBlocking {
        val muteList = GuildMuteListReq.Simple(guildId)
            .doRequest(V3, client, GatewayApiConstant.token).data!!

        println(muteList)
    }

}