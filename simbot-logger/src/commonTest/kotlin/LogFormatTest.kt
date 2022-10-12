import love.forte.simbot.logger.internal.logFormat
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class LogFormatTest {
    
    @Test
    fun formatTest() {
        assertEquals("hello world", "hello {}".logFormat(arrayOf("world")) {
            assertEquals(0, it)
        }, "hello {} with ['world']")
        
        assertEquals("hello world", "hello {}".logFormat(arrayOf("world", "remained")) {
            assertEquals(1, it)
        }, "hello {} with ['world', 'remained']")
        
        assertEquals("hello", "hello".logFormat(arrayOf("world", "remained")) {
            assertEquals(2, it)
        })
        
        assertEquals("hello {}", "hello {}".logFormat(emptyArray<String>()) {
            assertEquals(0, it)
        })
    }
    
}