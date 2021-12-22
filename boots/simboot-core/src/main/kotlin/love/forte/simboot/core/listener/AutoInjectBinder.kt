/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core.listener

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simboot.utils.WeakVal
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerProcessingContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass


/**
 * 尝试为参数其提供一个对应的实例。
 */
public object AutoInjectBinderFactory : ParameterBinderFactory {
    private val annotationTool: KAnnotationTool by WeakVal(true, ::KAnnotationTool)

    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val parameter = context.parameter
        val injectNeed = annotationTool.getAnnotation(parameter, Inject::class) != null
        if (!injectNeed) return ParameterBinderResult.empty()

        val container = context.annotationProcessContext.beanContainer

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