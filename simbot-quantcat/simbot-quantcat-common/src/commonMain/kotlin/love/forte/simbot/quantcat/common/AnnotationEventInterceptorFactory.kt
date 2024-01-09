/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.quantcat.common

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.function.plus
import love.forte.simbot.event.EventInterceptor
import love.forte.simbot.event.EventInterceptorRegistrationProperties
import love.forte.simbot.quantcat.common.AnnotationEventInterceptorFactory.Result.Companion.build
import kotlin.jvm.JvmStatic
import kotlin.reflect.KFunction


/**
 * 用于 [@Interceptor][love.forte.simbot.quantcat.annotations.Interceptor] 中的拦截器工厂。
 *
 * @author ForteScarlet
 */
public fun interface AnnotationEventInterceptorFactory {

    /**
     * 得到一个 [Result]。
     *
     * @see Result
     */
    public fun interceptor(context: Context): Result?

    /**
     * Context of [AnnotationEventInterceptorFactory.interceptor].
     */
    public interface Context {
        /**
         * 标记了注解而需要使用此工厂的函数本体。
         * 如果不为 `null`，则说明此工厂此时的使用者是一个标记了注解的函数。
         *
         * _Note: Java 中可以使用 `ReflectJvmMapping.javaMethod` 转化为 `Method`。_
         */
        public val function: KFunction<*>?
    }

    /**
     * 拦截器工厂的响应结果。包含一个拦截器实例 [interceptor] 以及用于注册的配置器 [configuration]。
     * 可以通过 [build] 快速构建。
     *
     * @see AnnotationEventInterceptorFactory.interceptor
     * @author ForteScarlet
     */
    public abstract class Result {
        /**
         * 拦截器实例
         */
        public abstract val interceptor: EventInterceptor

        /**
         * 拦截器的注册配置。
         */
        public abstract val configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>?

        public companion object {

            /**
             * 通过 `DSL` 构建一个 [Result]。
             */
            @JvmStatic
            public fun build(block: ConfigurerFunction<Builder>): Result =
                builder().also(block::invokeWith).build()

            /**
             * 得到一个 [Result] 的构建器。
             */
            @JvmStatic
            public fun builder(): Builder = EventInterceptorFactoryResultBuilderImpl()


        }

        /**
         * [Result] 的构建器。可以通过 [Result.builder] 获取或通过 [Result.build] 使用。
         */
        public interface Builder {
            /**
             * 设置一个拦截器结果。会覆盖上一次的结果。
             */
            public fun interceptor(interceptor: EventInterceptor): Builder

            /**
             * 添加一个配置。会与上一次调用时的配置合并。
             */
            public fun configuration(configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>): Builder

            /**
             * 构建 [Result]。
             *
             * @throws IllegalStateException 如果 [interceptor] 尚未设置
             */
            public fun build(): Result
        }
    }
}

private class EventInterceptorFactoryResultBuilderImpl : AnnotationEventInterceptorFactory.Result.Builder {
    private var interceptor: EventInterceptor? = null
    private var configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>? = null

    override fun interceptor(interceptor: EventInterceptor): AnnotationEventInterceptorFactory.Result.Builder = apply {
        this.interceptor = interceptor
    }

    override fun configuration(configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>): AnnotationEventInterceptorFactory.Result.Builder =
        apply {
            if (this.configuration == null) {
                this.configuration = configuration
            } else {
                this.configuration = this.configuration?.let { thisConfiguration ->
                    thisConfiguration + configuration
                }
            }
        }

    override fun build(): AnnotationEventInterceptorFactory.Result {
        return ResultImpl(
            interceptor = interceptor ?: error("No interceptor provided"),
            configuration = configuration
        )
    }

    private class ResultImpl(
        override val interceptor: EventInterceptor,
        override val configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>?
    ) : AnnotationEventInterceptorFactory.Result()
}
