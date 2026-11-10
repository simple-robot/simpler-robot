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

/**
 * [拦截器][Interceptor] 的容器，可以用于根据调用点获取对应的拦截器。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface InterceptorContainer {
    /**
     * 当前容器中所有的拦截器的序列。
     * 会以 (interception point, Interceptor priority) 的顺序返回此序列，
     * 而 interception point 本身无顺序，因此 point 会随机排序。
     * 例如:
     * ```
     * point1, interceptor1(priority 1)
     * point1, interceptor1(priority 2)
     * point1, interceptor1(priority 3)
     * point2, interceptor1(priority 1)
     * point2, interceptor1(priority 2)
     * point2, interceptor1(priority 3)
     * ```
     *
     * 拦截器序列是弱一致性的，若在迭代过程中某拦截器使用了 [InterceptorRegistration.dispose]，
     * 不保证本次迭代一定会掠过此元素，但能保证最终一致性。
     */
    public val interceptors: Sequence<Map.Entry<InterceptionPoint<*, *>, Interceptor<*, *, *>>>

    /**
     * 根据指定的 [InterceptionPoint] 获取对应的拦截器序列。
     *
     * 拦截器序列是弱一致性的，若在迭代过程中某拦截器使用了 [InterceptorRegistration.dispose]，
     * 不保证本次迭代一定会掠过此元素，但能保证最终一致性。
     *
     * @param point 拦截点标记
     */
    public fun <
        C : Interceptor.Context,
        R,
        P : InterceptionPoint<C, R>
        > interceptors(point: P): Sequence<Interceptor<C, R, P>>

    /**
     *  根据指定的 [InterceptionPoint] 判断是否存在任意拦截器元素。
     *
     * @param point 拦截点标记
     */
    public fun hasInterceptors(point: InterceptionPoint<*, *>): Boolean
}

