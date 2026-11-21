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

package love.forte.simbot.core.application

import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.InterceptorInvoker
import love.forte.simbot.interception.InterceptorManager
import love.forte.simbot.interception.InterceptorRegistration
import love.forte.simbot.plugin.createPlugin
import kotlin.test.*

/**
 * 验证应用工厂向插件传递所属应用的拦截器管理器。
 */
class ApplicationInterceptorManagerTests {
    /**
     * 各应用复用同一拦截点，以验证隔离来自管理器作用域。
     */
    private object Point : InterceptionPoint<String, String>

    /**
     * 插件构建期注册的拦截器可通过应用查询、执行和注销。
     */
    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun pluginAndApplicationShareInterceptorManager() = runTest {
        lateinit var pluginManager: InterceptorManager
        lateinit var registration: InterceptorRegistration<String, String, Point>
        val plugin = createPlugin("interceptor-manager-sharing") {
            pluginManager = interceptorManager
            registration = interceptorManager.register(Point) { _, context, chain ->
                chain.proceed("plugin:$context")
            }
        }
        val application = launchSimpleApplication {
            install(plugin)
        }

        try {
            assertSame(pluginManager, application.interceptorManager)
            assertSame(registration, application.interceptorManager.findById(registration.id))
            val invoker = InterceptorInvoker(
                Point,
                application.interceptorManager.interceptors(Point).map { it.interceptor }.iterator()
            ) { it }
            assertEquals("plugin:message", invoker.invoke("message"))

            assertTrue(application.interceptorManager.disposeById(registration.id))
            assertTrue(registration.isDisposed)
            assertNull(pluginManager.findById(registration.id))
        } finally {
            application.cancel()
            application.join()
        }
    }

    /**
     * 不同应用在相同拦截点上的注册和注销互不影响。
     */
    @Test
    fun applicationsHaveIndependentInterceptorManagers() = runTest {
        val first = launchSimpleApplication()
        val second = launchSimpleApplication()

        try {
            assertNotSame(first.interceptorManager, second.interceptorManager)
            val firstRegistration = first.interceptorManager.register(Point) { _, context, chain ->
                chain.proceed("first:$context")
            }
            val secondRegistration = second.interceptorManager.register(Point) { _, context, chain ->
                chain.proceed("second:$context")
            }

            assertNull(second.interceptorManager.findById(firstRegistration.id))
            assertNull(first.interceptorManager.findById(secondRegistration.id))
            assertFalse(second.interceptorManager.disposeById(firstRegistration.id))
            assertFalse(firstRegistration.isDisposed)
            assertSame(firstRegistration, first.interceptorManager.interceptors(Point).single())
            assertSame(secondRegistration, second.interceptorManager.interceptors(Point).single())

            assertTrue(first.interceptorManager.disposeById(firstRegistration.id))
            assertFalse(first.interceptorManager.hasInterceptors(Point))
            assertFalse(secondRegistration.isDisposed)
            val secondInvoker = InterceptorInvoker(
                Point,
                second.interceptorManager.interceptors(Point).map { it.interceptor }.iterator()
            ) { it }
            assertEquals("second:message", secondInvoker.invoke("message"))
        } finally {
            first.cancel()
            second.cancel()
            first.join()
            second.join()
        }
    }
}
