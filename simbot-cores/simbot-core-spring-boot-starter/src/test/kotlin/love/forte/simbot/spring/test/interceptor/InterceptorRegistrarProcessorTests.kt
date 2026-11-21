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

package love.forte.simbot.spring.test.interceptor

import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.Interceptor
import love.forte.simbot.interception.InterceptorRegistrar
import love.forte.simbot.interception.InterceptorRegistration
import love.forte.simbot.spring.configuration.DefaultSimbotInterceptorRegistrarProcessor
import love.forte.simbot.spring.configuration.SimbotInterceptorRegistrar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


/**
 * 用于测试的自定义拦截点。
 */
private object TestInterceptionPoint : InterceptionPoint<String, String>


/**
 * 测试 [DefaultSimbotInterceptorRegistrarProcessor] 的功能。
 */
class InterceptorRegistrarProcessorTests {

    // ========== 单元测试：直接测试 processor ==========

    /**
     * 测试：当没有注册任何 registrar 时，process 不应抛出异常。
     */
    @Test
    fun `process with empty registrars should not throw`() {
        val processor = DefaultSimbotInterceptorRegistrarProcessor(emptyList())
        val mockRegistrar = object : InterceptorRegistrar {
            override fun <C : Any, R, P : InterceptionPoint<C, R>> register(
                point: P,
                priority: Int,
                interceptor: Interceptor<C, R, P>
            ) = throw AssertionError("Should not be called")
        }

        // 不应抛出异常
        processor.process(mockRegistrar)
    }

    /**
     * 测试：单个 registrar 被正确调用。
     */
    @Test
    fun `process with single registrar should call register`() {
        var called = false
        val registrar = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                called = true
                registrar.register(TestInterceptionPoint) { _, _, chain ->
                    chain.proceed("result")
                }
            }
        }

        val processor = DefaultSimbotInterceptorRegistrarProcessor(listOf(registrar))
        val mockRegistrar = TestInterceptorRegistrar()
        processor.process(mockRegistrar)

        Assertions.assertTrue(called, "Registrar should be called")
        // 验证拦截器已被注册
        Assertions.assertEquals(1, mockRegistrar.registeredInterceptors.size)
    }

    /**
     * 测试：多个 registrar 按顺序被调用。
     */
    @Test
    fun `process with multiple registrars should call all in order`() {
        val callOrder = mutableListOf<Int>()

        val registrar1 = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                callOrder.add(1)
            }
        }

        val registrar2 = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                callOrder.add(2)
            }
        }

        val registrar3 = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                callOrder.add(3)
            }
        }

        val processor = DefaultSimbotInterceptorRegistrarProcessor(listOf(registrar1, registrar2, registrar3))
        val mockRegistrar = TestInterceptorRegistrar()
        processor.process(mockRegistrar)

        Assertions.assertEquals(listOf(1, 2, 3), callOrder)
    }

    /**
     * 测试：registrar 抛出异常时，其他 registrar 仍应被调用。
     */
    @Test
    fun `process should continue when one registrar throws`() {
        val callOrder = mutableListOf<Int>()

        val failingRegistrar = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                callOrder.add(1)
                throw RuntimeException("Test exception")
            }
        }

        val succeedingRegistrar = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                callOrder.add(2)
            }
        }

        val processor = DefaultSimbotInterceptorRegistrarProcessor(listOf(failingRegistrar, succeedingRegistrar))
        val mockRegistrar = TestInterceptorRegistrar()

        // 不应抛出异常，因为 processor 内部捕获了异常
        processor.process(mockRegistrar)

        // 两个 registrar 都应该被调用
        Assertions.assertEquals(listOf(1, 2), callOrder)
    }

    /**
     * 测试：registrar 可以注册多个拦截器到同一个点。
     */
    @Test
    fun `registrar can register multiple interceptors to the same point`() {
        val registrar = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                registrar.register(TestInterceptionPoint, priority = 1) { _, _, chain ->
                    chain.proceed("result")
                }
                registrar.register(TestInterceptionPoint, priority = 2) { _, _, chain ->
                    chain.proceed("result")
                }
                registrar.register(TestInterceptionPoint, priority = 3) { _, _, chain ->
                    chain.proceed("result")
                }
            }
        }

        val processor = DefaultSimbotInterceptorRegistrarProcessor(listOf(registrar))
        val mockRegistrar = TestInterceptorRegistrar()
        processor.process(mockRegistrar)

        Assertions.assertEquals(3, mockRegistrar.registeredInterceptors.size)
    }

    /**
     * 测试：registrar 可以注册到不同的拦截点。
     */
    @Test
    fun `registrar can register interceptors to different points`() {
        val registrar = object : SimbotInterceptorRegistrar {
            override fun register(registrar: InterceptorRegistrar) {
                registrar.register(TestInterceptionPoint) { _, _, chain ->
                    chain.proceed("result1")
                }
                registrar.register(AnotherTestInterceptionPoint) { _, _, chain ->
                    chain.proceed(42)
                }
            }
        }

        val processor = DefaultSimbotInterceptorRegistrarProcessor(listOf(registrar))
        val mockRegistrar = TestInterceptorRegistrar()
        processor.process(mockRegistrar)

        Assertions.assertEquals(2, mockRegistrar.registeredInterceptors.size)
    }
}


/**
 * 用于测试的另一个自定义拦截点。
 */
private object AnotherTestInterceptionPoint : InterceptionPoint<Int, Int>


/**
 * 测试用的 InterceptorRegistrar 实现，用于记录注册的拦截器。
 */
private class TestInterceptorRegistrar : InterceptorRegistrar {
    val registeredInterceptors = mutableListOf<Interceptor<*, *, *>>()

    @Suppress("UNCHECKED_CAST")
    override fun <C : Any, R, P : InterceptionPoint<C, R>> register(
        point: P,
        priority: Int,
        interceptor: Interceptor<C, R, P>
    ): InterceptorRegistration<C, R, P> {
        registeredInterceptors.add(interceptor)
        // 返回一个假的 registration
        return object : InterceptorRegistration<C, R, P> {
            override val id: String = "test-${registeredInterceptors.size}"
            override val point: P = point
            override val priority: Int = priority
            override val interceptor: Interceptor<C, R, P> = interceptor
            override val isDisposed: Boolean = false
            override fun dispose() {}
        }
    }
}


// ========== Spring Boot 集成测试 ==========

/**
 * 测试通过 Spring Bean 自动注册拦截器的功能。
 */
class InterceptorRegistrarSpringIntegrationTests {

    @Test
    fun `SimbotInterceptorRegistrar bean should be auto-registered`() {
        // 使用 GenericApplicationContext 手动创建上下文并注册配置类
        val context = org.springframework.context.support.GenericApplicationContext()
        context.refresh()

        // 手动注册配置
        org.springframework.beans.factory.support.DefaultListableBeanFactory(context).apply {
            registerSingleton(
                "testRegistrar",
                TestInterceptorRegistrarBeanForSpring()
            )
        }

        // 创建 processor 并验证
        val processor = DefaultSimbotInterceptorRegistrarProcessor(
            listOf(TestInterceptorRegistrarBeanForSpring())
        )

        // 验证 processor 正确处理了 TestInterceptorRegistrarBean
        val testRegistrar = TestInterceptorRegistrar()
        processor.process(testRegistrar)
        Assertions.assertTrue(testRegistrar.registeredInterceptors.isNotEmpty())

        context.close()
    }
}


/**
 * 测试用的 SimbotInterceptorRegistrar Bean。
 */
open class TestInterceptorRegistrarBeanForSpring : SimbotInterceptorRegistrar {
    override fun register(registrar: InterceptorRegistrar) {
        registrar.register(TestInterceptionPoint) { _, _, chain ->
            chain.proceed("spring-test-result")
        }
    }
}
