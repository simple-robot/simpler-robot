/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
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

