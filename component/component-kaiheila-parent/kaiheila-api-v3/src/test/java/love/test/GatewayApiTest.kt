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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.v3.Gateway
import love.forte.simbot.component.kaiheila.api.v3.GatewayReq
import love.forte.simbot.component.kaiheila.api.v3.V3
import love.forte.simbot.component.kaiheila.event.*
import love.forte.simbot.component.kaiheila.khlJson
import java.util.zip.InflaterInputStream
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


        val gateway: ObjectResp<Gateway> =
            gatewayReq.doRequest(
                V3,
                client,
                token)


        println(gateway)
        println(gateway.code)
        println(gateway.message)
        println(gateway.data)

        // var session: DefaultClientWebSocketSession? = null

        client.ws(gateway.data!!.url) {
            // session = this
            when (val frame: Frame = incoming.receive()) {
                is Frame.Text -> println("[Text  ]: " + frame.readText())
                is Frame.Binary -> {
                    val text = InflaterInputStream(frame.readBytes().inputStream()).reader(Charsets.UTF_8).use {
                        it.readText()
                    }
                    println("[Binary-text]: $text")

                    val element = khlJson.parseToJsonElement(text)
                    val s = element.jsonObject["s"]?.jsonPrimitive?.int
                    val signal = when(s) {
                        0 -> khlJson.decodeFromJsonElement<Signal_0>(element)
                        1 -> khlJson.decodeFromJsonElement<Signal_1>(element)
                        2 -> khlJson.decodeFromJsonElement<Signal_2>(element)
                        3 -> khlJson.decodeFromJsonElement<Signal_3>(element)
                        5 -> khlJson.decodeFromJsonElement<Signal_5>(element)
                        6 -> khlJson.decodeFromJsonElement<Signal_6>(element)
                        else -> error(element)
                    }

                    println("[Signal_$s]: $signal")

                }
                is Frame.Close -> {
                    println("[Close ]: " + frame.readBytes().decodeToString())
                    this.close()
                }
                is Frame.Ping -> {
                    val str = frame.readBytes().decodeToString()
                    val ping = khlJson.decodeFromString<Signal.Ping>(str)

                    println(ping)

                    println("[Ping  ]: $str")
                }
                is Frame.Pong -> {
                    println("[Pong: ]: " + frame.readBytes().decodeToString())
                }
            }

            delay(30000)
            this.close()
        }
    }


}