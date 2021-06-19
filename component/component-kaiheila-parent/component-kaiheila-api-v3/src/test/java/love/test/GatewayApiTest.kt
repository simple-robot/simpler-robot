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

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.kaiheila.api.doRequest
import love.forte.simbot.component.kaiheila.api.toKhlBuild
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
        // const val verifyToken = "UtuLQVwfvpxU2LDz"
        // const val token = "1/MTAyNTA=/246ZJ1bTE3tq5kd0vHaLZg=="
    }

    private val client: HttpClient = HttpClient(CIO) {
        install(JsonFeature) {
            this.serializer = KotlinxSerializer()
        }
    }

    @Test
    fun apiTest() {
        runBlocking {
            val gatewayReq = GatewayReq(authorization = token)

            // client.get<Gateway> {
            //
            // }

            val gateway = gatewayReq.doRequest(V3, client)

            println(gateway)
            println(gateway.code)
            println(gateway.message)
            println(gateway.data)

        }



    }


    @Test
    fun urlBuilderTest() {
        val build = URLBuilder().apply {
            this.toKhlBuild(V3, "/gateway/index")
        }.build()

        println(build)
        println(build.encodedPath)

        println("=================")

        val build2 = URLBuilder().apply {
            this.toKhlBuild(V3, "gateway/index")
        }.build()

        println(build2)
        println(build2.encodedPath)


    }


}