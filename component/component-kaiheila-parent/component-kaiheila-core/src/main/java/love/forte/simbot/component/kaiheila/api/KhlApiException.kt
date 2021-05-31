/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     KhlApiException.kt
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

@file:JvmName("KhlApiExceptions")

package love.forte.simbot.component.kaiheila.api

import love.forte.simbot.component.kaiheila.KhlRuntimeException


/**
 *
 * 开黑啦api相关异常
 *
 * @author ForteScarlet
 */
public open class KhlApiException : KhlRuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace)
}


public open class KhlApiHttpResponseException : KhlApiException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace)
}



public inline fun <D, R : KhlHttpResp<D>> checkResponse(apiName: String? = null, resp: R, msg: (apiName: String?, resp: R) -> String = ::throwMsg): D {
    if (resp.code != 0) {
        throw KhlApiHttpResponseException(msg(apiName, resp))
    }
    return resp.data
}


public fun <R : KhlHttpResp<*>> throwMsg(apiName: String?, resp: R): String = "code: ${resp.code}, message: ${resp.message}"

