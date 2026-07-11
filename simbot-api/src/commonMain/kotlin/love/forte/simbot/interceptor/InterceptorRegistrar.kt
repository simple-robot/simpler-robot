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

import love.forte.simbot.common.PriorityConstant

/**
 * 用于在配置阶段注册配置 [Interceptor] 的。
 *
 * @since 5.0
 * @author Forte Scarlet
 */
public interface InterceptorRegistrar {
    /**
     * 注册一个拦截器实例。
     *
     * @param priority 拦截器的优先级，越小越优先。
     * 默认为 [PriorityConstant.DEFAULT]。
     */
    public fun register(priority: Int, interceptor: Interceptor<*, *>)

    /**
     * 注册一个拦截器实例。
     */
    public fun register(interceptor: Interceptor<*, *>) {
        register(PriorityConstant.DEFAULT, interceptor)
    }

    /**
     * 根据注册信息，构建一个 [InterceptorCollection]。
     */
    public fun build(): InterceptorCollection
}
