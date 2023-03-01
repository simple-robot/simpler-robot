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

import love.forte.di.BeanContainer
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerProcessingContext
import javax.inject.Named
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

/**
 * 如果参数类型为 [KParameter.Kind.INSTANCE]，尝试通过beanContainer获取其实例。
 */
public object InstanceInjectBinderFactory : ParameterBinderFactory {
    override val priority: Int get() = PriorityConstant.LAST
    
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val parameter = context.parameter
        if (parameter.kind != KParameter.Kind.INSTANCE) return ParameterBinderResult.empty()
        
        val type = parameter.type.classifier as? KClass<*> ?: return ParameterBinderResult.empty()
        
        val name = parameter.findAnnotation<Named>()?.value
        
        return ParameterBinderResult.normal(
            InstanceBinder(
                name = name,
                type = type,
                beanContainer = context.beanContainer
            )
        )
        
        
    }
}


private class InstanceBinder(
    private val name: String?,
    private val type: KClass<*>,
    private val beanContainer: BeanContainer,
) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        return kotlin.runCatching { name?.let { n -> beanContainer[n, type] } ?: beanContainer[type] }
    }
}
