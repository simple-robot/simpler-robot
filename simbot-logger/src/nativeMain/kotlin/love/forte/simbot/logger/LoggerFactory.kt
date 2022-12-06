package love.forte.simbot.logger


private var defaultLoggerLevel0: LogLevel = LogLevel.INFO

/**
 * [LoggerFactory] 所使用的全局默认日志等级。
 */
public var LoggerFactory.defaultLoggerLevel: LogLevel by ::defaultLoggerLevel0

@Suppress("MemberVisibilityCanBePrivate")
public actual object LoggerFactory {
    
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = getLogger(name, defaultLoggerLevel)
    
    /**
     * 根据名称和日志级别获取一个 [Logger] 实例。
     */
    public fun getLogger(name: String, level: LogLevel): Logger = SimpleConsoleLogger(name = name, level = level)
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    return getLogger(T::class.qualifiedName ?: T::class.simpleName ?: T::class.toString())
}