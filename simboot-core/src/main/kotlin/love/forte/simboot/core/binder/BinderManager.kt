/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.core.binder

import love.forte.simboot.listener.ParameterBinderFactory
import kotlin.reflect.KFunction

/**
 * Binder管理器。
 */
public interface BinderManager {
    
    /**
     * 普通的binder工厂的数量。
     */
    public val normalBinderFactorySize: Int
    
    /**
     * 全局性的binder工厂的数量。
     */
    public val globalBinderFactorySize: Int
    
    /**
     * 根据ID获取一个指定的普通binder工厂。
     */
    public operator fun get(id: String): ParameterBinderFactory?
    
    /**
     * 获取所有的全局binder工厂。
     */
    public fun getGlobals(): List<ParameterBinderFactory>
    
    /**
     * 解析一个函数并将其作为一个binder工厂。
     */
    public fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory
}
