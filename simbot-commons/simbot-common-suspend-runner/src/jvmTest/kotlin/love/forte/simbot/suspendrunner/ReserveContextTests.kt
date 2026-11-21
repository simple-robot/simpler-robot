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
@file:OptIn(love.forte.simbot.annotations.InternalSimbotAPI::class)

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import love.forte.simbot.suspendrunner.reserve.*
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*
import kotlinx.coroutines.rx2.awaitSingleOrNull as awaitRx2
import kotlinx.coroutines.rx3.awaitSingleOrNull as awaitRx3

/**
 * 响应式 Reserve 继承 scope 的执行环境，但生命周期仍由订阅管理。
 */
class ReserveContextTests {
    /**
     * Mono 保留 scope 环境。
     */
    @Test
    fun monoInheritsScopeEnvironment() = runTest {
        verifyEnvironment("value") { it.transform(mono()).awaitSingle() }
    }

    /**
     * Flux 保留 scope 环境。
     */
    @Test
    fun fluxInheritsScopeEnvironment() = runTest {
        verifyEnvironment(flowOf("value"), expected = listOf("value")) {
            it.transform(flux()).asFlow().toList()
        }
    }

    /**
     * RxJava 2 保留 scope 环境。
     */
    @Test
    fun rx2InheritsScopeEnvironment() = runTest {
        verifyEnvironment("value") { it.transform(rx2Maybe()).awaitRx2() }
    }

    /**
     * RxJava 3 保留 scope 环境。
     */
    @Test
    fun rx3InheritsScopeEnvironment() = runTest {
        verifyEnvironment("value") { it.transform(rx3Maybe()).awaitRx3() }
    }

    /**
     * 显式 context 覆盖 scope 的同 key 元素，同时保留其他 scope 元素。
     */
    @Test
    fun explicitContextOverridesScopeEnvironment() = runTest {
        verifyEnvironment("value", explicit = true) { it.transform(mono()).awaitSingle() }
    }

    /**
     * 使用带 Job 和线程局部值的 scope 检测环境丢失及错误的生命周期继承。
     */
    private suspend fun <T> verifyEnvironment(
        value: T,
        expected: Any? = value,
        explicit: Boolean = false,
        transform: suspend (SuspendReserve<T>) -> Any?
    ) {
        val local = ThreadLocal<String>()
        val parent = currentCoroutineContext()[Job]!!
        val scopeEnvironment = currentCoroutineContext() + Dispatchers.Unconfined +
            CoroutineName("scope") + local.asContextElement("scope")
        val scope = object : CoroutineScope {
            override val coroutineContext = scopeEnvironment
        }
        val name = if (explicit) "explicit" else "scope"
        val context = if (explicit) {
            CoroutineName(name) + local.asContextElement(name)
        } else {
            EmptyCoroutineContext
        }
        val reserve = suspendReserve(scope, context) {
            assertEquals(name, currentCoroutineContext()[CoroutineName]?.name)
            assertEquals(name, local.get())
            assertSame(Dispatchers.Unconfined, currentCoroutineContext()[ContinuationInterceptor])
            assertNotSame(parent, currentCoroutineContext()[Job])
            yield()
            assertEquals(name, local.get())
            value
        }

        assertEquals(expected, transform(reserve))
        assertTrue(parent.isActive)
        assertNull(local.get())
    }
}
