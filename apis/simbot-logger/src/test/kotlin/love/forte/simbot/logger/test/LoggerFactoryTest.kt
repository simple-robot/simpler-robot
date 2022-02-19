package love.forte.simbot.logger.test

import org.slf4j.LoggerFactory
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class LoggerFactoryTest {

    @Test
    fun getLogger() {
        val logger = LoggerFactory.getLogger("Hello World")

        logger.info("Info {}", "Message")

    }


}