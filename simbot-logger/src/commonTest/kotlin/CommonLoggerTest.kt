import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class CommonLoggerTest {
    
    @Test
    fun infoTest() {
        val logger = LoggerFactory.logger<CommonLoggerTest>()
        logger.info("Hello")
        logger.info("Hello {}", "World")
        logger.info("Hello {} {}", "World", RuntimeException(RuntimeException()))
        logger.info("Hello {}", "World", RuntimeException(RuntimeException()))
    }
}