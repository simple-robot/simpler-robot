package love.forte.simbot.logger

import kotlin.jvm.JvmStatic

/**
 * 日志工厂。
 *
 */
public expect object LoggerFactory {
    
    /**
     * 根据名称获取一个 [Logger] 实例。
     */
    @JvmStatic
    public fun getLogger(name: String): Logger
    
}


@Suppress("unused")
public expect inline fun <reified T> LoggerFactory.logger(): Logger