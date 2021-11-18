/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmSynthetic
@file:JvmMultifileClass
@file:JvmName("LoggerFactories")
package love.forte.simbot

import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.reflect.KClass


/**
 * 字符串的国际化获取，
 * 约定国际化语言路径为 `lang.message`.
 *
 * JVM实现中使用 @PropertyKey 进行约束。
 */
@Suppress("NOTHING_TO_INLINE")
@get:JsName("toI18n")
public expect val String.i18n: String


public expect object I18n {
    /**
     * 如果找不到，抛出异常
     *
     * @throws NullPointerException
     */
    public operator fun get(key: String): String
    public val keys: Iterator<String>
    public val locale: String
}


/**
 * 日志工厂, 用于得到一个日志实例.
 * @author ForteScarlet
 */
public expect object LoggerFactory {
    @JvmStatic
    @JsName("getLoggerByName")
    public fun getLogger(name: String): Logger

    @JvmStatic
    public fun getLogger(type: KClass<*>): Logger
}


/**
 * 日志实例。
 * 日志级别：trace debug info warn error
 */
public expect interface Logger {
    public fun getName(): String

    //region trace
    public fun isTraceEnabled(): Boolean

    @JsName("trace")
    public fun trace(msg: String)

    @JsName("traceFormat1")
    public fun trace(format: String, arg: Any)

    @JsName("traceFormat2")
    public fun trace(format: String, arg1: Any, arg2: Any)

    @JsName("traceError")
    public fun trace(msg: String, t: Throwable)
    //endregion

    //region debug
    public fun isDebugEnabled(): Boolean

    @JsName("debug")
    public fun debug(msg: String)

    @JsName("debugFormat1")
    public fun debug(format: String, arg: Any)

    @JsName("debugFormat2")
    public fun debug(format: String, arg1: Any, arg2: Any)

    @JsName("debugError")
    public fun debug(msg: String, t: Throwable)
    //endregion

    //region info
    public fun isInfoEnabled(): Boolean

    @JsName("info")
    public fun info(msg: String)

    @JsName("infoFormat1")
    public fun info(format: String, arg: Any)

    @JsName("infoFormat2")
    public fun info(format: String, arg1: Any, arg2: Any)

    @JsName("infoError")
    public fun info(msg: String, t: Throwable)
    //endregion

    //region warn
    public fun isWarnEnabled(): Boolean

    @JsName("warn")
    public fun warn(msg: String)

    @JsName("warnFormat1")
    public fun warn(format: String, arg: Any)

    @JsName("warnFormat2")
    public fun warn(format: String, arg1: Any, arg2: Any)

    @JsName("warnError")
    public fun warn(msg: String, t: Throwable)
    //endregion

    //region error
    public fun isErrorEnabled(): Boolean

    @JsName("error")
    public fun error(msg: String)

    @JsName("errorFormat1")
    public fun error(format: String, arg: Any)

    @JsName("errorFormat2")
    public fun error(format: String, arg1: Any, arg2: Any)

    @JsName("errorError")
    public fun error(msg: String, t: Throwable)
    //endregion


}

@get:JvmSynthetic
public val Logger.name: String
    get() = getName()
