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

import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.v3.Gateway
import love.forte.simbot.component.kaiheila.api.v3.GatewayReq
import love.forte.simbot.component.kaiheila.api.v3.V3
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class GatewayApiTest {

    companion object {
        const val clientId = GatewayApiConstant.clientId
        const val token = GatewayApiConstant.token
        const val clientSecret = GatewayApiConstant.clientSecret
    }


    @Test
    fun apiTest() = runBlocking {
        val gatewayReq = GatewayReq(1)

        // client.get<Gateway> {
        //
        // }


        val gateway: ObjectResp<Gateway> =
            gatewayReq.doRequest(
                V3,
                client,
                token)


        println(gateway)
        println(gateway.code)
        println(gateway.message)
        println(gateway.data)

        var session: DefaultClientWebSocketSession? = null

        client.ws(gateway.data!!.url) {
            session = this
            when (val frame: Frame = incoming.receive()) {
                is Frame.Text -> println("[Text  ]: " + frame.readText())
                is Frame.Binary -> println("[Binary]: " + frame.readBytes().decodeToString())
                is Frame.Close -> println("[Close ]: " + frame.readBytes().decodeToString())
                is Frame.Ping -> println("[Ping  ]: " + frame.readBytes().decodeToString())
                is Frame.Pong -> println("[Pong: ]: " + frame.readBytes().decodeToString())
            }

            delay(5000)

            this.close()

        }
    }


}