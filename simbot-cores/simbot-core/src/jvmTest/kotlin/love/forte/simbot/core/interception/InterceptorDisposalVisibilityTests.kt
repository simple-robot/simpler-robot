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
package love.forte.simbot.core.interception

import kotlinx.coroutines.runBlocking
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.InterceptorInvoker
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.*

@Timeout(10)
class InterceptorDisposalVisibilityTests {
    /**
     * 两种注销入口都应先使查询和缓存执行视图失效，重复注销不能等待原清理完成。
     */
    @Test
    fun allQueriesFilterDisposedRegistrationBeforePhysicalRemoval() {
        for (byId in listOf(false, true)) {
            val enteredRemoval = CountDownLatch(1)
            val releaseRemoval = CountDownLatch(1)
            val point = object : InterceptionPoint<String, String> {
                /**
                 * 在进入索引清理前暂停注销线程，使测试无需依赖随机调度。
                 */
                override fun hashCode(): Int {
                    if (Thread.currentThread().name == "blocked-interceptor-disposal") {
                        enteredRemoval.countDown()
                        check(releaseRemoval.await(5, TimeUnit.SECONDS))
                    }
                    return 42
                }
            }
            val manager = SimpleInterceptorManager()
            val delegateCalls = AtomicInteger()
            val registration = manager.register(point) { _, c, chain ->
                delegateCalls.incrementAndGet()
                chain.proceed(c)
            }
            val saved = registration.interceptor
            val byPoint = manager.interceptors(point)
            val all = manager.interceptors
            val executor = Executors.newSingleThreadExecutor { runnable ->
                Thread(runnable, "blocked-interceptor-disposal").apply { isDaemon = true }
            }
            try {
                val disposal = executor.submit<Boolean> {
                    if (byId) {
                        manager.disposeById(registration.id)
                    } else {
                        registration.dispose()
                        true
                    }
                }
                assertTrue(enteredRemoval.await(5, TimeUnit.SECONDS))
                // remove(point) 尚未进入 Map，两个索引仍保留此对象，仅失效标记已改变。
                assertTrue(registration.isDisposed)
                assertNull(manager.findById(registration.id))
                assertNull(manager.findById(registration.id, point))
                assertFalse(manager.disposeById(registration.id))
                registration.dispose()
                assertFalse(manager.hasInterceptors(point))
                assertTrue(byPoint.none())
                assertTrue(all.none())
                assertEquals("target", runBlocking {
                    InterceptorInvoker(point, listOf(saved)) { "target" }.invoke("context")
                })
                assertEquals(0, delegateCalls.get())
                releaseRemoval.countDown()
                assertTrue(disposal.get(5, TimeUnit.SECONDS))
            } finally {
                releaseRemoval.countDown()
                executor.shutdownNow()
            }
        }
    }
}
