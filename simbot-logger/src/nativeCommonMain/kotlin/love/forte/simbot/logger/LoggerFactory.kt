package love.forte.simbot.logger

public actual object LoggerFactory {
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    public actual fun getLogger(name: String): Logger = TODO()
}


public actual inline fun <reified T> LoggerFactory.logger(): Logger {
    return getLogger(T::class.qualifiedName ?: T::class.simpleName ?: T::class.toString())
}