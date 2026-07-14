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

import love.forte.simbot.common.collection.computeValueIfAbsent

/**
 * [InterceptorRegistrar] 的基础实现，用于构建一个 [InterceptorCollection]。
 *
 * @since 5.0
 * @author Forte Scarlet
 */
public class SimpleInterceptorRegistrar : InterceptorRegistrar {
    private val interceptors = mutableMapOf<Int, MutableList<Interceptor<*, *>>>()

    override fun register(priority: Int, interceptor: Interceptor<*, *>) {
        interceptors.computeValueIfAbsent(priority) { mutableListOf() }.add(interceptor)
    }

    override fun build(): InterceptorCollection {
        return SimpleInterceptorCollection().also {
            for ((priority, interceptors) in interceptors) {
                for (interceptor in interceptors) {
                    it.add(priority, interceptor)
                }
            }
        }
    }
}
