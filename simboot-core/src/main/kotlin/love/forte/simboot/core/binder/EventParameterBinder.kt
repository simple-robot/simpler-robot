/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simboot.core.binder

import kotlinx.serialization.modules.SerializersModule
import love.forte.simboot.core.listener.BootListenerAttributes
import love.forte.simboot.listener.BindException
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.Attribute
import love.forte.simbot.AttributeContainer
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.application.Application
import love.forte.simbot.core.application.ApplicationAttributes
import love.forte.simbot.core.scope.SimpleScope
import love.forte.simbot.event.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.safeCast


/**
 * 如果参数是 [Event]、[EventListenerProcessingContext]、[EventListener]、[SerializersModule] 类型, 检测并通过 [EventListenerProcessingContext] 中提供的内容进行提供。
 *
 * 同时也会检测一些非集合类型的作用域或属性类型，包括 [SimpleScope]、[ApplicationAttributes]、[BootListenerAttributes] 中的相关属性等。
 *
 */
public object EventParameterBinderFactory : ParameterBinderFactory {
    @OptIn(ExperimentalSimbotApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val type = context.parameter.type
        val classifier = type.classifier as? KClass<*> ?: return ParameterBinderResult.empty()
        
        val nullable = type.isMarkedNullable
        
        return when {
            // Event
            classifier.isSubclassOf(Event::class) -> {
                return ParameterBinderResult.normal(EventInstanceBinder(classifier as KClass<Event>))
            }
            
            // EventListener
            classifier.isSubclassOf(EventListener::class) -> {
                return ParameterBinderResult.normal(EventListenerInstanceBinder(classifier as KClass<EventListener>))
            }
            
            // EventProcessingContext
            classifier.isSubclassOf(EventProcessingContext::class) || classifier.isSubclassOf(InstantScopeContext::class) -> {
                return ParameterBinderResult.normal(
                    EventListenerProcessingContextInstanceBinder(classifier as KClass<EventProcessingContext>)
                )
            }
    
            // SerializersModule
            classifier.isSubclassOf(SerializersModule::class) -> {
                return ParameterBinderResult.normal(
                    SerializersModuleInstanceBinder(classifier as KClass<SerializersModule>)
                )
            }
            
            // Scope 相关类型
            
            classifier.isSubclassOf(GlobalScopeContext::class) -> {
                return ParameterBinderResult.normal(attributeBinder(
                    nullable, SimpleScope.Global, classifier
                ) { "SimpleScope[Global] for type [$classifier] in current context is not found." })
            }
            
            classifier.isSubclassOf(ContinuousSessionContext::class) -> {
                return ParameterBinderResult.normal(attributeBinder(
                    nullable, SimpleScope.ContinuousSession, classifier
                ) { "SimpleScope[ContinuousSession] for type [$classifier] in current context is not found." })
            }
            
            // Application
            classifier.isSubclassOf(Application::class) -> {
                return ParameterBinderResult.normal(attributeBinder(
                    nullable, ApplicationAttributes.Application, classifier
                ) { "ApplicationAttributes[Application] for type [$classifier] in current context is not found." })
            }
            
            // KFunction from listener
            classifier.isSubclassOf(KFunction::class) -> {
                return ParameterBinderResult.normal(attributeBinder(
                    nullable, BootListenerAttributes.RawFunction, classifier,
                    {
                        it.listener
                    },
                ) { "BootListenerAttributes[RawFunction] for type [$classifier] in current context is not found." })
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
        return targetType.safeCast(context.event)?.let { Result.success(it) }
            ?: Result.failure(BindException("The type of EventListenerProcessingContext.event is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供 [EventListenerProcessingContext] 作为参数。
 */
private class EventListenerProcessingContextInstanceBinder(private val targetType: KClass<EventProcessingContext>) :
    ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前context类型是目标类型的子类，提供参数
        return targetType.safeCast(context)?.let { Result.success(it) }
            ?: Result.failure(BindException("The type of current eventListenerProcessingContext is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供监听函数自身作为参数。
 */
private class EventListenerInstanceBinder(private val targetType: KClass<EventListener>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前listener类型是目标类型的子类，提供参数
        return targetType.safeCast(context.listener)?.let { Result.success(it) }
            ?: Result.failure(BindException("The type of EventListenerProcessingContext.listener is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供监听函数自身作为参数。
 */
private class SerializersModuleInstanceBinder(private val targetType: KClass<SerializersModule>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前listener类型是目标类型的子类，提供参数
        return targetType.safeCast(context.messagesSerializersModule)?.let { Result.success(it) }
            ?: Result.failure(BindException("The type of EventListenerProcessingContext.messagesSerializersModule is inconsistent with the target type $targetType"))
    }
}

private inline fun attributeBinder(
    nullable: Boolean,
    attribute: Attribute<*>,
    classifier: KClass<*>,
    noinline attributeMapSelector: (EventListenerProcessingContext) -> AttributeContainer = { it },
    nullMessageBlock: () -> String,
): AttributeBinder {
    return if (nullable) AttributeBinder.Nullable(classifier, attribute, attributeMapSelector)
    else AttributeBinder.Notnull(classifier, attribute, nullMessageBlock(), attributeMapSelector)
}

private sealed class AttributeBinder : ParameterBinder {
    protected abstract val attributeMapSelector: (EventListenerProcessingContext) -> AttributeContainer
    protected abstract val attribute: Attribute<*>
    
    class Nullable(
        private val classifier: KClass<*>, override val attribute: Attribute<*>,
        override val attributeMapSelector: (EventListenerProcessingContext) -> AttributeContainer,
    ) : AttributeBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            return attributeMapSelector(context).getAttribute(attribute)
                .let { value -> Result.success(value.takeIf { classifier.isInstance(it) }) }
        }
    }
    
    class Notnull(
        private val classifier: KClass<*>,
        override val attribute: Attribute<*>,
        private val nullMessage: String,
        override val attributeMapSelector: (EventListenerProcessingContext) -> AttributeContainer,
    ) : AttributeBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            return attributeMapSelector(context).getAttribute(attribute)?.takeIf { classifier.isInstance(it) }
                ?.let { Result.success(it) } ?: Result.failure(NullPointerException(nullMessage))
        }
    }
}
