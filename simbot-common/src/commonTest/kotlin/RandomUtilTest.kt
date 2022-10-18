import love.forte.simbot.utils.randomIdStr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RandomUtilTest {
    
    @Test
    fun idUtil() {
        assertEquals(32, randomIdStr().length)
        assertTrue(randomIdStr().matches(Regex("[0-9a-zA-Z]{32}")))
    }
    
}