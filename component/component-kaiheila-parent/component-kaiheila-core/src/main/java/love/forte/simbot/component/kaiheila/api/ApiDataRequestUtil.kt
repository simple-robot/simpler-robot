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

@file:JvmName("ApiDataReuestUtil")

package love.forte.simbot.component.kaiheila.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import love.forte.simbot.component.kaiheila.khlJson


/**
 * 请求一个 [ApiData.Req] 并得到一个对应的 [响应体][ApiData.Resp].
 */
public suspend inline fun <reified HTTP_RESP : ApiData.Resp<*>> ApiData.Req<HTTP_RESP>.doRequest(
    apiVersion: ApiVersion,
    client: HttpClient,
): HTTP_RESP {

    var apiPath: List<String> = emptyList()

    val responseContent = client.request<String> {
        contentType(ContentType.Application.Json)
        this@doRequest.authorization?.let { authorization ->
            header("Authorization", "Bot $authorization")
        }
        url {
            val routeInfoBuilder = RouteInfoBuilder.getInstance(parameters)
            this@doRequest.route(routeInfoBuilder)

            apiPath = routeInfoBuilder.apiPath

            routeInfoBuilder.body?.let { b -> body = b }
            this.toKhlBuild(apiVersion, apiPath)
        }
    }

    // resp.status.let { status ->
    //     if (!status.isSuccess()) {
    //         throw ClientRequestException(resp, "$status ")
    //     }
    // }


    // val contentText = resp.readText(Charsets.UTF_8)

    val data = khlJson.decodeFromString(deserializer = this.dataSerializer, responseContent)

    return data.check { apiPath.joinToString("/") }
}


public suspend inline fun <D, reified HTTP_RESP : ApiData.Resp<out D>> ApiData.Req<HTTP_RESP>.doRequestForData(
    apiVersion: ApiVersion,
    client: HttpClient,
): D = doRequest(apiVersion, client).data

