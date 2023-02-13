/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbotest.duration

import org.openjdk.jmh.annotations.*
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * 测试不同的毫秒转秒和秒转毫秒
 * @author ForteScarlet
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.MINUTES)
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.MINUTES)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class DurationTest {
    
    private inline val randomSecond: Long get() = Random.nextLong(100_000, 100_000_000)
    private inline val randomMilli: Long get() = Random.nextLong(100_000_000, 100_000_000_000L)
    
    @Benchmark
    @Group("MilliToSecond")
    fun timeUnitMilliToSecond(): Long {
        return TimeUnit.MILLISECONDS.toSeconds(randomMilli)
    }
    
    @Benchmark
    @Group("MilliToSecond")
    fun ktDurationMilliToSecond(): Long {
        return randomMilli.milliseconds.inWholeSeconds
    }
    
    @Benchmark
    @Group("MilliToSecond")
    fun javaDurationMilliToSecond(): Long {
        return Duration.ofMillis(randomMilli).seconds
    }
    
    @Benchmark
    @Group("SecondToMilli")
    fun timeUnitSecondToMilli(): Long {
        return TimeUnit.SECONDS.toMillis(randomSecond)
    }
    
    @Benchmark
    @Group("SecondToMilli")
    fun ktDurationSecondToMilli(): Long {
        return randomSecond.seconds.inWholeMilliseconds
    }
    
    @Benchmark
    @Group("SecondToMilli")
    fun javaDurationSecondToMilli(): Long {
        return Duration.ofSeconds(randomSecond).toMillis()
    }
    
}
