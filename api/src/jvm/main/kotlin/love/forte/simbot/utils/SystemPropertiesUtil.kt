package love.forte.simbot.utils


/**
 * 尝试获取系统参数。
 */
public actual fun systemProperties(key: String): String? = System.getProperty(key)