import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.Timestamp
import love.forte.simbot.toTimestamp
import org.testng.annotations.Test
import java.time.Instant

/**
 *
 * @author ForteScarlet
 */
class TimestampTest {
    
    @Test
    fun serializerTest() {
        val t1 = Timestamp.NotSupport
        val t2 = Instant.now().toTimestamp()
        val list = listOf(t1, t2)
        val json = Json.encodeToString(list)
        
        val timeMillList = Json.decodeFromString<List<Long>>(json)
        assert(timeMillList.size == 2)
        assert(timeMillList.filter { it < 0 }.size == 1)
        assert(timeMillList.filter { it > 0 }.size == 1)
        
        val timeList = Json.decodeFromString<List<Timestamp>>(json)
        assert(timeList.size == 2)
        assert(timeList.filter { !it.isSupport() }.size == 1)
        assert(timeList.filter { it.isSupport() }.size == 1)
    }
    
    
    @Test
    fun compareTest() {
        val list = listOf(
            Instant.now().toTimestamp(),
            Instant.now().toTimestamp(),
            Instant.now().toTimestamp(),
            Timestamp.notSupport(),
            Timestamp.notSupport(),
            Timestamp.notSupport(),
            Instant.now().toTimestamp(),
            Instant.now().toTimestamp(),
            Instant.now().toTimestamp(),
        )
        
        val newList = list.sorted()
        
        assert(newList.first() === Timestamp.NotSupport)
    }
    
    
    @Test(groups = ["构建性能"], description = "通过Timestamp.now()构建实例测试")
    fun currentTimestampByNowTest() {
        repeat(5000_000) {
            Timestamp.now()
        }
    }
    
    @Test(groups = ["构建性能"], description = "通过Instant.now().toTimestamp()构建实例测试")
    fun currentTimestampByInstantTest() {
        repeat(5000_000) {
            Instant.now().toTimestamp()
        }
    }
    
    
}