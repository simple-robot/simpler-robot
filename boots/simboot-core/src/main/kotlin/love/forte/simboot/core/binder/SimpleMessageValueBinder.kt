/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 * @see love.forte.simboot.annotation.MessageValue
 */
// TODO
public object MessageValueBinderFactory : ParameterBinderFactory {
    private val logger = LoggerFactory.getLogger(MessageValueBinderFactory::class.java)
    
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val tool = KAnnotationTool()
        val parameter = context.parameter
        val messageValue = tool.getAnnotation<MessageValue>(parameter) ?: return ParameterBinderResult.empty()
        val isNullable = parameter.type.isMarkedNullable
        
        val targetType: Message.Key<*>
        val classifier = parameter.type.classifier
        if (classifier is KClass<*>) {
            return ParameterBinderResult.normal(classifier.resolve(isNullable))
        } else if (classifier is KTypeParameter) {
            return ParameterBinderResult.normal(classifier.resolve(isNullable))
        } else {
            // TODO no
            return ParameterBinderResult.empty()
        }
        
        
    }
    
    
    private fun KClass<*>.resolve(isNullable: Boolean): ParameterBinder {
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
    
    private fun KTypeParameter.resolve(isNullable: Boolean): ParameterBinder {
        
        
        TODO()
    }
    
    
}


private sealed class MessagesSelfBinder : ParameterBinder {
    object Nullable : MessagesSelfBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            val event = context.event
            return if (event is MessageEvent) {
                Result.success(event.messageContent.messages)
            } else {
                Result.success(null)
            }
        }
    }
    
    object NotNull : MessagesSelfBinder() {
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




