import love.forte.simbot.logger.*
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class LoggerTest {

    
    @Test
    fun levelChangeTest() {
        fun Logger.doLog() {
            debug("Hello")
            debug("Hello {}", "World")
        }
        LoggerFactory.logger<LoggerTest>().doLog()
        LoggerFactory.defaultLoggerLevel = LogLevel.DEBUG
        println("ok↓")
        LoggerFactory.logger<LoggerTest>().doLog()
    }
    
}