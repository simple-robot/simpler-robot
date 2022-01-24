import love.forte.simbot.utils.RandomIDUtil
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 *
 * @author ForteScarlet
 */
class UUIDTest {
    @OptIn(ExperimentalTime::class)
    @Test
    fun test() {
        val time = measureTime {
            repeat(1_000_000) {
                RandomIDUtil.randomID()
            }
        }
        println(time)
        repeat(10) {
            println(RandomIDUtil.randomID())
        }
    }
}