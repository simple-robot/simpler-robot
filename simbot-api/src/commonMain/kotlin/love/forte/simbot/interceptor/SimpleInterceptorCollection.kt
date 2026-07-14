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

package love.forte.simbot.interceptor

import love.forte.simbot.common.collection.ConcurrentQueue
import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.PriorityConcurrentQueue
import love.forte.simbot.common.collection.createPriorityConcurrentQueue

/**
 * 基于并发队列的简单 [InterceptorCollection] 实现。
 *
 * 该实现适合用于基础注册场景：调用 [add] 追加拦截器，
 * 调用 [all] 以当前队列顺序获取拦截器序列。
 * 序列是否反映遍历期间的新增元素取决于底层 [ConcurrentQueue] 的行为。
 *
 * ```Kotlin
 * val set = SimpleInterceptorSet()
 * set.add(TextInterceptor { context -> context.invoke() })
 * ```
 *
 * @since 5.0
 * @author Forte Scarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
internal class SimpleInterceptorCollection : InterceptorCollection {
    private val interceptors: PriorityConcurrentQueue<Interceptor<*, *>> = createPriorityConcurrentQueue()

    override val size: Int
        get() = interceptors.size

    /**
     * 向集合末尾追加一个 [interceptor]。
     *
     * ```Kotlin
     * set.add(TextInterceptor { context -> context.invoke() })
     * ```
     */
    fun add(priority: Int, interceptor: Interceptor<*, *>) {
        interceptors.add(priority, interceptor)
    }

    /**
     * 以当前队列顺序返回全部拦截器。
     */
    override fun all(): Sequence<Interceptor<*, *>> = interceptors.asSequence()
}
