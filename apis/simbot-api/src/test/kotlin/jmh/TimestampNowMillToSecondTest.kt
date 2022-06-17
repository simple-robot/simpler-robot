package jmh

import love.forte.simbot.Timestamp
import love.forte.simbot.toTimestamp
import org.openjdk.jmh.annotations.*
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * 测试 [Timestamp] 获取指定时间的毫秒值并转化为秒值。
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
@Threads(2)
@Fork(2)
open class TimestampNowMillToSecondTest {
    /*
        Benchmark                                                    Mode  Cnt          Score          Error  Units
        TimestampNowMillToSecondTest.instantToSecond                thrpt   10  385155789.485 ±  8926544.193  ops/s
        TimestampNowMillToSecondTest.ktDurationToSecond             thrpt   10  231423079.305 ± 15373222.469  ops/s
        TimestampNowMillToSecondTest.timeUnitToSecond               thrpt   10  806603961.872 ± 62111656.924  ops/s
        TimestampNowMillToSecondTest.timestampByInstantToSecond     thrpt   10  502221466.431 ± 35491343.696  ops/s
        TimestampNowMillToSecondTest.timestampToSecond              thrpt   10  509223233.597 ± 42807784.315  ops/s
     */
    
    companion object {
        val time: Long
            get() {
                return Random.nextLong(1655430000000, 1655430457196L)
            }
    }
    
    @Benchmark
    fun timestampToSecond() {
        Timestamp.byMillisecond(time).second
    }
    
    @Benchmark
    fun timestampByInstantToSecond() {
        Instant.ofEpochSecond(time).toTimestamp().second
    }
    
    @Benchmark
    fun instantToSecond() {
        Instant.ofEpochMilli(time).epochSecond
    }
    
    @Benchmark
    fun timeUnitToSecond() {
        TimeUnit.MILLISECONDS.toSeconds(time)
    }
    
    @Benchmark
    fun ktDurationToSecond() {
        time.milliseconds.inWholeSeconds
    }
    
    
}
