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
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * 一个拦截器。
 *
 * 拦截器的应用方通过 [InterceptionPoint] 和 [Context][C] 来决定向拦截器本身提供的信息与可供使用的行为，
 * [Interceptor] 本身则由用户实现并注册到 [InterceptorRegistrar] 中。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public fun interface Interceptor<C : Interceptor.Context, R, P : InterceptionPoint<C, R>> {
    // TODO doc...
    public suspend fun intercept(point: P, context: C, proceed: suspend (C) -> R): R

    /**
     * 拦截器的上下文信息，由拦截点的实现者自由实现。
     * 上下文类型本身不做过多约束，仅要求其提供一个可以让用户自由使用、可在拦截流程中传递信息的 [attributes]，
     * 其余内容不做限制。
     *
     * @since 5.0
     */
    public interface Context {
        /**
         * 用户可自由使用、在拦截器之间传递信息的 [attributes]，是一个伪类型安全的 Map like。
         */
        public val attributes: MutableAttributeMap
    }
}
