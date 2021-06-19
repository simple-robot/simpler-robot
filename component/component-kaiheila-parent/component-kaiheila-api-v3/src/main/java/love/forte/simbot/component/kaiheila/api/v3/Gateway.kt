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

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.ObjectResp


/**
 *
 * 开黑啦 v3-api的 [gateway](https://developer.kaiheila.cn/doc/http/gateway) 获取接口的请求参数。
 *
 * @author ForteScarlet
 */
public data class GatewayReq(val compress: Int = 1, override val authorization: String) :
    ApiData.Req<ObjectResp<Gateway>> {

    override suspend fun request(client: HttpClient, block: HttpRequestBuilder.() -> Unit): ObjectResp<Gateway> {
        return client.get(block = block)
    }

    override val route: String
        get() = "/gateway/index?compress=$compress"


    override val body: Any? = null


}



@Serializable
public data class Gateway(val url: String) : ApiData.Resp.Data
