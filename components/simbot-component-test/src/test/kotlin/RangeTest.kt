import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class RangeTest {
    @Test
    fun range() {
        repeat(50000) {
            assert((0L..0L).random() == 0L)
        }
    }
}