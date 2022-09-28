package love.forte.simbot.logger.test

import love.forte.simbot.logger.ConsoleSimbotLoggerProcessor
import love.forte.simbot.logger.SimbotLoggerFactory
import org.slf4j.event.Level
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class LoggerTest {
    private val logger = SimbotLoggerFactory(listOf(
        ConsoleSimbotLoggerProcessor(Level.TRACE)
    )).getLogger("forte.forte.forte.forte.forte.forte.forte.LoggerTest")
    
    @Test
    fun log() {
        Thread.sleep(100)
        logger.error("{}, {}", "Hello", "World")
        logger.error("{}, {}", "Hello", "World")
        logger.error("{}, {}", "Hello", "World")
        logger.warn("{}, {}", "Hello", "World")
        logger.warn("{}, {}", "Hello", "World")
        logger.warn("{}, {}", "Hello", "World")
        logger.info("{}, {}", "Hello", "World")
        logger.info("{}, {}", "Hello", "World")
        logger.info("{}, {}", "Hello", "World")
        logger.debug("{}, {}", "Hello", "World")
        logger.debug("{}, {}", "Hello", "World")
        logger.debug("{}, {}", "Hello", "World")
        logger.trace("{}, {}", "Hello", "World")
        logger.trace("{}, {}", "Hello", "World")
        logger.trace("{}, {}", "Hello", "World")
        Thread.sleep(100)
    }
}