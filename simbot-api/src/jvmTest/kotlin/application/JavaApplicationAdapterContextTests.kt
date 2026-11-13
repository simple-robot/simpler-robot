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

package application

import kotlinx.coroutines.*
import love.forte.simbot.application.Application
import love.forte.simbot.application.JBlockingApplicationLauncher
import love.forte.simbot.application.JBlockingApplicationLauncher.Companion.toEventListener
import love.forte.simbot.application.JBlockingSuspendApplicationEventHandler
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CountDownLatch
import kotlin.test.*

/**
 * 验证 Java 应用生命周期适配器的上下文配置约束。
 */
@Timeout(10)
class JavaApplicationAdapterContextTests {
    /**
     * 生命周期处理器在构造时拒绝替换调用方 Job。
     */
    @Test
    fun blockingHandlerRejectsJobBeforeInvocation() {
        val job = Job()
        try {
            assertFailsWith<IllegalArgumentException> {
                JBlockingSuspendApplicationEventHandler.toHandler(job, JBlockingSuspendApplicationEventHandler<Unit> {})
            }
        } finally {
            job.cancel()
        }
    }

    /**
     * 启动器在构造时拒绝替换调用方 Job，不能延迟到实际启动才检查。
     */
    @Test
    fun blockingLauncherRejectsJobBeforeInvocation() {
        val job = Job()
        val launcher = JBlockingApplicationLauncher<Application> { error("Must not launch") }
        try {
            assertFailsWith<IllegalArgumentException> { launcher.toEventListener(job) }
        } finally {
            job.cancel()
        }
    }

    /**
     * 生命周期回调的局部配置在返回后恢复，不污染调用方上下文。
     */
    @Test
    fun blockingHandlerContextIsLocalToTheCallback() {
        val local = ThreadLocal<String>()
        val javaOnly = ThreadLocal<String>()
        runBlocking(CoroutineName("caller") + local.asContextElement("caller")) {
            var invoked = false
            val handler = JBlockingSuspendApplicationEventHandler.toHandler(
                Dispatchers.IO + CoroutineName("java") + local.asContextElement("java") +
                    javaOnly.asContextElement("java-only"),
                JBlockingSuspendApplicationEventHandler<Unit> {
                    invoked = true
                    assertEquals("java", local.get())
                    assertEquals("java-only", javaOnly.get())
                }
            )
            handler.invoke(Unit)
            assertTrue(invoked)
            assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
            assertEquals("caller", local.get())
            assertNull(javaOnly.get())
        }
        assertNull(local.get())
        assertNull(javaOnly.get())
    }

    /**
     * 父调用取消时，生命周期处理器应中断实际阻塞中的 Java 线程。
     */
    @Test
    fun callerCancellationInterruptsBlockingHandler() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val interrupted = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val release = CountDownLatch(1)
        val handler = JBlockingSuspendApplicationEventHandler.toHandler(
            handler = JBlockingSuspendApplicationEventHandler<Unit> {
                try {
                    entered.complete(Unit)
                    release.await()
                } catch (e: InterruptedException) {
                    interrupted.complete(Unit)
                    throw e
                } finally {
                    finished.complete(Unit)
                }
            }
        )
        withTimeout(5_000) {
            val call = launch { handler.invoke(Unit) }
            try {
                entered.await()
                call.cancelAndJoin()
                assertTrue(call.isCancelled)
                assertTrue(interrupted.isCompleted)
                assertTrue(finished.isCompleted)
            } finally {
                // 实现回归时也释放 Java 线程，避免测试本身遗留永久阻塞。
                release.countDown()
            }
        }
    }

    /**
     * 父调用取消时，启动器应中断实际阻塞中的 Java 线程。
     */
    @Test
    fun callerCancellationInterruptsBlockingLauncher() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val interrupted = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val release = CountDownLatch(1)
        val launcher = JBlockingApplicationLauncher<Application> {
            try {
                entered.complete(Unit)
                release.await()
                error("Launcher should have been interrupted")
            } catch (e: InterruptedException) {
                interrupted.complete(Unit)
                throw e
            } finally {
                finished.complete(Unit)
            }
        }.toEventListener()
        withTimeout(5_000) {
            val call = launch { launcher.launch() }
            try {
                entered.await()
                call.cancelAndJoin()
                assertTrue(call.isCancelled)
                assertTrue(interrupted.isCompleted)
                assertTrue(finished.isCompleted)
            } finally {
                release.countDown()
            }
        }
    }

    /**
     * Java 中断异常转换为取消异常，并保留中断原因。
     */
    @Test
    fun interruptedExceptionsBecomeCancellation() = runBlocking {
        val handlerFailure = InterruptedException("handler interrupted")
        val handler = JBlockingSuspendApplicationEventHandler.toHandler(
            handler = JBlockingSuspendApplicationEventHandler<Unit> { throw handlerFailure }
        )
        val handlerCancellation = assertFailsWith<CancellationException> { handler.invoke(Unit) }
        assertTrue(generateSequence(handlerCancellation as Throwable) { it.cause }.any { it === handlerFailure })

        val launcherFailure = InterruptedException("launcher interrupted")
        val launcher = JBlockingApplicationLauncher<Application> { throw launcherFailure }.toEventListener()
        val launcherCancellation = assertFailsWith<CancellationException> { launcher.launch() }
        assertTrue(generateSequence(launcherCancellation as Throwable) { it.cause }.any { it === launcherFailure })
        currentCoroutineContext().ensureActive()
    }

    /**
     * 普通业务异常直接传播，不应被转换为取消或提前取消父调用。
     */
    @Test
    fun ordinaryFailuresRemainRecoverable() = runBlocking {
        val handler = JBlockingSuspendApplicationEventHandler.toHandler(
            handler = JBlockingSuspendApplicationEventHandler<Unit> { error("handler failed") }
        )
        assertEquals("handler failed", assertFailsWith<IllegalStateException> { handler.invoke(Unit) }.message)

        val launcher = JBlockingApplicationLauncher<Application> { error("launcher failed") }.toEventListener()
        assertEquals("launcher failed", assertFailsWith<IllegalStateException> { launcher.launch() }.message)
        currentCoroutineContext().ensureActive()
    }
}
