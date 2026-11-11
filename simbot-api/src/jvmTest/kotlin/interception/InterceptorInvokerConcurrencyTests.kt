/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package interception

import kotlinx.coroutines.*
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.Interceptor
import love.forte.simbot.interception.InterceptorInvoker
import org.junit.jupiter.api.Timeout
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * 验证并发继续执行共享固定后继，并且不会并发消费来源迭代器。
 */
@Timeout(10)
class InterceptorInvokerConcurrencyTests {
    private object Point : InterceptionPoint<Int, Int>

    /**
     * 多个并发分支必须完整经过相同后续链，来源中每个元素只能被读取一次。
     */
    @Test
    fun concurrentProceedInitializesEachSuccessorOnce() = runBlocking {
        val middleCalls = AtomicInteger()
        val lastCalls = AtomicInteger()
        val reads = AtomicInteger()
        val head = Interceptor<Int, Int, Point> { _, _, chain ->
            coroutineScope {
                val start = CompletableDeferred<Unit>()
                val branches = (1..32).map { value ->
                    async(Dispatchers.Default) {
                        start.await()
                        chain.proceed(value)
                    }
                }
                start.complete(Unit)
                branches.awaitAll().sum()
            }
        }
        val middle = Interceptor<Int, Int, Point> { _, context, chain ->
            middleCalls.incrementAndGet()
            chain.proceed(context)
        }
        val last = Interceptor<Int, Int, Point> { _, context, chain ->
            lastCalls.incrementAndGet()
            chain.proceed(context)
        }
        val source = object : Iterator<Interceptor<Int, Int, Point>> {
            private val delegate = listOf(head, middle, last).iterator()
            private val reading = AtomicBoolean(false)

            private fun <T> guarded(block: () -> T): T {
                check(reading.compareAndSet(false, true)) { "Concurrent iterator access" }
                try {
                    Thread.yield()
                    return block()
                } finally {
                    reading.set(false)
                }
            }

            override fun hasNext(): Boolean = guarded { delegate.hasNext() }

            override fun next(): Interceptor<Int, Int, Point> = guarded {
                reads.incrementAndGet()
                delegate.next()
            }
        }
        val invoker = InterceptorInvoker(Point, source) {
            delay(1)
            it
        }
        assertEquals(0, reads.get())
        assertEquals(528, withTimeout(5_000) { invoker.invoke(0) })
        assertEquals(3, reads.get())
        assertEquals(32, middleCalls.get())
        assertEquals(32, lastCalls.get())
    }

    /**
     * 同时竞争执行入口也只能有一个调用进入目标，其余调用必须被拒绝。
     */
    @Test
    fun concurrentInvocationsHaveExactlyOneWinner() = runBlocking {
        val start = CompletableDeferred<Unit>()
        val enteredTarget = CompletableDeferred<Unit>()
        val releaseTarget = CompletableDeferred<Unit>()
        val targetCalls = AtomicInteger()
        val invoker = InterceptorInvoker(Point, emptyList()) { context ->
            targetCalls.incrementAndGet()
            enteredTarget.complete(Unit)
            releaseTarget.await()
            context
        }
        val attempts = (1..32).map { context ->
            async(Dispatchers.Default) {
                start.await()
                runCatching { invoker.invoke(context) }
            }
        }

        start.complete(Unit)
        try {
            withTimeout(5_000) { enteredTarget.await() }
        } finally {
            // 断言或等待失败时也必须释放赢家，避免子协程阻止测试结束。
            releaseTarget.complete(Unit)
        }
        val results = withTimeout(5_000) { attempts.awaitAll() }
        assertEquals(1, targetCalls.get())
        assertEquals(1, results.count { it.isSuccess })
        assertEquals(31, results.count { it.isFailure })
        results.filter { it.isFailure }.forEach { assertIs<IllegalStateException>(it.exceptionOrNull()) }
    }
}
