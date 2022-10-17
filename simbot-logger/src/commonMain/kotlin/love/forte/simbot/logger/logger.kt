package love.forte.simbot.logger


/**
 * 日志对象类型，用于输出日志到目标处（例如控制台）。
 *
 * ## JVM
 * 在JVM平台目标会将 [Logger] 作为 [org.slf4j.Logger] 使用。
 *
 * ## JS
 * 在JS平台中会通过 [kotlin.js.console] 向控制台输出日志信息,
 * 并会尝试为不同种类的api分配对应的 [console][kotlin.js.console] api.
 * - [Logger.trace] 使用 [console.log][kotlin.js.console.log]
 * - [Logger.debug] 使用 [console.log][kotlin.js.console.log]
 * - [Logger.info] 使用 [console.info][kotlin.js.console.info]
 * - [Logger.warn] 使用 [console.warn][kotlin.js.console.warn]
 * - [Logger.error] 使用 [console.error][kotlin.js.console.error]
 *
 * ## Native
 * 在native平台中则会直接使用 [println] 简单的向控制台输出日志信息。
 *
 *
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


