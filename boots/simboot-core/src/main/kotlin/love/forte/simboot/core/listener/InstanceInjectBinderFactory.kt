/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core.listener

import love.forte.di.BeanContainer
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.EventListenerProcessingContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * 如果参数类型为 [KParameter.Kind.INSTANCE]，尝试通过beanContainer获取其实例。
 */
public object InstanceInjectBinderFactory : ParameterBinderFactory {
    override val priority: Int get() = PriorityConstant.LAST

    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val parameter = context.parameter
        if (parameter.kind != KParameter.Kind.INSTANCE) return ParameterBinderResult.empty()

        val type = parameter.type.classifier as? KClass<*> ?: return ParameterBinderResult.empty()

        val annotationProcessContext = context.annotationProcessContext

        return ParameterBinderResult.normal(
            InstanceBinder(
                annotationProcessContext.beanId,
                type,
                annotationProcessContext.beanContainer
            )
        )


    }
}

private class InstanceBinder(
    private val name: String?,
    private val type: KClass<*>,
    private val beanContainer: BeanContainer
) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        return kotlin.runCatching { name?.let { n -> beanContainer[n, type] } ?: beanContainer[type] }
    }
}