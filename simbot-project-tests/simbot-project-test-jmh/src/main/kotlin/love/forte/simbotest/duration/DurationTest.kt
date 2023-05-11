/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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
