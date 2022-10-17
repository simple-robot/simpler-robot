import love.forte.simbot.logger.*
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class LoggerTest {
    
    @Test
    fun infoTest() {
        val logger = LoggerFactory.logger<LoggerTest>()
        logger.info("Hello")
        logger.info("Hello {}", "World")
        logger.info("Hello {} {}", "World", RuntimeException(RuntimeException()))
        logger.info("Hello {}", "World", RuntimeException(RuntimeException()))
    }
    
    @Test
    fun debugTest() {
        fun Logger.doLog() {
            debug("Hello")
            debug("Hello {}", "World")
        }
        LoggerFactory.logger<LoggerTest>().doLog()
        LoggerFactory.globalLoggerLevel = LogLevel.DEBUG
        println("ok↓")
        LoggerFactory.logger<LoggerTest>().doLog()
    }
    
}