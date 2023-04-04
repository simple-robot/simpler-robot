/*
 * Copyright (c) 2023 ForteScarlet.
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

import kotlinx.coroutines.*
import love.forte.simbot.util.stageloop.Stage
import love.forte.simbot.util.stageloop.StageLoop
import love.forte.simbot.util.stageloop.State
import love.forte.simbot.util.stageloop.loop
import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAdder

/**
 * 测试使用不同方式的事件循环的性能。
 * @author ForteScarlet
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.MINUTES)
@Warmup(iterations = 1, time = 30)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@org.openjdk.jmh.annotations.State(Scope.Benchmark)
open class StateLoopTest {
    companion object {
        val dispatch =
            ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, LinkedBlockingDeque()) { r ->
                Thread(r).also { it.isDaemon = true }
            }.asCoroutineDispatcher()
    }

    @Param("100")
    var loopTimes: Int = 0

    @Param("true", "false")
    var newEach: Boolean = false

    @Benchmark
    open fun loopByState(): Long {
        val adder = LongAdder()
        TestState.Start(loopTimes, adder, newEach).also {
            runBlocking(dispatch) { it.loop() }
        }
        return adder.sum()
    }

    @Benchmark
    open fun loopByStageLoop(): Long {
        val adder = LongAdder()
        val loop = StageLoop<TestStage>()
        loop.appendStage(TestStage.Start(loopTimes, adder, newEach))
        runBlocking(dispatch) { loop.loop() }
        return adder.sum()
    }

    @Benchmark
    open fun loopSimple(): Long {
        val adder = LongAdder()
        runBlocking(dispatch) {
            repeat(loopTimes) { v ->
                justRun(adder, v.toLong())
            }
        }
        return adder.sum()
    }

    open suspend fun justRun(adder: LongAdder, value: Long) {
        adder.add(value)
//        yield()
//        adder.add(value)
    }

    sealed class TestState : State<TestState>() {
        data class Start(val total: Int, val adder: LongAdder, val newEach: Boolean) : TestState() {
            override suspend fun invoke(): TestState = if (newEach) Loop(adder, total) else LoopSignal(adder, total)
        }

        data class Loop(val adder: LongAdder, val total: Int, private val value: Long = 0) : TestState() {
            override suspend fun invoke(): TestState {
                adder.increment()
//                yield()
//                adder.increment()
                val next = value + 1
                return if (next >= total) End else Loop(adder, total, next)
            }
        }

        data class LoopSignal(val adder: LongAdder, val total: Int) : TestState() {
            private val value: AtomicLong = AtomicLong()
            override suspend fun invoke(): TestState {
                adder.increment()
//                yield()
//                adder.increment()
                return if (value.incrementAndGet() >= total) End else this
            }
        }

        object End : TestState() {
            override suspend fun invoke(): TestState? = null
        }
    }


    sealed class TestStage : Stage<TestStage>() {
        data class Start(val total: Int, val adder: LongAdder, val newEach: Boolean) : TestStage() {
            override suspend fun invoke(loop: StageLoop<TestStage>) {
                if (newEach) {
                    loop.appendStage(Loop(adder, total))
                } else {
                    loop.appendStage(LoopSingle(adder, total))
                }
            }
        }

        data class Loop(val adder: LongAdder, val total: Int, private val value: Long = 0) : TestStage() {
            override suspend fun invoke(loop: StageLoop<TestStage>) {
                adder.increment()
//                yield()
//                adder.increment()
                val next = value + 1
                loop.appendStage(if (next >= total) End else Loop(adder, total, next))
            }
        }

        data class LoopSingle(val adder: LongAdder, val total: Int) : TestStage() {
            private val value: AtomicLong = AtomicLong()
            override suspend fun invoke(loop: StageLoop<TestStage>) {
                adder.increment()
//                yield()
//                adder.increment()
                loop.appendStage(if (value.incrementAndGet() >= total) End else this)
            }
        }

        object End : TestStage() {
            override suspend fun invoke(loop: StageLoop<TestStage>) {
            }
        }
    }
}

/*
Benchmark                      (loopTimes)   Mode  Cnt   Score   Error   Units
StateLoopTest.loopByStageLoop          100  thrpt       11.401          ops/ms
StateLoopTest.loopByStageLoop         5000  thrpt        0.191          ops/ms
StateLoopTest.loopByState              100  thrpt       12.519          ops/ms
StateLoopTest.loopByState             5000  thrpt        0.306          ops/ms
 */

/*
Benchmark                             (loopTimes)  (newEach)   Mode  Cnt   Score   Error   Units
StateLoopTest.loopByStageLoop                 100       true  thrpt       12.954          ops/ms
StateLoopTest.loopByStageLoop                 100      false  thrpt        8.946          ops/ms
StateLoopTest.loopByState                     100       true  thrpt       13.597          ops/ms
StateLoopTest.loopByState                     100      false  thrpt       13.007          ops/ms
StateLoopTest.loopSimple                      100       true  thrpt       14.027          ops/ms
StateLoopTest.loopSimple                      100      false  thrpt       14.063          ops/ms

NO YIELD

Benchmark                      (loopTimes)  (newEach)   Mode  Cnt    Score   Error   Units
StateLoopTest.loopByStageLoop          100       true  thrpt        71.067          ops/ms
StateLoopTest.loopByStageLoop          100      false  thrpt        71.051          ops/ms
StateLoopTest.loopByState              100       true  thrpt       111.996          ops/ms
StateLoopTest.loopByState              100      false  thrpt       110.110          ops/ms
StateLoopTest.loopSimple               100       true  thrpt       119.147          ops/ms
StateLoopTest.loopSimple               100      false  thrpt       119.460          ops/ms
 */
