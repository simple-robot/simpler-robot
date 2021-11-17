import love.forte.simbot.LoggerFactory
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class ResourceTest {

    @Test
    fun test() {
        val logger =  LoggerFactory.getLogger("Test")
        logger.trace("trace!")
        logger.debug("debug!")
        logger.info("info!!")
        logger.warn("warn!!")
        logger.error("error!!")
    }

}