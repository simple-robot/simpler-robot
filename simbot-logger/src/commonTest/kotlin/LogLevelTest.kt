import love.forte.simbot.logger.LogLevel
import love.forte.simbot.logger.compareTo
import love.forte.simbot.logger.level
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class LogLevelTest {
    
    @Test
    fun logLevelCompareTest() {
        assertTrue(LogLevel.ERROR.level > LogLevel.WARN.level)
        assertEquals(LogLevel.WARN.level, LogLevel.WARN.level)
        assertTrue(LogLevel.ERROR.level > LogLevel.INFO.level)
    }
    
}