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

import love.forte.simbot.event.Event
import love.forte.simbot.event.EventContext
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.quantcat.common.binder.BindException
import love.forte.simbot.quantcat.common.binder.ParameterBinder
import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.ParameterBinderResult
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.safeCast


/**
 * 如果参数是 [Event]、
 * [EventContext]、
 * [EventListener] 类型,
 * 检测并通过 [EventListenerContext] 中提供的内容进行提供。
 *
 */
public object EventParameterBinderFactory : ParameterBinderFactory {
    @Suppress("UNCHECKED_CAST")
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val type = context.parameter.type
        val classifier = type.classifier as? KClass<*> ?: return ParameterBinderResult.empty()

        return when {
            // Event
            classifier.isSubclassOf(Event::class) -> {
                ParameterBinderResult.normal(EventInstanceBinder(classifier as KClass<Event>))
            }

            // EventListener
            classifier.isSubclassOf(EventListener::class) -> {
                ParameterBinderResult.normal(EventListenerInstanceBinder)
            }

            // EventContext
            classifier.isSubclassOf(EventContext::class) -> {
                ParameterBinderResult.normal(EventContextInstanceBinder)
            }

            // EventListenerContext
            classifier.isSubclassOf(EventListenerContext::class) -> {
                ParameterBinderResult.normal(EventListenerContextInstanceBinder)
            }

            // KFunction from listener
            classifier.isSubclassOf(KFunction::class) -> {
                ParameterBinderResult.normal(KFunctionInstanceBinder(context.source))
            }

            else -> ParameterBinderResult.empty()
        }

    }
}


/**
 * 提供事件本体作为参数。
 */
private class EventInstanceBinder(private val targetType: KClass<Event>) : ParameterBinder {
    override fun arg(context: EventListenerContext): Result<Any?> {
        // 如果当前事件类型是目标类型的子类，提供参数
        return targetType.safeCast(context.event)?.let { Result.success(it) }
            ?: Result.failure(
                BindException(
                    "The type of EventListenerContext.event " +
                        "is inconsistent with the target type $targetType"
                )
            )
    }
}

/**
 * 提供 [EventContext] 作为参数。
 */
private object EventContextInstanceBinder : ParameterBinder {
    override fun arg(context: EventListenerContext): Result<Any?> = Result.success(context.context)
}

/**
 * 提供 [EventListenerContext] 作为参数。
 */
private object EventListenerContextInstanceBinder :
    ParameterBinder {
    override fun arg(context: EventListenerContext): Result<Any?> = Result.success(context)
}

/**
 * 提供监听函数自身作为参数。
 */
private object EventListenerInstanceBinder : ParameterBinder {
    override fun arg(context: EventListenerContext): Result<Any?> = Result.success(context.listener)
}

/**
 * 提供正在被处理的函数源值
 */
private class KFunctionInstanceBinder(private val function: KFunction<*>) : ParameterBinder {
    override fun arg(context: EventListenerContext): Result<Any?> = Result.success(function)
}

// private inline fun attributeBinder(
//     nullable: Boolean,
//     attribute: Attribute<*>,
//     classifier: KClass<*>,
//     noinline attributeMapSelector: (EventListenerContext) -> AttributeMap = { it.context.attributes },
//     nullMessageBlock: () -> String,
// ): AttributeBinder {
//     return if (nullable) AttributeBinder.Nullable(classifier, attribute, attributeMapSelector)
//     else AttributeBinder.Notnull(classifier, attribute, nullMessageBlock(), attributeMapSelector)
// }
//
// private sealed class AttributeBinder : ParameterBinder {
//     protected abstract val attributeMapSelector: (EventListenerContext) -> AttributeMap
//     protected abstract val attribute: Attribute<*>
//
//     class Nullable(
//         private val classifier: KClass<*>, override val attribute: Attribute<*>,
//         override val attributeMapSelector: (EventListenerContext) -> AttributeMap,
//     ) : AttributeBinder() {
//         override fun arg(context: EventListenerContext): Result<Any?> {
//             return attributeMapSelector(context)[attribute]
//                 .let { value -> Result.success(value.takeIf { classifier.isInstance(it) }) }
//         }
//     }
//
//     class Notnull(
//         private val classifier: KClass<*>,
//         override val attribute: Attribute<*>,
//         private val nullMessage: String,
//         override val attributeMapSelector: (EventListenerContext) -> AttributeMap,
//     ) : AttributeBinder() {
//         override fun arg(context: EventListenerContext): Result<Any?> {
//             return attributeMapSelector(context)[attribute]?.takeIf { classifier.isInstance(it) }
//                 ?.let { Result.success(it) } ?: Result.failure(NullPointerException(nullMessage))
//         }
//     }
// }
