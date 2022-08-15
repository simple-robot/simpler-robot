package love.forte.simbot.logger.test

import love.forte.simbot.logger.DefaultSimbotLoggerProcessorsFactory
import love.forte.simbot.logger.SimbotLoggerFactory
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class LoggerTest {
    private val logger = SimbotLoggerFactory(DefaultSimbotLoggerProcessorsFactory.getProcessors()).getLogger("LoggerTest")
    
    @Test
    fun log() {
        Thread.sleep(100)
        logger.info("{}, {}", "Hello", "World", RuntimeException())
        logger.info("{}", "Hello", "World", RuntimeException())
        logger.info("{}, {}, {}", "Hello", "World", RuntimeException())
        Thread.sleep(100)
    }
}