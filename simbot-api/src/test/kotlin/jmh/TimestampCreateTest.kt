package jmh

import love.forte.simbot.Timestamp
import love.forte.simbot.toTimestamp
import org.openjdk.jmh.annotations.*
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * 测试 [Timestamp] 实例的两种对当前时间的构建速度。
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
@Threads(8)
open class TimestampCreateTest {
    /*
        Benchmark                                   Mode  Cnt          Score         Error  Units
        TimestampCreateTest.timestampByInstantNow  thrpt   25  237486404.132 ± 2671045.055  ops/s
        TimestampCreateTest.timestampByNow         thrpt   25  258600996.469 ± 2419138.248  ops/s
     */
    
    @Benchmark
    fun timestampByNow() {
        Timestamp.now()
    }
    
    @Benchmark
    fun timestampByInstantNow() {
        Instant.now().toTimestamp()
    }
}
