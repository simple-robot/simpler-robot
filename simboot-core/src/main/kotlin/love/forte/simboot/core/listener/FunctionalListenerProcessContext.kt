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

package love.forte.simboot.core.listener

import love.forte.di.BeanContainer
import love.forte.simboot.core.binder.BinderManager
import kotlin.reflect.KFunction

/**
 * [KFunctionListenerProcessor] 进行处理所需参数集.
 */
public data class FunctionalListenerProcessContext(
    
    // /**
    //  * 此监听函数所指定的特殊ID。
    //  */
    // val id: String?,
    
    /**
     * 此监听函数对应的function。
     */
    val function: KFunction<*>,
    
    // /**
    //  * 此监听函数的期望优先级。
    //  */
    // val priority: Int,
    
    // /**
    //  * 此监听函数期望中是否为异步。
    //  */
    // val isAsync: Boolean,
    
    /**
     * binder factory的容器。
     */
    val binderManager: BinderManager,
    
    /**
     * Bean容器。
     */
    val beanContainer: BeanContainer,
    
)

