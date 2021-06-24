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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.v3.V3
import love.forte.simbot.component.kaiheila.api.v3.message.MessageCreateReq
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


class MessageCreateTest {


    @OptIn(ExperimentalTime::class)
    @Test
    fun createTest() = runBlocking {
        // 文字频道: 7566099004366572
        val textChannel = "7566099004366572"

        MessageCreateReq(targetId = textChannel, content = "山本，我日你先人！").doRequest(
            api = V3,
            client,
            GatewayApiConstant.token
        ).let { resp ->
            println(resp)
            println(resp.data)
            val data = resp.data!!

            val time = Duration.milliseconds(data.msgTimestamp)

            println(LocalDateTime.ofInstant(Instant.ofEpochMilli(time.inWholeMilliseconds), ZoneId.of("+8")))

        }


    }



}