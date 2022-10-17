package love.forte.simbot.logger

/**
 * 日志。
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
