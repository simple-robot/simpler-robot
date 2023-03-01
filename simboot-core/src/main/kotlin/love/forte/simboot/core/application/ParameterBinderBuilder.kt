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

package love.forte.simboot.core.application

import love.forte.simboot.annotation.Binder
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import kotlin.reflect.KFunction


/**
 *
 * @author ForteScarlet
 */
public interface ParameterBinderBuilder {
    
    /**
     * 提供一个 [binderFactory].
     *
     * 如果不指定 [id]，则为对全局所有监听函数生效的binder。如果指定id，那么只有当一个监听函数上标记了 [Binder] 注解的时候才会被使用。
     */
    public fun binder(id: String? = null, binderFactory: ParameterBinderFactory)
    
    
    /**
     * 将一个 [KFunction][function] 解析为 [ParameterBinderFactory].
     *
     * 此 function必须遵循规则：
     * - 返回值类型必须是 [ParameterBinder] 或 [ParameterBinderResult] 类型。
     * - 参数或则receiver有且只能有一个，且类型**必须是** [ParameterBinderFactory.Context]
     *
     * @param function 解析目标
     * @param instanceGetter 获取 [function] 执行实例的对象
     * @return 解析的结果。此结果已经被添加到当前环境中。
     */
    public fun binder(
        id: String? = null,
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory
    
}


