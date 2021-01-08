/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

@file:JvmName("ErrorMsgSenders")
package love.forte.simbot.api.sender

import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.api.SimbotApiException


public class UnusableApiException : SimbotApiException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    // constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
    //     message,
    //     cause,
    //     enableSuppression,
    //     writableStackTrace
    // )
}


@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun NO(api: String): Nothing = throw UnusableApiException("This api cannot be used: $api")


internal inline val FalseCarrier: Carrier<Boolean> get() = false.toCarrier()


