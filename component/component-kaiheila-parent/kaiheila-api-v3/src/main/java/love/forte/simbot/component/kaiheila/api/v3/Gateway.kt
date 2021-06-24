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

package love.forte.simbot.component.kaiheila.api.v3

import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*


/**
 *
 * 开黑啦 v3-api的 [gateway](https://developer.kaiheila.cn/doc/http/gateway) 获取接口的请求参数。
 *
 * @author ForteScarlet
 */
public data class GatewayReq(val compress: Int = 1) :
    ApiData.Req<ObjectResp<Gateway>> {
    override val dataSerializer = Key.dataSerializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
        builder.parametersBuilder.append("compress", compress.toString())
    }

    override val body: Any? get() = null

    private companion object Key : ApiData.Req.Key by key("/gateway/index") {
        private val dataSerializer = objectResp(Gateway.serializer())
        val ROUTE = listOf("gateway", "index")
    }

    override val key: ApiData.Req.Key get() = Key
}



@Serializable
public data class Gateway(val url: String) : ApiData.Resp.Data
