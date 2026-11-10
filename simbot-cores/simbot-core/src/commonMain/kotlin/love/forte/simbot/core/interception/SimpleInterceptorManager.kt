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


/**
 * 一个简单的 [InterceptorManager] 实现，
 * 支持动态的注册、注销，并且注销的拦截器会被瞬时标记并立刻失效。
 *
 * ## 拦截器失效
 * 失效的拦截器在弱一致性的队列中可能不会立刻被移除，但是它会立刻切换到“无条件放行”的逻辑上，
 * 不会再调用原拦截器的拦截逻辑。
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
public class SimpleInterceptorManager : InterceptorManager {
    private class UnregisterableInterceptor<
        C : Interceptor.Context,
        R,
        P : InterceptionPoint<C, R>
        >(val delegate: Interceptor<C, R, P>) : Interceptor<C, R, P>, InterceptorRegistration {
        @Volatile
        private var disposed = false

        override fun dispose() {
            disposed = true
        }

        override suspend fun intercept(point: P, context: C, proceed: suspend (C) -> R): R {
            return if (disposed) {
                proceed(context)
            } else {
                delegate.intercept(point, context, proceed)
            }
        }

        override fun toString(): String {
            return "UnregisterableInterceptor(delegate=$delegate)"
        }
    }

    private val interceptorMap =
        concurrentMutableMap<InterceptionPoint<*, *>, PriorityConcurrentQueue<UnregisterableInterceptor<*, *, *>>>()

    override fun <C : Interceptor.Context, R, P : InterceptionPoint<C, R>> register(
        point: P,
        priority: Int,
        interceptor: Interceptor<C, R, P>
    ): InterceptorRegistration {
        val unregisterableInterceptor = UnregisterableInterceptor(interceptor)
        val queue = interceptorMap.computeValueIfAbsent(point) { createPriorityConcurrentQueue() }
        queue.add(priority, unregisterableInterceptor)
        return unregisterableInterceptor
    }

    override fun <C : Interceptor.Context, R, P : InterceptionPoint<C, R>> interceptors(
        point: P
    ): Sequence<Interceptor<C, R, P>> {
        return interceptorMap[point]?.asSequence()?.map {
            @Suppress("UNCHECKED_CAST")
            it.delegate as Interceptor<C, R, P>
        } ?: emptySequence()
    }

    private data class Entry(
        override val key: InterceptionPoint<*, *>,
        override val value: Interceptor<*, *, *>
    ) : Map.Entry<InterceptionPoint<*, *>, Interceptor<*, *, *>>

    override val interceptors: Sequence<Map.Entry<InterceptionPoint<*, *>, Interceptor<*, *, *>>>
        get() = sequence {
            for ((key, queue) in interceptorMap) {
                for (interceptor in queue) {
                    yield(Entry(key, interceptor.delegate))
                }
            }
        }

    override fun hasInterceptors(point: InterceptionPoint<*, *>): Boolean {
        return point in interceptorMap
    }
}
