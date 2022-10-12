package love.forte.simbot.logger


/**
 * 日志。
 * @author ForteScarlet
 */
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


