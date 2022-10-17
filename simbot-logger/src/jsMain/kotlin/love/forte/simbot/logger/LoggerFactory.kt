package love.forte.simbot.logger

public actual object LoggerFactory {
    
    /**
     * 配置当前全局使用的日志等级。
     */
    public var globalLevel: LogLevel = LogLevel.INFO
    
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = getLogger(name, globalLevel)
    
    /**
     * 根据名称和日志级别获取一个 [Logger] 实例。
     */
    public fun getLogger(name: String, level: LogLevel): Logger = TODO()
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    // T::class.qualifiedName ?:
    return getLogger(T::class.simpleName ?: T::class.toString())
}