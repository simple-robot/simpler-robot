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
