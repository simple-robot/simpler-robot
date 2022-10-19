import love.forte.simbot.logger.internal.toDisplayName
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class LogDisplayNameTest {
    
    @Test
    fun displayNameTest() {
        assertEquals("    love.forte.simbot.simbot.simbot", "love.forte.simbot.simbot.simbot".toDisplayName())
        assertEquals("l.forte.simbot.simbot.simbot.simbot", "love.forte.simbot.simbot.simbot.simbot".toDisplayName())
        assertEquals("l.f.simbot.simbot.simbot.simbot.sim", "love.forte.simbot.simbot.simbot.simbot.sim".toDisplayName())
        val value = "love.forte.simbot" + ".simbot".repeat(17)
        assertEquals(".s.s.s.s.s.s.s.s.s.s.s.s.s.s.simbot", value.toDisplayName())
    }
    
}