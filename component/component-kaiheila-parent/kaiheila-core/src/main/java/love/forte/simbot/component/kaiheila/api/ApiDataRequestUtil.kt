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

@file:JvmName("ApiDataRequestUtil")

package love.forte.simbot.component.kaiheila.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import love.forte.simbot.component.kaiheila.khlJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.contracts.ExperimentalContracts


public val logger: Logger = LoggerFactory.getLogger(ApiData::class.java)


/**
 * 请求一个 [ApiData.Req] 并得到一个对应的 [响应体][ApiData.Resp].
 * [token] 存在的时候，[authorizationType] 必须存在.
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <reified HTTP_RESP : ApiData.Resp<*>> ApiData.Req<HTTP_RESP>.doRequest(
    api: Api,
    client: HttpClient,
    token: String? = null,
    authorizationType: AuthorizationType = AuthorizationType.BOT,
): HTTP_RESP {
    var apiPath: List<String> = emptyList()

    val responseContent = client.request<String> {

        contentType(ContentType.Application.Json)
        method = this@doRequest.method

        token?.let { auth ->
            header("Authorization", authorizationType.getAuthorization(auth))
        }
        url {
            val routeInfoBuilder = RouteInfoBuilder.getInstance(parameters)
            this@doRequest.route(routeInfoBuilder)

            apiPath = routeInfoBuilder.apiPath

            this@doRequest.body?.let { b -> body = b }

            this.toKhlBuild(api, apiPath)
        }
    }

    // resp.status.let { status ->
    //     if (!status.isSuccess()) {
    //         throw ClientRequestException(resp, "$status ")
    //     }
    // }

    // val log = when (val key = this.key) {
    //     is LogAble -> key.log
    //     else -> logger
    // }

    println(responseContent)

    // val contentText = resp.readText(Charsets.UTF_8)

    val data = khlJson.decodeFromString(deserializer = this.dataSerializer, responseContent)

    return data.check { apiPath.joinToString("/") }
}


public suspend inline fun <D, reified HTTP_RESP : ApiData.Resp<out D>> ApiData.Req<HTTP_RESP>.doRequestForData(
    api: Api,
    client: HttpClient,
    token: String? = null,
    authorizationType: AuthorizationType = AuthorizationType.BOT,
): D = doRequest(api, client, token, authorizationType).data

