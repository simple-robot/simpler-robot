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

package love.forte.simbot.kaiheila.api.v3

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*


/**
 *
 * 开黑啦 v3-api的 [gateway](https://developer.kaiheila.cn/doc/http/gateway) 获取接口的请求参数。
 *
 * @author ForteScarlet
 */
public data class GatewayReq(val compress: Int = 1) :
    ApiData.Req<ObjectResp<love.forte.simbot.kaiheila.api.v3.Gateway>> {
    override val dataSerializer = love.forte.simbot.kaiheila.api.v3.GatewayReq.Key.dataSerializer

    override val method: HttpMethod
        get() = HttpMethod.Get

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = love.forte.simbot.kaiheila.api.v3.GatewayReq.Key.ROUTE
        builder.parameters {
            append("compress", compress)
        }
        // builder.parametersAppender.append("compress", compress.toString())
    }

    override val body: Any? get() = null

    companion object Key : ApiData.Req.Key by key("/gateway/index") {
        private val dataSerializer = objectResp(love.forte.simbot.kaiheila.api.v3.Gateway.serializer())
        private val ROUTE = listOf("gateway", "index")
    }

    override val key: ApiData.Req.Key get() = love.forte.simbot.kaiheila.api.v3.GatewayReq.Key
}



@Serializable
public data class Gateway(val url: String) : love.forte.simbot.kaiheila.api.v3.BaseV3RespData()

