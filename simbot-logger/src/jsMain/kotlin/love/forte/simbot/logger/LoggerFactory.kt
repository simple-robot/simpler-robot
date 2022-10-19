package love.forte.simbot.logger

public actual object LoggerFactory {
    
    /**
     * 配置当前全局使用的默认日志等级。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public var defaultLevel: LogLevel = LogLevel.INFO
    
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = getLogger(name, defaultLevel)
    
    /**
     * 根据名称和日志级别获取一个 [Logger] 实例。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun getLogger(name: String, level: LogLevel): Logger = SimpleConsoleLogger(name = name, level = level)
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    // T::class.qualifiedName ?:
    return getLogger(T::class.simpleName ?: T::class.toString())
}