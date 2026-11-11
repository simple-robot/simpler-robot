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

import love.forte.simbot.common.collection.*
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.Interceptor
import love.forte.simbot.interception.InterceptorRegistration
import kotlin.concurrent.Volatile
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.uuid.Uuid

/**
 * 拦截器管理器。
 *
 * 每次注册生成 UUID 字符串，注册返回值与查询结果是同一个对象。
 * 同一个点按优先级升序、同优先级按注册顺序执行，查询具有弱一致性并会过滤已注销的拦截器。
 *
 * 注销先原子地标记失效，再清理相关索引。查询及执行均会检查失效标记，
 * 已收集到的执行视图在注销后会直接放行，不会进入拦截逻辑。
 * 已经进入原始逻辑的调用不被中断，只会影响迭代时的可见性和调用时的逻辑有效性。
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
public class SimpleInterceptorManager : InterceptorManager {
    @OptIn(ExperimentalAtomicApi::class)
    private class RegisteredInterceptor<C : Any, R, P : InterceptionPoint<C, R>>(
        val delegate: Interceptor<C, R, P>
    ) : Interceptor<C, R, P> {
        private val disposed = AtomicBoolean(false)
        val isDisposed: Boolean get() = disposed.load()

        fun dispose(): Boolean = disposed.compareAndSet(expectedValue = false, newValue = true)

        override suspend fun intercept(point: P, context: C, chain: Interceptor.Chain<C, R>): R {
            if (isDisposed) return chain.proceed(context)
            return delegate.intercept(point, context, chain)
        }
    }

    private class Registration<C : Any, R, P : InterceptionPoint<C, R>>(
        override val id: String,
        override val point: P,
        override val priority: Int,
        override val interceptor: RegisteredInterceptor<C, R, P>,
        manager: SimpleInterceptorManager
    ) : InterceptorRegistration<C, R, P> {
        @Volatile
        private var manager: SimpleInterceptorManager? = manager

        override val isDisposed: Boolean get() = interceptor.isDisposed

        override fun dispose() {
            tryDispose()
        }

        fun tryDispose(): Boolean {
            if (!interceptor.dispose()) return false
            manager?.remove(this)
            manager = null
            return true
        }

        override fun toString(): String =
            "InterceptorRegistration(id=$id, point=$point, priority=$priority, isDisposed=$isDisposed)"
    }

    private val interceptorMap =
        concurrentMutableMap<InterceptionPoint<*, *>, PriorityConcurrentQueue<Registration<*, *, *>>>()
    private val registrationsById = concurrentMutableMap<String, Registration<*, *, *>>()

    private fun remove(registration: Registration<*, *, *>) {
        interceptorMap.computeValueIfPresent(registration.point) { _, queue ->
            queue.remove(registration.priority, registration)
            queue.takeUnless { it.isEmpty() }
        }
        registrationsById.removeValue(registration.id) { registration }
    }

    override fun <C : Any, R, P : InterceptionPoint<C, R>> register(
        point: P,
        priority: Int,
        interceptor: Interceptor<C, R, P>
    ): InterceptorRegistration<C, R, P> {
        val registeredInterceptor = RegisteredInterceptor(interceptor)
        val uuid = Uuid.random().toString()
        val registration = Registration(uuid, point, priority, registeredInterceptor, this)

        registrationsById[uuid] = registration
        interceptorMap.computeValue(point) { _, current ->
            val queue = current ?: createPriorityConcurrentQueue()
            queue.add(priority, registration)
            queue
        }

        return registration
    }

    override fun <C : Any, R, P : InterceptionPoint<C, R>> interceptors(
        point: P
    ): Sequence<InterceptorRegistration<C, R, P>> = sequence {
        val queue = interceptorMap[point] ?: return@sequence
        for (registration in queue) {
            if (!registration.isDisposed) {
                @Suppress("UNCHECKED_CAST")
                yield(registration as InterceptorRegistration<C, R, P>)
            }
        }
    }

    override val interceptors: Sequence<InterceptorRegistration<*, *, *>>
        get() = sequence {
            for (queue in interceptorMap.values) {
                for (registration in queue) {
                    if (!registration.isDisposed) {
                        yield(registration)
                    }
                }
            }
        }

    override fun hasInterceptors(point: InterceptionPoint<*, *>): Boolean =
        interceptorMap[point]?.any { !it.isDisposed } == true

    override fun findById(id: String): InterceptorRegistration<*, *, *>? =
        registrationsById[id]?.takeUnless { it.isDisposed }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Any, R, P : InterceptionPoint<C, R>> findById(
        id: String,
        point: P
    ): InterceptorRegistration<C, R, P>? =
        findById(id)?.takeIf { it.point == point } as? InterceptorRegistration<C, R, P>

    override fun disposeById(id: String): Boolean = registrationsById[id]?.tryDispose() ?: false
}
