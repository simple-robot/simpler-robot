package love.forte.simbot.logger.test

import love.forte.simbot.logger.getOnMax
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class LoggerFactoryTest {

    @Test
    fun getLogger() {
        assert("ForteScarlet".getOnMax(7) == "Scarlet")
        assert("ForteScarlet".getOnMax(12) == "ForteScarlet")
        assert("ForteScarlet".getOnMax(0) == "")
    }


}