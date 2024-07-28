/*
 *     Copyright (c) 2021-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.quantcat.common.binder.impl

import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.common.attribute.AttributeMapContainer
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.quantcat.common.binder.BindException
import love.forte.simbot.quantcat.common.binder.ParameterBinder
import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.ParameterBinderResult
import love.forte.simbot.quantcat.common.convert.NonConverters
import love.forte.simbot.quantcat.common.filter.FilterValueProperties
import love.forte.simbot.quantcat.common.keyword.Keyword
import love.forte.simbot.quantcat.common.keyword.KeywordListAttribute
import kotlin.reflect.KClass

/**
 *
 * 为 [Keyword] 提供参数绑定功能的绑定器工厂。
 *
 * @author ForteScarlet
 */
public class KeywordBinderFactory(
    private val filterValueReader: (context: ParameterBinderFactory.Context) -> FilterValueProperties?
) : ParameterBinderFactory {
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val value = filterValueReader(context) ?: return ParameterBinderResult.empty()
        val paramType = context.parameter.type.classifier as? KClass<*>
        return ParameterBinderResult.normal(
            if (value.required) {
                KeywordBinder.Required(value.value, paramType)
            } else {
                KeywordBinder.NotRequired(value.value, paramType)
            }
        )
    }
}


private sealed class KeywordBinder(val name: String, val paramType: KClass<*>?) : ParameterBinder {

    @OptIn(InternalSimbotAPI::class)
    private val converter: (Any) -> Any = if (paramType != null && paramType != String::class) {
        {
            NonConverters.convert(instance = it, to = paramType)
        }
    } else {
        { it }
    }

    protected fun convert(param: Any): Any = converter(param)

    class Required(name: String, paramType: KClass<*>?) : KeywordBinder(name, paramType) {
        override fun arg(context: EventListenerContext): Result<Any?> {
            val keywordList = (context.listener as? AttributeMapContainer)?.attributeMap?.get(KeywordListAttribute)
                ?: return Result.failure(BindException("Current listener function does not have any keyword"))

            val textContent =
                context.plainText ?: return Result.failure(BindException("Current event's textContent is null."))

            for (keyword in keywordList) {
                val param = keyword.regexValueMatcher.getParam(name, textContent)
                if (param != null) {
                    return Result.success(convert(param))
                }
            }

            return Result.failure(BindException("No keyword matched successfully."))
        }
    }

    class NotRequired(name: String, paramType: KClass<*>?) : KeywordBinder(name, paramType) {
        override fun arg(context: EventListenerContext): Result<Any?> {
            val keywords = (context.listener as? AttributeMapContainer)?.attributeMap?.get(KeywordListAttribute)
                ?: return Result.success(null)
            val textContent = context.plainText ?: return Result.success(null)
            for (keyword in keywords) {
                val param = keyword.regexValueMatcher.getParam(name, textContent)
                if (param != null) return Result.success(convert(param))
            }

            return Result.success(null)
        }
    }

}
