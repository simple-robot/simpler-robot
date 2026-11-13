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
 * 同时提供拦截器注册、查询和按 ID 注销的管理器。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface InterceptorManager : InterceptorRegistrar, InterceptorContainer

/**
 * 构建一个基础的 [InterceptorManager]。
 *
 * @since 5.0
 */
public fun InterceptorManager(): InterceptorManager {
    // 暂时没什么可配置的，就先不管配置类了。
    return SimpleInterceptorManager()
}
