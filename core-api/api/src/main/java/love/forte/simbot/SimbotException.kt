/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     SimbotException.kt
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

package love.forte.simbot


/**
 * simbot 的异常实例接口。
 */
public interface SimbotError

/**
 * simbot 的运行时异常实例接口。
 */
public interface SimbotRuntimeError : SimbotError


public interface SimbotIllegalStateError : SimbotRuntimeError

public interface SimbotIllegalArgumentError : SimbotRuntimeError


/**
 * simbot 的运行时异常。
 */
public open class SimbotRuntimeException : RuntimeException, SimbotRuntimeError {
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


/**
 * simbot的异常接口
 */
public open class SimbotException : Exception, SimbotError {
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


/**
 * simbot 的 [IllegalStateException] 异常实例。
 */
public open class SimbotIllegalStateException : IllegalStateException, SimbotIllegalStateError {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}


/**
 * simbot 的 [IllegalArgumentException] 异常实例。
 */
public open class SimbotIllegalArgumentException : IllegalArgumentException, SimbotIllegalArgumentError {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}


/**
 * simbot的预期内的异常。
 */
public open class SimbotExpectedException : IllegalStateException, SimbotIllegalStateError {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
