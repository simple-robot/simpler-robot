/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     ApiDataRequestUtil.kt
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

@file:JvmName("ApiDataReuestUtil")
package love.forte.simbot.component.kaiheila.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*


/**
 * 请求一个 [ApiData.Req] 并得到一个对应的 [响应体][ApiData.Resp].
 */
public suspend inline fun <reified HTTP_RESP : ApiData.Resp<*>> ApiData.Req<HTTP_RESP>.doRequest(
    apiVersion: ApiVersion,
    client: HttpClient,
): HTTP_RESP {
    return request(client) {
        // body
        this@doRequest.body?.let { b ->
            this.body = b
        }
        contentType(ContentType.Application.Json)
        this@doRequest.authorization?.let { authorization ->
            header("Authorization", "Bot $authorization")
        }

        // path
        url {
            this.toKhlBuild(apiVersion, this@doRequest.route)
        }
    }
}
