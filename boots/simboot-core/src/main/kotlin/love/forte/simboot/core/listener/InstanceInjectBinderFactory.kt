/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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