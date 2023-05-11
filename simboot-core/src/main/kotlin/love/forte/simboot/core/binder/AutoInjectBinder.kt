/*
 * Copyright (c) 2021-2023 ForteScarlet.
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

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerProcessingContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass


/**
 * 尝试为参数其提供一个对应的实例。
 */
public object AutoInjectBinderFactory : ParameterBinderFactory {

    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val annotationTool = KAnnotationTool()
        val parameter = context.parameter
        val injectNeed = annotationTool.getAnnotation(parameter, Inject::class) != null
        if (!injectNeed) return ParameterBinderResult.empty()

        val container = context.beanContainer

        val name = annotationTool.getAnnotation(parameter, Named::class)?.value
        return ParameterBinderResult.normal(
            if (name != null) AutoInjectBinderByName(name, container)
            else AutoInjectBinderByType(
                parameter.type.classifier as KClass<*>, container
            )
        , PriorityConstant.NORMAL + 1
        )
    }
}


private class AutoInjectBinderByName(private val name: String, private val container: BeanContainer) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        return kotlin.runCatching { container[name] }
    }
}


private class AutoInjectBinderByType(private val type: KClass<*>, private val container: BeanContainer) :
    ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        return kotlin.runCatching { container[type] }
    }
}
