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

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


/**
 * 测试使用不同方式的事件循环的性能。
 * @author ForteScarlet
 */
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 1, time = 2, timeUnit = TimeUnit.MINUTES)
@Warmup(iterations = 1, time = 30, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@Threads(8)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class StageLoopTest {
    companion object {
        private const val LOOP_TIMES = 100_000
    }
    
    sealed class Stage {
        open operator fun invoke() {}
        data class Plus(val num: AtomicLong, val queue: Queue<Stage>) : Stage() {
            override fun invoke() {
                val newNum = num.incrementAndGet()
                if (newNum < LOOP_TIMES) {
                    queue.offer(this)
                } else {
                    queue.offer(Done(newNum))
                }
            }
        }
        
        data class Done(val value: Long) : Stage()
    }

//    @org.openjdk.jmh.annotations.State(Scope.Benchmark)
//    open class LoopByLinkedList {
//        val queue = LinkedList<State>()
//    }
//
//    @Benchmark
//    open fun loopByLinkedList(loop: LoopByLinkedList): Long {
//        return runLoop(loop.queue)
//    }
    
    @State(Scope.Benchmark)
    open class LoopByConcurrentLinkedQueue {
        val queue = ConcurrentLinkedQueue<Stage>().apply { initQueue() }
    }
    
    @Benchmark
    open fun loopByConcurrentLinkedQueue(loop: LoopByConcurrentLinkedQueue): Long {
        return runLoop(loop.queue)
    }
    
    
    @State(Scope.Benchmark)
    open class LoopBySynchronousQueue {
        val queue = SynchronousQueue<Stage>().apply { initQueue() }
    }
    
    @Benchmark
    open fun loopBySynchronousQueue(loop: LoopBySynchronousQueue): Long {
        return runLoop(loop.queue)
    }
    
    @State(Scope.Benchmark)
    open class LoopByArrayBlockingQueueCapacity {
        val queue = ArrayBlockingQueue<Stage>(16).apply { initQueue() }
    }
    
    @Benchmark
    open fun loopByArrayBlockingQueueCapacity(loop: LoopByArrayBlockingQueueCapacity): Long {
        return runLoop(loop.queue)
    }
    
    
    @Suppress("NOTHING_TO_INLINE")
    private inline fun runLoop(queue: Queue<Stage>): Long {
        while (true) {
            val state = queue.poll() ?: return -1
            if (state is Stage.Done) return state.value
            
            state.invoke()
        }
        
    }
    
    
}

private fun Queue<StageLoopTest.Stage>.initQueue() {
    offer(StageLoopTest.Stage.Plus(AtomicLong(0), this))
}

/*
Benchmark                                        Mode  Cnt        Score   Error   Units
StageLoopTest.loopByArrayBlockingQueueCapacity  thrpt         47650.162          ops/ms
StageLoopTest.loopByConcurrentLinkedQueue       thrpt       1877774.364          ops/ms
StageLoopTest.loopBySynchronousQueue            thrpt       1514750.177          ops/ms
 */

