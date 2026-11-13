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

/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package event

import kotlinx.coroutines.*
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.event.*
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CountDownLatch
import kotlin.test.*

/**
 * 验证 Java 监听器适配器保留调用方的取消关系。
 */
@OptIn(Api4J::class)
@Timeout(10)
class JavaEventListenerContextTests {
    /**
     * 所有阻塞监听器工厂都应在构造时拒绝替换父 Job。
     */
    @Test
    fun blockingFactoriesRejectJobBeforeInvocation() {
        val job = Job()
        try {
            assertFailsWith<IllegalArgumentException> {
                JBlockEventListener.toListener(job) { EventResult.empty() }
            }
            assertFailsWith<IllegalArgumentException> {
                TypedJBlockEventListener.toListener(job, Event::class.java) { _, _ -> EventResult.empty() }
            }
            assertFailsWith<IllegalArgumentException> {
                block(job) { EventResult.empty() }
            }
            assertFailsWith<IllegalArgumentException> {
                block(job, Event::class.java) { _, _ -> EventResult.empty() }
            }
        } finally {
            job.cancel()
        }
    }

    /**
     * Java 的局部线程上下文在监听器返回后恢复，不影响后续 Kotlin 逻辑。
     */
    @Test
    fun blockingContextIsLocalToTheJavaCallback() {
        val local = ThreadLocal<String>()
        val javaOnly = ThreadLocal<String>()
        runBlocking(CoroutineName("caller") + local.asContextElement("caller")) {
            val expected = EventResult.empty()
            val listener = block(
                Dispatchers.IO + CoroutineName("java") + local.asContextElement("java") +
                    javaOnly.asContextElement("java-only")
            ) {
                assertEquals("java", local.get())
                assertEquals("java-only", javaOnly.get())
                expected
            }
            assertSame(expected, listener.handleWith(listener.testContext()))
            assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
            assertEquals("caller", local.get())
            assertNull(javaOnly.get())
        }
        assertNull(local.get())
        assertNull(javaOnly.get())
    }

    /**
     * 默认阻塞适配器在父调用取消时中断等待中的 Java 操作。
     */
    @Test
    fun callerCancellationInterruptsBlockingListener() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val interrupted = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val listener = JBlockEventListener.toListener {
            try {
                entered.complete(Unit)
                CountDownLatch(1).await()
                EventResult.empty()
            } catch (e: InterruptedException) {
                interrupted.complete(Unit)
                throw e
            } finally {
                finished.complete(Unit)
            }
        }
        withTimeout(5_000) {
            val call = launch { listener.handleWith(listener.testContext()) }
            entered.await()
            call.cancelAndJoin()
            assertTrue(interrupted.isCompleted)
            assertTrue(finished.isCompleted)
        }
    }

    /**
     * 不需要业务事件的回调只使用监听器上下文的身份信息。
     */
    private fun EventListener.testContext(): EventListenerContext = object : EventListenerContext {
        /**
         * 本组测试不读取业务事件。
         */
        override val context: EventContext get() = error("No event data required")

        /**
         * 保留当前监听器身份。
         */
        override val listener: EventListener = this@testContext

        /**
         * 本组测试不匹配消息文本。
         */
        override var plainText: String? = null
    }
}
