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
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.util.*

/**
 * @see WebSockets.Feature
 */
object MyWebSockets : HttpClientFeature<WebSockets.Config, WebSockets> {
    override val key: AttributeKey<WebSockets> get() = WebSockets.key

    /**
     * @see WebSockets.Feature.install
     */
    override fun install(feature: WebSockets, scope: HttpClient) {
        TODO("Not yet implemented")
    }

    /**
     * @see WebSockets.Feature.prepare
     */
    override fun prepare(block: WebSockets.Config.() -> Unit): WebSockets {
        TODO("Not yet implemented")
    }
}