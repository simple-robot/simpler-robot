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

import love.forte.simboot.listener.BindException
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.Attribute
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventProcessingContext
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


/**
 * 如果参数是 [Event]、[EventListenerProcessingContext]、[EventListener] 类型, 检测并通过 [EventListenerProcessingContext] 中提供的内容进行提供。
 *
 */
public object EventParameterBinderFactory : ParameterBinderFactory {
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val type = context.parameter.type
        val classifier = type.classifier as? KClass<*> ?: return ParameterBinderResult.empty()
        //if (classifier !is KClass<*>)

        val nullable = type.isMarkedNullable

        return when {
            // 是不是 Event子类型
            classifier.isSubclassOf(Event::class) -> {
                @Suppress("UNCHECKED_CAST")
                return ParameterBinderResult.normal(EventInstanceBinder(classifier as KClass<Event>))
            }

            // 是不是 EventListener 子类型
            classifier.isSubclassOf(EventListener::class) -> {
                @Suppress("UNCHECKED_CAST")
                return ParameterBinderResult.normal(EventListenerInstanceBinder(classifier as KClass<EventListener>))
            }

            // 是不是 EventProcessingContext 子类型
            classifier.isSubclassOf(EventProcessingContext::class) -> {
                @Suppress("UNCHECKED_CAST")
                return ParameterBinderResult.normal(EventListenerProcessingContextInstanceBinder(classifier as KClass<EventProcessingContext>))
            }

            // Scope 相关类型

            classifier.isSubclassOf(EventProcessingContext.Scope.Instant.type) -> {
                return ParameterBinderResult.normal(attributeBinder(nullable, EventProcessingContext.Scope.Instant) { "Scope [Instant] in current context is null." } )
            }
            classifier.isSubclassOf(EventProcessingContext.Scope.Global.type) -> {
                return ParameterBinderResult.normal(attributeBinder(nullable, EventProcessingContext.Scope.Global) { "Scope [Global] in current context is null." } )
            }
            classifier.isSubclassOf(EventProcessingContext.Scope.ContinuousSession.type) -> {
                return ParameterBinderResult.normal(attributeBinder(nullable, EventProcessingContext.Scope.ContinuousSession) { "Scope [ContinuousSession] in current context is null." } )
            }


            else -> ParameterBinderResult.empty()
        }

    }
}

/**
 * 提供事件本体作为参数。
 */
private class EventInstanceBinder(private val targetType: KClass<Event>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前事件类型是目标类型的子类，提供参数
        val event = context.event
        return if (targetType.isInstance(event)) Result.success(event)
        else Result.failure(BindException("The type of event is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供 [EventListenerProcessingContext] 作为参数。
 */
private class EventListenerProcessingContextInstanceBinder(private val targetType: KClass<EventProcessingContext>) :
    ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前context类型是目标类型的子类，提供参数
        return if (targetType.isInstance(context)) Result.success(context)
        else Result.failure(BindException("The type of context is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供监听函数自身作为参数。
 */
private class EventListenerInstanceBinder(private val targetType: KClass<EventListener>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前listener类型是目标类型的子类，提供参数
        val listener = context.listener
        return if (targetType.isInstance(listener)) Result.success(listener)
        else Result.failure(BindException("The type of listener is inconsistent with the target type $targetType"))
    }
}

private inline fun attributeBinder(nullable: Boolean, attribute: Attribute<*>, nullMessageBlock: () -> String): AttributeBinder {
    return if (nullable) AttributeBinder.Nullable(attribute)
    else AttributeBinder.Notnull(attribute, nullMessageBlock())
}

private sealed class AttributeBinder : ParameterBinder {

    protected abstract val attribute: Attribute<*>

    class Nullable(override val attribute: Attribute<*>) : AttributeBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            return context.getAttribute(attribute).let { Result.success(it) }
        }
    }

    class Notnull(override val attribute: Attribute<*>, private val nullMessage: String) : AttributeBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            return context.getAttribute(attribute)?.let { Result.success(it) }
                ?: Result.failure(NullPointerException(nullMessage))
        }
    }
}
