/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     GatewayApiConstant.kt
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

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import love.forte.simbot.component.kaiheila.khlJson


object GatewayApiConstant {

    const val clientId = "jqdlyHK85xe1i5Bo"
    const val token = "1/MTAyNTA=/246ZJ1bTE3tq5kd0vHaLZg=="
    const val clientSecret = "teYfprPTddOe6deh"

}

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(khlJson)
    }
    install(WebSockets)
}

const val guildId = "6865507942900765"
