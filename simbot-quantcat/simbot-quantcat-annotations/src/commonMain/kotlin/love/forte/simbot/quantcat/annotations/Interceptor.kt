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

package love.forte.simbot.quantcat.annotations

import love.forte.simbot.quantcat.common.AnnotationEventInterceptorFactory
import kotlin.reflect.KClass

/**
 * 配合 [@Listener][Listener] 使用，为被标记的事件处理器添加一个目标工厂所产生的拦截器。
 *
 * [factories] 提供一个 [AnnotationEventInterceptorFactory] 的 **实现类型**。如果此类型是 `object`，
 * 则直接使用，否则会构建一个实例。此实例 **可能会被共享**，因此请考虑处理并发。
 *
 * 在一些有 DI 能力的实现中（例如 Spring Boot starter），则可能会根据类型从 bean 池中获取，
 * 无法获取时才会尝试构建实例并共享。此时的共享实例不会被添加到相应的 DI 环境中，仅是一个临时的共享缓存。
 *
 * @author ForteScarlet
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Interceptor(
    /**
     * 提供一个 [AnnotationEventInterceptorFactory] 的 **实现类型**。如果此类型是 `object`，
     * 则直接使用，否则会构建一个实例。此实例 **可能会被共享**，因此请考虑处理并发。
     *
     * 注意：只有**第一个**元素有效。如果提供多个元素则会抛出异常。[Array] 类型仅为了提供默认值。
     * 如果希望添加多个拦截器，请使用多个 [Interceptor] 注解。
     *
     * [factories] 与 [factoryClassName] 同时存在时，优先使用 [factories]。
     */
    vararg val factories: KClass<out AnnotationEventInterceptorFactory> = []
)
