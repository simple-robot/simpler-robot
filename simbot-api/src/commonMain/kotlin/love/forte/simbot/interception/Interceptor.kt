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
@file:JvmMultifileClass

package love.forte.simbot.interception

import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.common.collection.concurrentMutableMap
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 拦截由 [InterceptionPoint] 标识的操作。
 *
 * 用户通过 [InterceptorRegistrar] 注册拦截器，组件在实际操作时主动执行拦截链逻辑。
 * [C] 仅承载拦截点定义的业务信息，拦截器间的自定义信息通过 [Chain.attributes] 传递。
 *
 * @param C 拦截点的非空业务上下文类型。
 * @param R 操作及拦截链的返回值类型，可以为空。
 * @param P 拦截点的具体类型。
 * @since 5.0
 * @author ForteScarlet
 */
public fun interface Interceptor<C : Any, R, P : InterceptionPoint<C, R>> {
    /**
     * 拦截一次操作，可以通过 [chain] 继续执行、替换传递的 [context]，或直接返回结果以短路后续操作。
     *
     * 重复调用 [Chain.proceed] 会重复执行同一段后续链及其副作用。
     * 是否允许重复或并发调用、短路的含义等，由具体 [point] 的约定做进一步限定。
     * 异常与协程取消默认向调用方传播，拦截器可按拦截点契约处理操作异常。
     *
     * @param point 本次操作的拦截点。
     * @param context 传递到当前拦截器的业务上下文。
     * @param chain 当前拦截器之后的执行链及本次操作的共享属性。
     * @return 拦截器处理后的操作结果，可能是最终目标实际执行结果，也可能是下一段拦截器或某自定义结果。
     */
    @JvmSynthetic
    public suspend fun intercept(point: P, context: C, chain: Chain<C, R>): R

    /**
     * 当前拦截器之后的执行链，以及同一次操作中共享的附加信息。
     *
     * 每一层 chain 都指向固定的后续链。
     * chain 只应在所属拦截调用完成前使用，不应保存到调用之外执行或在异步中执行。
     * 普通使用者不需要实现此接口。组件或插件开发者自行构建执行入口时可使用 [create]。
     *
     * @param C 后续链接受的非空业务上下文类型。
     * @param R 后续链的返回值类型。
     * @since 5.0
     */
    @SubclassOptInRequired(InternalInterceptorInheritanceApi::class)
    public interface Chain<C : Any, R> {
        /**
         * 同一次操作各层拦截器、各次 [proceed] 共享的自定义属性。
         *
         * 并发执行时，调用方应保证此 Map、其中的值及复合读写操作的并发安全。AttributeMap 内部使用线程安全的 Map。
         * 默认执行器为每次操作提供独立的 Map，共享范围是一次完整的拦截处理链。
         */
        public val attributes: MutableAttributeMap

        /**
         * 使用 [context] 执行固定的后续链，并返回其结果。
         * 每次调用重新执行下游，异常及取消直接传播，具体调用次数限制由拦截点约定。
         */
        @JvmSynthetic
        public suspend fun proceed(context: C): R

        companion {
            /**
             * 使用指定共享属性和后续操作构建 chain。
             *
             * [proceed] 的提供方负责维持固定后继的契约。
             * @param attributes 与同一次操作的其他 chain 共享的属性。默认基于 [concurrentMutableMap] 构建。
             * @param proceed 每次继续执行时调用的后续操作。
             * @return 将 [proceed] 与 [attributes] 组合的执行链。
             */
            public fun <C : Any, R> create(
                attributes: MutableAttributeMap = MutableAttributeMap.create(concurrentMutableMap()),
                proceed: suspend (C) -> R
            ): Chain<C, R> = SimpleChain(attributes, proceed)
        }
    }
}

@OptIn(InternalInterceptorInheritanceApi::class)
private class SimpleChain<C : Any, R>(
    override val attributes: MutableAttributeMap,
    private val action: suspend (C) -> R
) : Interceptor.Chain<C, R> {
    override suspend fun proceed(context: C): R = action(context)
}
