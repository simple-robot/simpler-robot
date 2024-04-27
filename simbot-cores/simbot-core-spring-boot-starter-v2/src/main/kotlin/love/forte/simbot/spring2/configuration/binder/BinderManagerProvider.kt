/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.spring2.configuration.binder

import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory

/**
 * 如果注册 [ParameterBinderFactory] 时 id 出现重复
 */
public open class DuplicateBinderIdException(message: String, cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

/**
 * 应用于 [ParameterBinderManagerBuilderConfigurer] 的构建器。
 *
 */
public interface ParameterBinderManagerBuilder {
    /**
     * 添加一个有 ID 的具体作用域b [ParameterBinderFactory]
     *
     * @throws DuplicateBinderIdException 如果 id 出现重复
     */
    public fun addBinderFactory(id: String, factory: ParameterBinderFactory)

    /**
     * 添加一个全局应用的 [ParameterBinderFactory]
     */
    public fun addBinderFactory(factory: ParameterBinderFactory)
}

/**
 * 在默认行为中会被 [ResolveBinderManagerProcessor] 批量加载并配置。
 */
public fun interface ParameterBinderManagerBuilderConfigurer {
    /**
     * 配置 [builder]
     */
    public fun configure(builder: ParameterBinderManagerBuilder)
}
