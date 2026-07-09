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

@file:JvmName("Interceptors")

package love.forte.simbot.interceptor

import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmName

/**
 * 配置于 [love.forte.simbot.application.Application] 或其他运行环境中的任意一种拦截器，
 * 用于在指定领域的目标逻辑前后插入处理流程。
 *
 * [Interceptor] 只定义最基础的类型约束：拦截器接收一个上下文 [C]，
 * 并返回目标领域约定的结果 [R]。具体的拦截器子类型、上下文内容和结果含义，
 * 由组件、插件或其他上层场景定义。
 *
 * [Interceptor.Context.invoke] 代表继续执行被拦截的内部逻辑。
 * 在 [AggregationInterceptorContext] 中，这通常表示进入下一层拦截器；当没有下一层拦截器时，
 * 则表示执行最终目标逻辑。
 *
 * 例如，一个只放行、不改变结果的拦截器通常类似于：
 * ```Kotlin
 * suspend fun intercept(context: CustomContext): CustomResult {
 *     return context.invoke()
 * }
 * ```
 *
 * 拦截器可以选择不调用 [Interceptor.Context.invoke]，从而直接短路本次流程并返回结果；
 * 也可以在调用前后增加前置、后置或异常处理逻辑。
 *
 * 一个简单的字符串拦截器类型可以这样定义：
 * ```Kotlin
 * fun interface TextInterceptor : Interceptor<TextContext, String> {
 *     override suspend fun intercept(context: TextContext): String
 * }
 * ```
 *
 * 放行并修改内部结果：
 * ```Kotlin
 * val interceptor = TextInterceptor { context ->
 *     "[${context.invoke()}]"
 * }
 * ```
 *
 * 对于普通的应用开发者来说，拦截器对象 [Interceptor] 的某个子类型是用来**实现**的，
 * 拦截器的上下文对象 [Interceptor.Context] 是用来**调用**的。
 *
 * 拦截器的 Java 平台桥接/兼容类型由子类型提供实现，[Interceptor] 类型本身不考虑 Java 兼容性。
 *
 * @see AggregationInterceptorContext
 * @see InterceptorSet
 * @since 5.0
 *
 * @author Forte Scarlet
 */
public interface Interceptor<C : Interceptor.Context<R>, R> {
    /**
     * 根据 [context] 执行拦截逻辑并得到结果 [R]。
     *
     * 实现可以通过 [Interceptor.Context.invoke] 继续执行内部逻辑，也可以直接返回结果完成短路。
     *
     * ```Kotlin
     * override suspend fun intercept(context: TextContext): String {
     *     return context.invoke()
     * }
     * ```
     */
    @Throws(Exception::class)
    public suspend fun intercept(context: C): R

    /**
     * 拦截器的上下文对象。
     *
     * 上下文用于承载当前拦截场景的共享信息，并提供继续执行内部逻辑的入口。
     *
     * [AggregationInterceptorContext] 用于表达一个上下文已经聚合了一组拦截器与最终目标逻辑。
     * [IteratorAggregationInterceptorContext] 则提供了一种基于迭代器游标推进的默认实现。
     *
     * @see AggregationInterceptorContext
     * @see IteratorAggregationInterceptorContext
     */
    public interface Context<R> {
        /**
         * 继续执行被拦截的内部逻辑。
         *
         * 对于 [AggregationInterceptorContext]，这通常会进入下一层拦截器；
         * 当没有下一层拦截器时，则执行最终目标逻辑。
         *
         * - 对于使用者，每个拦截器中此函数应当只能**执行一次**。
         * - 对于实现者，**建议**对调用行为进行校验，如果一个拦截器调用了多次应当抛出明确的异常。
         *
         * ```Kotlin
         * val result = context.invoke()
         * ```
         */
        @ST
        @Throws(Exception::class)
        public suspend operator fun invoke(): R
    }
}
