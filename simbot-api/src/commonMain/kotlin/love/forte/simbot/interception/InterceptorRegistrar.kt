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

import love.forte.simbot.common.PriorityConstant

/**
 * 拦截器的注册器
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface InterceptorRegistrar {
    /**
     * 基于拦截点标记，注册一个指定拦截点的拦截器。
     * @param point 拦截点标记
     * @param priority 这个拦截器在相同拦截点或一组拦截器之间的优先级，默认为 [PriorityConstant.DEFAULT]。
     * @param interceptor 拦截器
     *
     * @return 注册后的回执句柄，可用于取消注册的拦截器
     */
    public fun <
        C : Interceptor.Context,
        R,
        P : InterceptionPoint<C, R>
        > register(
        point: P,
        priority: Int = PriorityConstant.DEFAULT,
        interceptor: Interceptor<C, R, P>
    ): InterceptorRegistration
}

