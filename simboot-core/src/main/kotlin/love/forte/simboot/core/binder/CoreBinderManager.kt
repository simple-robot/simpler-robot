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
 * [BinderManager] 的基础实现，提供基本功能。
 */
public class CoreBinderManager(
    private val globalBinderFactories: List<ParameterBinderFactory> = emptyList(),
    private val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf(),
) : BinderManager {
    override val normalBinderFactorySize: Int
        get() = idBinderFactories.size
    
    override val globalBinderFactorySize: Int
        get() = globalBinderFactories.size
    
    override fun get(id: String): ParameterBinderFactory? {
        return idBinderFactories[id]
    }
    
    override fun getGlobals(): List<ParameterBinderFactory> {
        return globalBinderFactories.toList()
    }
    
    override fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory {
        return function.toBinderFactory(instanceGetter)
    }
}
