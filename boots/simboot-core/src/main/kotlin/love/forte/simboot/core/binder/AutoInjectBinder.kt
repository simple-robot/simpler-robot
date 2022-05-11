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