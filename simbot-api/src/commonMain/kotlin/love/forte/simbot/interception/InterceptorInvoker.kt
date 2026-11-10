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

package love.forte.simbot.interception

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * 拦截器执行器，一个用于辅助进行拦截器使用的工具性质的类型。
 *
 * [InterceptorInvoker] 是一次性地执行器，[invoke] 只允许调用一次。
 *
 * @since 5.0
 */
public interface InterceptorInvoker<C : Interceptor.Context, R, P : InterceptionPoint<C, R>> {
    /**
     * 执行拦截器逻辑
     * @throws IllegalStateException 如果 [invoke] 被重复执行
     */
    public suspend fun invoke(context: C): R
}

/**
 * 构建 [InterceptorInvoker]。
 *
 * @param point 拦截点标记
 * @param interceptors 拦截器列表
 * @param interceptedTarget 拦截目标逻辑
 *
 * @since 5.0
 */
public fun <C : Interceptor.Context, R, P : InterceptionPoint<C, R>> InterceptorInvoker(
    point: P,
    interceptors: Collection<Interceptor<C, R, P>>,
    interceptedTarget: suspend (C) -> R,
): InterceptorInvoker<C, R, P> {
    return if (interceptors.isEmpty()) {
        EmptyInterceptorInvoker(interceptedTarget)
    } else {
        InterceptorInvokerImpl(point, interceptors.iterator(), interceptedTarget)
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class EmptyInterceptorInvoker<C : Interceptor.Context, R, P : InterceptionPoint<C, R>>(
    private val interceptedTarget: suspend (C) -> R,
) : InterceptorInvoker<C, R, P> {
    private val invoked = AtomicBoolean(false)

    override suspend fun invoke(context: C): R {
        if (!invoked.compareAndSet(expectedValue = false, newValue = true)) {
            error("InterceptorInvoker has already invoked")
        }
        return interceptedTarget(context)
    }
}

/**
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalAtomicApi::class)
private class InterceptorInvokerImpl<C : Interceptor.Context, R, P : InterceptionPoint<C, R>>(
    /**
     * 拦截点标记
     */
    private val point: P,
    /**
     * 拦截器列表
     */
    private val interceptors: Iterator<Interceptor<C, R, P>>,
    /**
     * 最终被拦截的真实逻辑目标
     */
    private val interceptedTarget: suspend (C) -> R,
) : InterceptorInvoker<C, R, P> {
    private val invoked = AtomicBoolean(false)

    override suspend fun invoke(context: C): R {
        if (!invoked.compareAndSet(expectedValue = false, newValue = true)) {
            error("InterceptorInvoker has already invoked")
        }

        return invoke0(context)
    }

    private suspend fun invoke0(context: C): R {
        val hasNext = interceptors.hasNext()
        return if (!hasNext) {
            interceptedTarget(context)
        } else {
            val next = interceptors.next()
            next.intercept(point, context) { c -> invoke0(c) }
        }
    }
}
