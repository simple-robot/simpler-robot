import love.forte.simbot.ID
import love.forte.simbot.definition.Category
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class CategoryKTest {
    
    @Test
    fun equalsTest() {
        val c1 = Category(114.ID)
        val c2 = Category("114")
        val c3 = Category(114.ID, "114")
        
        assert(c1 == c2)
        assert(c1 == c3)
        assert(c2 == c3)
        
    }
    
    
}