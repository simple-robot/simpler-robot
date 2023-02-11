/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.logger


/**
 * 日志对象类型，用于输出日志到目标处（例如控制台）。
 *
 * ## JVM
 * 在JVM平台目标会将 [Logger] 作为 [org.slf4j.Logger] 使用。
 *
 * ## JS
 * 在JS平台中会通过 `kotlin.js.Console` 向控制台输出日志信息。详见平台实现说明。
 *
 * ## Native
 * 在native平台中则会直接使用 [println] 简单的向控制台输出日志信息。
 *
 * @author ForteScarlet
 */
@Suppress("KDocUnresolvedReference")
public expect interface Logger {
    /**
     * 当前logger的名称。
     */
    public fun getName(): String
    
    // region Level: TRACE
    
    /**
     * 是否允许 [LogLevel.TRACE] 级别的日志输出.
     */
    public fun isTraceEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.TRACE] 级别日志。
     */
    public fun trace(log: String)
    
    /**
     * 输出 [LogLevel.TRACE] 级别日志。
     */
    public fun trace(log: String, vararg arg: Any?)
    // endregion
    
    // region Level: DEBUG
    /**
     * 是否允许 [LogLevel.DEBUG] 级别的日志输出.
     */
    public fun isDebugEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.DEBUG] 级别日志。
     */
    public fun debug(log: String)
    
    /**
     * 输出 [LogLevel.DEBUG] 级别日志。
     */
    public fun debug(log: String, vararg arg: Any?)
    // endregion
    
    // region Level: INFO
    /**
     * 是否允许 [LogLevel.INFO] 级别的日志输出.
     */
    public fun isInfoEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.INFO] 级别日志。
     */
    public fun info(log: String)
    
    /**
     * 输出 [LogLevel.INFO] 级别日志。
     */
    public fun info(log: String, vararg arg: Any?)
    // endregion
    
    // region Level: WARN
    /**
     * 是否允许 [LogLevel.WARN] 级别的日志输出.
     */
    public fun isWarnEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public fun warn(log: String)
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public fun warn(log: String, vararg arg: Any?)
    // endregion
    
    // region Level: ERROR
    /**
     * 是否允许 [LogLevel.ERROR] 级别的日志输出.
     */
    public fun isErrorEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public fun error(log: String)
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public fun error(log: String, vararg arg: Any?)
    // endregion
}

public inline val Logger.name: String get() = getName()
public inline val Logger.isTraceEnabled: Boolean get() = isTraceEnabled()
public inline val Logger.isDebugEnabled: Boolean get() = isDebugEnabled()
public inline val Logger.isInfoEnabled: Boolean get() = isInfoEnabled()
public inline val Logger.isWarnEnabled: Boolean get() = isWarnEnabled()
public inline val Logger.isErrorEnabled: Boolean get() = isErrorEnabled()


