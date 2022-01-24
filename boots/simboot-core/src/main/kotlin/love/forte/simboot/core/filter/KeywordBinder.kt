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

package love.forte.simboot.core.filter

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.listener.BindException
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.event.EventListenerProcessingContext

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
        return ParameterBinderResult.normal(
            if (value.required) KeywordBinder.Required(value.value) else KeywordBinder.NotRequired(
                value.value
            )
        )
    }
}


private sealed class KeywordBinder(val name: String) : ParameterBinder {

    class Required(name: String) : KeywordBinder(name) {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val keywords = context.listener.getAttribute(KeywordsAttribute)
                ?: return Result.failure(BindException("Current listener function does not have any keyword"))

            val textContent =
                context.textContent ?: return Result.failure(BindException("Current event's textContent is null."))

            for (keyword in keywords) {
                val param = keyword.matcherValue.getParam(textContent, name)
                if (param != null) return Result.success(param)
            }

            return Result.failure(BindException("No keyword matched successfully."))
        }
    }

    class NotRequired(name: String) : KeywordBinder(name) {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val keywords = context.listener.getAttribute(KeywordsAttribute) ?: return Result.success(null)
            val textContent = context.textContent ?: return Result.success(null)
            for (keyword in keywords) {
                val param = keyword.matcherValue.getParam(textContent, name)
                if (param != null) return Result.success(param)
            }

            return Result.success(null)
        }
    }

}