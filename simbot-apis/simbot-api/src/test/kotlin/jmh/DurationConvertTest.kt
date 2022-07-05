package jmh

import love.forte.simbot.JavaDuration
import love.forte.simbot.toJavaDurationDirect
import love.forte.simbot.toKotlinDuration
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds


/**
 *
 * @author ForteScarlet
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 1, time = 30, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.MINUTES)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Threads(2)
open class DurationConvertTest {
    
    private inline val randomMinutes: Int get() = Random.nextInt(0, 30000)
    
    @Benchmark
    // @Group("MinutesDuration")
    fun minutesTimeUnitToKotlinDuration() = TimeUnit.MINUTES.toNanos(randomMinutes.toLong()).nanoseconds
    
    @Benchmark
    // @Group("MinutesDuration")
    fun kotlinMinutesDurationToJavaDuration() = randomMinutes.minutes.toJavaDurationDirect()
    
    @Benchmark
    // @Group("MinutesDuration")
    fun javaMinutesDurationToKotlinDuration(): Duration = JavaDuration.ofMinutes(randomMinutes.toLong()).toKotlinDuration()
    
    
    private inline val randomSeconds: Int get() = Random.nextInt(0, 180000)
    
    
    @Benchmark
    // @Group("SecondsDuration")
    fun kotlinSecondsDurationToJavaDuration() = randomSeconds.seconds.toJavaDurationDirect()
    
    @Benchmark
    // @Group("SecondsDuration")
    fun javaSecondsDurationToKotlinDuration() = JavaDuration.ofSeconds(randomSeconds.toLong()).toKotlinDuration()
    
    @Benchmark
    // @Group("SecondsDuration")
    fun secondsTimeUnitToKotlinDuration() = TimeUnit.SECONDS.toNanos(randomSeconds.toLong()).nanoseconds

    /*
        @Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.MINUTES)
        @Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.MINUTES)
    
        Benchmark                             Mode  Cnt       Score   Error   Units
        DurationConvertTest.MinutesDuration  thrpt    2  180193.034          ops/ms
        DurationConvertTest.SecondsDuration  thrpt    2  259193.630          ops/ms
     */
    
}