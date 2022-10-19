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
    override val level: LogLevel,
) : AbstractSimpleLogger() {
    override fun getName(): String = name
    
    
    override fun trace0(formattedLog: String, throwable: Throwable?) {
        console.log("[trace]", "[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.log(it) }
    }
    
    override fun debug0(formattedLog: String, throwable: Throwable?) {
        console.log("[debug]", "[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.log(it) }
    }
    
    override fun info0(formattedLog: String, throwable: Throwable?) {
        console.info("[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.info(it) }
    }
    
    override fun warn0(formattedLog: String, throwable: Throwable?) {
        console.warn("[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.warn(it) }
    }
    
    override fun error0(formattedLog: String, throwable: Throwable?) {
        console.error("[$displayName]:", formattedLog)
        throwable?.stackTraceToString()?.also { console.error(it) }
    }
}