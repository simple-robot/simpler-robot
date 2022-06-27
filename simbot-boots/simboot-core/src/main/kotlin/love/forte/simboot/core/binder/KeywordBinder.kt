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
import love.forte.annotationtool.core.nonConverters
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.core.filter.KeywordsAttribute
import love.forte.simboot.listener.BindException
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.event.EventListenerProcessingContext
import kotlin.reflect.KClass

/**
 *
 * 为 [love.forte.simboot.filter.Keyword] 提供参数绑定功能。
 *
 * @author ForteScarlet
 */
public object KeywordBinderFactory : ParameterBinderFactory {
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val tool = KAnnotationTool()
        val value = tool.getAnnotation(context.parameter, FilterValue::class) ?: return ParameterBinderResult.empty()
        val paramType = context.parameter.type as? KClass<*>
        return ParameterBinderResult.normal(
            if (value.required)
                KeywordBinder.Required(value.value, paramType)
            else KeywordBinder.NotRequired(value.value, paramType)
        )
    }
}


private sealed class KeywordBinder(val name: String, val paramType: KClass<*>?) : ParameterBinder {

    private val converter: (Any) -> Any = if (paramType != null && paramType != String::class) {
        {
            nonConverters().convert(instance = it, to = paramType)
        }
    } else {
        { it }
    }

    protected fun convert(param: Any): Any = converter(param)

    class Required(name: String, paramType: KClass<*>?) : KeywordBinder(name, paramType) {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val keywords = context.listener.getAttribute(KeywordsAttribute)
                ?: return Result.failure(BindException("Current listener function does not have any keyword"))

            val textContent =
                context.textContent ?: return Result.failure(BindException("Current event's textContent is null."))

            for (keyword in keywords) {
                val param = keyword.matcherValue.getParam(name, textContent)
                if (param != null) {
                    return Result.success(convert(param))
                }
            }

            return Result.failure(BindException("No keyword matched successfully."))
        }
    }

    class NotRequired(name: String, paramType: KClass<*>?) : KeywordBinder(name, paramType) {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val keywords = context.listener.getAttribute(KeywordsAttribute) ?: return Result.success(null)
            val textContent = context.textContent ?: return Result.success(null)
            for (keyword in keywords) {
                val param = keyword.matcherValue.getParam(name, textContent)
                if (param != null) return Result.success(convert(param))
            }

            return Result.success(null)
        }
    }

}