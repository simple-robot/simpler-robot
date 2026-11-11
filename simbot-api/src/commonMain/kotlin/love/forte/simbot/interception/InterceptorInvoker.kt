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

import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.common.collection.concurrentMutableMap
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * 拦截器执行器，一个用于辅助进行拦截器使用的工具性质的类型。
 *
 * [InterceptorInvoker] 是一次性执行器，[invoke] 只允许调用一次。
 *
 * @param C 非空业务上下文类型。
 * @param R 操作返回值类型。
 *
 * @since 5.0
 */
public interface InterceptorInvoker<C : Any, R> {
    /**
     * 使用 [context] 执行整条拦截链，并返回链的结果。
     * 未处理的异常和取消异常会直接向上传播。
     * @throws IllegalStateException 如果 [invoke] 被重复执行
     */
    public suspend fun invoke(context: C): R
}

/**
 * 构建 [InterceptorInvoker]。
 *
 * 每个拦截器的 `proceed` 原则上只建议**调用一次**。
 * 重复调用不会因调用次数而报错，每次都会执行相同的后续拦截链和最终目标并产生所有可能产生的副作用。
 * 拦截点可以进一步限制重复或并发调用。执行器本身不保证用户拦截器和上下文的线程安全。
 *
 * @param point 拦截点标记。
 * @param interceptors 拦截器的可迭代器，执行期间由执行器按需迭代。
 * @param attributes 各层 chain 共享的属性，默认通过 [concurrentMutableMap] 创建。
 * @param interceptedTarget 最终目标。每次后续链到达终点时执行，或也可能因短路而不执行。
 *
 * @since 5.0
 */
public fun <C : Any, R, P : InterceptionPoint<C, R>> InterceptorInvoker(
    point: P,
    interceptors: Iterable<Interceptor<C, R, P>>,
    attributes: MutableAttributeMap = mutableAttributeMapOf(concurrentMutableMap()),
    interceptedTarget: suspend (C) -> R,
): InterceptorInvoker<C, R> {
    return InterceptorInvoker(point, interceptors.iterator(), attributes, interceptedTarget)
}

/**
 * 使用迭代器构建按需读取后续链的 [InterceptorInvoker]，不预先收集或复制全部拦截器。
 *
 * 每个拦截器的 `proceed` 原则上只建议**调用一次**。
 * 重复调用不会因调用次数而报错，每次都会执行相同的后续拦截链和最终目标并产生所有可能产生的副作用。
 * 拦截点可以进一步限制重复或并发调用。执行器本身不保证用户拦截器和上下文的线程安全。
 *
 * @param point 拦截点标记。
 * @param interceptors 交给本执行器独占消费的迭代器，调用方不得再主动推进它。并发修改来源是否有效，由来源的迭代器契约决定。
 * @param attributes 各层 chain 共享的属性，默认通过 [concurrentMutableMap] 创建。
 * @param interceptedTarget 每次后续链到达终点时执行的最终目标，短路时可能不执行。
 * @since 5.0
 */
public fun <C : Any, R, P : InterceptionPoint<C, R>> InterceptorInvoker(
    point: P,
    interceptors: Iterator<Interceptor<C, R, P>>,
    attributes: MutableAttributeMap = mutableAttributeMapOf(concurrentMutableMap()),
    interceptedTarget: suspend (C) -> R,
): InterceptorInvoker<C, R> = InterceptorInvokerImpl(point, interceptors, attributes, interceptedTarget)

/**
 * 惰性后继串执行链。
 * 已访问部分可重复执行，未访问部分不提前消耗迭代器。
 *
 * @since 5.0
 * @author ForteScarlet
 */
@OptIn(ExperimentalAtomicApi::class)
private class InterceptorInvokerImpl<C : Any, R, P : InterceptionPoint<C, R>>(
    /**
     * 拦截点标记
     */
    private val point: P,
    private val interceptors: Iterator<Interceptor<C, R, P>>,
    private val attributes: MutableAttributeMap,
    /**
     * 最终被拦截的真实逻辑目标
     */
    private val interceptedTarget: suspend (C) -> R,
) : InterceptorInvoker<C, R> {
    private val invoked = AtomicBoolean(false)

    override suspend fun invoke(context: C): R {
        if (!invoked.compareAndSet(expectedValue = false, newValue = true)) {
            error("InterceptorInvoker has already invoked")
        }

        return invoke0().invoke(context)
    }

    /**
     * 为当前拦截器构建执行闭包，在继续执行时递归构建并缓存其后继目标。
     */
    private fun invoke0(): suspend (C) -> R {
        if (!interceptors.hasNext()) return interceptedTarget

        val current = interceptors.next()
        // 避免 lazy 在迭代器读取失败后重试并意外推进到不同位置。
        val next by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { runCatching { invoke0() } }
        val chain = Interceptor.Chain.create<C, R>(attributes) { context ->
            next.getOrThrow().invoke(context)
        }

        return { context -> current.intercept(point, context, chain) }
    }
}
