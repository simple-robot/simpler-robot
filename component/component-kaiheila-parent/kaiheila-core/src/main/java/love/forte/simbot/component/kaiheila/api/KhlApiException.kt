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


public inline fun <R : ApiData.Resp<*>> checkResponse(resp: R, api: (resp: R) -> String): R {
    if (resp.code != 0) {
        throw KhlApiHttpResponseException(throwMsg(api(resp), resp))
    }
    return resp
}


public inline fun <R : ApiData.Resp<*>> R.check(msg: (resp: R) -> String) = checkResponse(this, msg)


public fun <R : ApiData.Resp<*>> throwMsg(api: String, resp: R): String = buildString {
    append("code: ").append(resp.code).appendLine()
    append("message: ").append(resp.message).appendLine()
}
