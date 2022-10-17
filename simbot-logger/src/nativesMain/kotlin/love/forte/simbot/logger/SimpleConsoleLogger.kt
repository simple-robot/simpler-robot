package love.forte.simbot.logger

import love.forte.simbot.logger.internal.AbstractSimpleLogger
import love.forte.simbot.logger.internal.toDisplayName


/**
 *
 * @author ForteScarlet
 */
internal class SimpleConsoleLogger(
    private val name: String,
    override val displayName: String = name.toDisplayName(),
    override val level: LogLevel = LogLevel.INFO
): AbstractSimpleLogger() {
    override fun getName(): String = name
    
    private fun printLog(shortLevel: String, log: String, throwable: Throwable?) {
        println("[$displayName] $shortLevel: $log")
        throwable?.printStackTrace()
    }
    
    override fun trace0(formattedLog: String, throwable: Throwable?) {
        printLog("t", formattedLog, throwable)
    }
    
    override fun debug0(formattedLog: String, throwable: Throwable?) {
        printLog("d", formattedLog, throwable)
    }
    
    override fun info0(formattedLog: String, throwable: Throwable?) {
        printLog("i", formattedLog, throwable)
    }
    
    override fun warn0(formattedLog: String, throwable: Throwable?) {
        printLog("w", formattedLog, throwable)
    }
    
    override fun error0(formattedLog: String, throwable: Throwable?) {
        printLog("e", formattedLog, throwable)
    }
}