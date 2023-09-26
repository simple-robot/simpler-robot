/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("ALL", "UNUSED_PARAMETER")

package love.forte.simboot.core.binder

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.getAnnotation
import love.forte.simboot.annotation.MessageValue
import love.forte.simboot.listener.BindException
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.isSubclassOf

/**
 * 从 [MessageEvent] 的 [messageContent][MessageEvent.messageContent] 中获取指定元素。
 *
 * ```kotlin
 * suspend fun MessageEvent.onEvent1(@MessageValue firstAt: At?) {
 *      // ...
 * }
 *
 * suspend fun MessageEvent.onEvent2(@MessageValue(isReversed = true) lastAt: At?) {
 *      // ...
 * }
 * ```
 *
 * @suppress TODO
 * @see love.forte.simboot.annotation.MessageValue
 */
// TODO
public object MessageValueBinderFactory : ParameterBinderFactory {
    private val logger = LoggerFactory.getLogger(MessageValueBinderFactory::class.java)
    
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val tool = KAnnotationTool()
        val parameter = context.parameter
        val messageValue = tool.getAnnotation<MessageValue>(parameter) ?: return ParameterBinderResult.empty()
        val isReversed: Boolean = messageValue.isReversed
        val isNullable = parameter.type.isMarkedNullable
        
        // val targetType: Message.Key<*>
        
        return when (val classifier = parameter.type.classifier) {
            is KClass<*> -> {
                ParameterBinderResult.normal(classifier.resolve(isNullable, isReversed))
            }
            is KTypeParameter -> {
                ParameterBinderResult.normal(classifier.resolve(isNullable, isReversed))
            }
            else -> {
                // TODO no
                ParameterBinderResult.empty()
            }
        }
        
        
    }
    
    
    private fun KClass<*>.resolve(isNullable: Boolean, isReversed: Boolean): ParameterBinder {
        // if is type of message
        if (isSubclassOf(Message::class)) {
            when {
                isSubclassOf(Message.Element::class) -> {
                
                }
                isSubclassOf(Messages::class) -> if (isNullable) MessagesSelfBinder.Nullable else MessagesSelfBinder.NotNull
                else -> {
                
                }
            }
        }
        
        TODO()
    }
    
    private fun KTypeParameter.resolve(isNullable: Boolean, isReversed: Boolean): ParameterBinder {
        
        
        TODO()
    }
    
    
}


private sealed class MessagesSelfBinder : ParameterBinder {
    data object Nullable : MessagesSelfBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val event = context.event
            return if (event is MessageEvent) {
                Result.success(event.messageContent.messages)
            } else {
                Result.success(null)
            }
        }
    }
    
    data object NotNull : MessagesSelfBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val event = context.event
            return if (event is MessageEvent) {
                Result.success(event.messageContent.messages)
            } else {
                Result.failure(BindException("Event [$event] is not type of MessageEvent"))
            }
        }
    }
}


private sealed class SimpleMessageValueBinder(
    val targetMessage: Message.Key<*>,
    val isReversed: Boolean,
) : ParameterBinder {
    class Nullable(targetMessage: Message.Key<*>, isReversed: Boolean) :
        SimpleMessageValueBinder(targetMessage, isReversed) {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val event = context.event
            if (event !is MessageEvent) {
                return Result.success(null)
            }
            
            event.messageContent.messages
            TODO("Not yet implemented")
        }
    }
    
    class NotNull(targetMessage: Message.Key<*>, isReversed: Boolean) :
        SimpleMessageValueBinder(targetMessage, isReversed) {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            TODO("Not yet implemented")
        }
    }
}




