package love.forte.simbot.logger


private var globalLoggerLevel: LogLevel = LogLevel.INFO

/**
 * [LoggerFactory] 所使用的全局变量。
 */
public var LoggerFactory. globalLoggerLevel: LogLevel by ::globalLoggerLevel

@Suppress("MemberVisibilityCanBePrivate")
public actual object LoggerFactory {
    
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = getLogger(name, globalLoggerLevel)
    
    /**
     * 根据名称和日志级别获取一个 [Logger] 实例。
     */
    public fun getLogger(name: String, level: LogLevel): Logger = ConsoleLogger(name = name, level = level)
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    return getLogger(T::class.qualifiedName ?: T::class.simpleName ?: T::class.toString())
}