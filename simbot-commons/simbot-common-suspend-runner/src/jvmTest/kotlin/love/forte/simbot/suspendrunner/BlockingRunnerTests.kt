/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.*
import love.forte.simbot.annotations.InternalSimbotAPI
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
@OptIn(InternalSimbotAPI::class)
@Timeout(20)
class BlockingRunnerTests {
    @Test
    fun runBlockingExceptionallyTest() {
        runInNoScopeBlocking { runNormally() }

        assertIs<RunException>(
            assertFails {
                runInNoScopeBlocking { runExceptionally1() }
            }
        )

        assertIs<RunException>(
            assertFails {
                runInNoScopeBlocking { runExceptionally2() }
            }
        )
    }

    private suspend fun runNormally() = suspendCancellableCoroutine { continuation ->
        continuation.resume(0)
    }

    private suspend fun runExceptionally1() {
        delay(1)
        throw RunException()
    }

    private suspend fun runExceptionally2() = suspendCancellableCoroutine<Int> { continuation ->
        continuation.resumeWithException(RunException())
    }

    /**
     * 无调度器时也安装线程上下文，挂起恢复后保持一致，返回时恢复调用线程原值。
     */
    @Test
    fun noScopeBridgeInstallsThreadContextAcrossSuspension() {
        assertThreadContext { context, block -> runInNoScopeBlocking(context, block) }
    }

    /**
     * 关闭旧等待日志的入口与普通入口具有相同的上下文和异常语义。
     */
    @Test
    @Suppress("DEPRECATION")
    fun withoutTimeoutDebugRetainsTheSameBridgeSemantics() {
        assertThreadContext { context, block -> runInNoScopeBlockingWithoutTimeoutDebug(context, block) }
        val failure = assertFailsWith<IllegalArgumentException> {
            runInNoScopeBlockingWithoutTimeoutDebug<Int>(EmptyCoroutineContext) {
                throw IllegalArgumentException("business")
            }
        }
        assertEquals("business", failure.message)
    }

    /**
     * 无作用域超时入口仍抛 Java 超时异常，并在返回前完成已取消任务的异步清理。
     */
    @Test
    fun noScopeTimeoutCancelsTheOperationAndCompletesCleanup() {
        var entered = false
        var finished = false
        val failure = assertFailsWith<TimeoutException> {
            runInNoScopeTimeoutBlocking<Int>(100, EmptyCoroutineContext) {
                try {
                    entered = true
                    awaitCancellation()
                } finally {
                    withContext(NonCancellable + Dispatchers.Default) {
                        finished = true
                    }
                }
            }
        }
        assertTrue(entered)
        assertTrue(finished)
        assertIs<TimeoutCancellationException>(failure.cause)
    }

    /**
     * 本次桥接创建父任务的子 Job，取消该子任务不会取消父任务。
     */
    @Test
    fun bridgeOwnsAChildJobWithoutCancellingItsParent() {
        val parent = Job()
        try {
            assertFailsWith<CancellationException> {
                runInNoScopeBlocking<Int>(parent) {
                    val ownJob = currentCoroutineContext().job
                    assertNotSame(parent, ownJob)
                    ownJob.cancel()
                    ownJob.ensureActive()
                    42
                }
            }
            assertTrue(parent.isActive)
            assertFalse(parent.children.any())
        } finally {
            parent.cancel()
        }
    }

    /**
     * 取消传入的父 Job 会停止已挂起的桥接任务并完成其清理。
     */
    @Test
    fun parentCancellationReachesTheSuspendedOperation() {
        val parent = Job()
        val entered = CountDownLatch(1)
        val finished = CountDownLatch(1)
        val release = CompletableDeferred<Unit>()
        val (worker, outcome) = startBlockingCall {
            runInNoScopeBlocking(parent + Dispatchers.Default) {
                try {
                    entered.countDown()
                    release.await()
                    42
                } finally {
                    finished.countDown()
                }
            }
        }
        try {
            assertTrue(entered.await(5, TimeUnit.SECONDS))
            parent.cancel()
            assertIs<CancellationException>(outcome.get(5, TimeUnit.SECONDS).exceptionOrNull())
            assertEquals(0L, finished.count)
        } finally {
            parent.cancel()
            release.complete(Unit)
            finishBlockingCall(worker)
        }
    }

    /**
     * 等待线程中断会取消真实挂起的任务，并在异步 finally 清理结束后才返回。
     */
    @Test
    fun waitingThreadInterruptionCancelsAndWaitsForSuspendedCleanup() {
        val entered = CountDownLatch(1)
        val cleanupEntered = CountDownLatch(1)
        val finished = CountDownLatch(1)
        val release = CompletableDeferred<Unit>()
        val releaseCleanup = CompletableDeferred<Unit>()
        val (worker, outcome) = startBlockingCall {
            runInNoScopeBlocking(Dispatchers.Default) {
                try {
                    entered.countDown()
                    release.await()
                    42
                } finally {
                    withContext(NonCancellable) {
                        cleanupEntered.countDown()
                        releaseCleanup.await()
                        finished.countDown()
                    }
                }
            }
        }
        try {
            assertTrue(entered.await(5, TimeUnit.SECONDS))
            worker.interrupt()
            assertTrue(cleanupEntered.await(5, TimeUnit.SECONDS))
            assertFalse(outcome.isDone)
            releaseCleanup.complete(Unit)
            assertIs<InterruptedException>(outcome.get(5, TimeUnit.SECONDS).exceptionOrNull())
            assertEquals(0L, finished.count)
        } finally {
            release.complete(Unit)
            releaseCleanup.complete(Unit)
            finishBlockingCall(worker)
        }
    }

    /**
     * 同步或真正挂起后的业务异常和取消异常均按原类型与消息传播。
     */
    @Test
    fun failuresKeepTheirTypesBeforeAndAfterSuspension() {
        for (suspendFirst in listOf(false, true)) {
            for (cancellation in listOf(false, true)) {
                val failure = assertFails {
                    runInNoScopeBlocking<Int>(EmptyCoroutineContext) {
                        if (suspendFirst) {
                            val resumed = CompletableDeferred<Unit>()
                            CoroutineScope(currentCoroutineContext()).launch { resumed.complete(Unit) }
                            resumed.await()
                        }
                        if (cancellation) throw CancellationException("cancelled")
                        throw IllegalStateException("business")
                    }
                }
                if (cancellation) {
                    assertIs<CancellationException>(failure)
                    assertEquals("cancelled", failure.message)
                } else {
                    assertIs<IllegalStateException>(failure)
                    assertEquals("business", failure.message)
                }
            }
        }
    }

    /**
     * 无作用域形参不代表脱离结构化并发，本次 Job 启动的子任务必须完成后桥接才返回。
     */
    @Test
    fun bridgeWaitsForChildrenStartedFromItsCurrentContext() {
        val childEntered = CountDownLatch(1)
        val blockReturned = CountDownLatch(1)
        val childFinished = CountDownLatch(1)
        val release = CompletableDeferred<Unit>()
        val (worker, outcome) = startBlockingCall {
            runInNoScopeBlocking(EmptyCoroutineContext) {
                CoroutineScope(currentCoroutineContext()).launch {
                    try {
                        childEntered.countDown()
                        release.await()
                    } finally {
                        childFinished.countDown()
                    }
                }
                blockReturned.countDown()
                42
            }
        }
        try {
            assertTrue(blockReturned.await(5, TimeUnit.SECONDS))
            assertTrue(childEntered.await(5, TimeUnit.SECONDS))
            assertFailsWith<TimeoutException> { outcome.get(100, TimeUnit.MILLISECONDS) }
            release.complete(Unit)
            assertEquals(42, outcome.get(5, TimeUnit.SECONDS).getOrThrow())
            assertEquals(0L, childFinished.count)
        } finally {
            release.complete(Unit)
            finishBlockingCall(worker)
        }
    }

    /**
     * 使用本次协程的子任务驱动挂起恢复，避免借用外部测试作用域。
     */
    private fun assertThreadContext(bridge: (CoroutineContext, suspend () -> Int) -> Int) {
        val local = ThreadLocal<String>()
        val callerThread = Thread.currentThread()
        local.set("outside")
        try {
            val result = bridge(CoroutineName("inside") + local.asContextElement("inside")) {
                assertSame(callerThread, Thread.currentThread())
                assertEquals("inside", local.get())
                assertEquals("inside", currentCoroutineContext()[CoroutineName]?.name)
                val resumed = CompletableDeferred<Unit>()
                CoroutineScope(currentCoroutineContext()).launch {
                    assertEquals("inside", local.get())
                    resumed.complete(Unit)
                }
                resumed.await()
                assertSame(callerThread, Thread.currentThread())
                assertEquals("inside", local.get())
                assertEquals("inside", currentCoroutineContext()[CoroutineName]?.name)
                42
            }
            assertEquals(42, result)
            assertEquals("outside", local.get())
        } finally {
            local.remove()
        }
    }

    /**
     * 用独立线程执行阻塞入口，让测试线程能够确定地发出取消或中断。
     */
    private fun <T> startBlockingCall(block: () -> T): Pair<Thread, CompletableFuture<Result<T>>> {
        val outcome = CompletableFuture<Result<T>>()
        val worker = Thread { outcome.complete(runCatching(block)) }.apply {
            isDaemon = true
            start()
        }
        return worker to outcome
    }

    /**
     * 测试失败时也中断并回收等待线程，避免遗留后台执行。
     */
    private fun finishBlockingCall(worker: Thread) {
        worker.interrupt()
        worker.join(5_000)
        assertFalse(worker.isAlive)
    }


    private class RunException : RuntimeException()
}
