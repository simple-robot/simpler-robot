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
 * 日志。
 *
 * 在JS平台中会通过 [kotlin.js.console] 向控制台输出日志信息,
 * 并会尝试为不同种类的api分配对应的 [console][kotlin.js.Console] api.
 * - [Logger.trace] 使用 [console.log][kotlin.js.Console.log]
 * - [Logger.debug] 使用 [console.log][kotlin.js.Console.log]
 * - [Logger.info] 使用 [console.info][kotlin.js.Console.info]
 * - [Logger.warn] 使用 [console.warn][kotlin.js.Console.warn]
 * - [Logger.error] 使用 [console.error][kotlin.js.Console.error]
 *
 * @author ForteScarlet
 */
public actual interface Logger {
    /**
     * 当前logger的名称。
     */
    public actual fun getName(): String
    
    /**
     * 是否允许 [LogLevel.TRACE] 级别的日志输出.
     */
    public actual fun isTraceEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.TRACE] 级别日志。
     */
    public actual fun trace(log: String)
    
    /**
     * 输出 [LogLevel.TRACE] 级别日志。
     */
    public actual fun trace(log: String, vararg arg: Any?)
    
    /**
     * 是否允许 [LogLevel.DEBUG] 级别的日志输出.
     */
    public actual fun isDebugEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.DEBUG] 级别日志。
     */
    public actual fun debug(log: String)
    
    /**
     * 输出 [LogLevel.DEBUG] 级别日志。
     */
    public actual fun debug(log: String, vararg arg: Any?)
    
    /**
     * 是否允许 [LogLevel.INFO] 级别的日志输出.
     */
    public actual fun isInfoEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.INFO] 级别日志。
     */
    public actual fun info(log: String)
    
    /**
     * 输出 [LogLevel.INFO] 级别日志。
     */
    public actual fun info(log: String, vararg arg: Any?)
    
    /**
     * 是否允许 [LogLevel.WARN] 级别的日志输出.
     */
    public actual fun isWarnEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun warn(log: String)
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun warn(log: String, vararg arg: Any?)
    
    /**
     * 是否允许 [LogLevel.ERROR] 级别的日志输出.
     */
    public actual fun isErrorEnabled(): Boolean
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun error(log: String)
    
    /**
     * 输出 [LogLevel.WARN] 级别日志。
     */
    public actual fun error(log: String, vararg arg: Any?)
}
