package love.forte.simbot.logger

import kotlin.reflect.KClass
import org.slf4j.LoggerFactory as Slf4jLoggerFactory

/**
 * [Logger] 的构建工厂.
 *
 * 委托给 [Slf4jLoggerFactory].
 *
 * @see Slf4jLoggerFactory
 */
public actual object LoggerFactory {
    /**
     * 根据名称获取一个 [Logger] 实例。
     *
     * @see Slf4jLoggerFactory.getLogger
     */
    @JvmStatic
    public actual fun getLogger(name: String): Logger = Slf4jLoggerFactory.getLogger(name)
    
    /**
     *
     * @see Slf4jLoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(type: KClass<*>): Logger = getLogger(type.java)
    
    /**
     *
     * @see Slf4jLoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(type: Class<*>): Logger = Slf4jLoggerFactory.getLogger(type)
}

/**
 * just like `LoggerFactory.getLogger(T::class)`
 */
public actual inline fun <reified T> LoggerFactory.logger(): Logger = getLogger(T::class.java)