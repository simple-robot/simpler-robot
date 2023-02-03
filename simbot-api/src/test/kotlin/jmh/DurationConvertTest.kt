package jmh

import love.forte.simbot.JavaDuration
import love.forte.simbot.kotlin
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.toKotlinDuration

private inline val randomMinutes: Int get() = Random.nextInt(0, 30000)
private inline val randomSeconds: Int get() = Random.nextInt(0, 180000)

/**
 * 不同时间单位类型转化间的差距。
 * @author ForteScarlet
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 1, time = 3, timeUnit = TimeUnit.MINUTES)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
open class DurationConvertTest {
    
    
    @Benchmark
    @Group("MinutesDuration")
    fun javaMinutesDuration() {
        JavaDuration.ofMinutes(randomMinutes.toLong()).toKotlinDuration()
    }
    
    @Benchmark
    @Group("MinutesDuration")
    fun javaMinutesDurationSimbot() {
        JavaDuration.ofMinutes(randomMinutes.toLong()).kotlin
    }
    
    @Benchmark
    @Group("MinutesDuration")
    fun minutesTimeUnit() {
        TimeUnit.MINUTES.toNanos(randomMinutes.toLong()).nanoseconds
    }
    
    @Benchmark
    @Group("SecondsDuration")
    fun javaSecondsDuration() {
        JavaDuration.ofSeconds(randomSeconds.toLong()).toKotlinDuration()
    }
    
    @Benchmark
    @Group("SecondsDuration")
    fun javaSecondsDurationSimbot() {
        JavaDuration.ofSeconds(randomSeconds.toLong()).kotlin
    }
    
    @Benchmark
    @Group("SecondsDuration")
    fun secondsTimeUnit() {
        TimeUnit.SECONDS.toNanos(randomSeconds.toLong()).nanoseconds
    }
    
}

