package love.forte.simboot.core.listener

import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


/**
 * 如果参数是 [Event]、[EventListenerProcessingContext]、[EventListener] 类型, 检测并通过 [EventListenerProcessingContext] 中提供的内容进行提供。
 *
 */
public object EventParameterBinderFactory : ParameterBinderFactory {
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val classifier = context.parameter.type.classifier
        if (classifier !is KClass<*>) return ParameterBinderResult.empty()

        if (classifier.isSubclassOf(Event::class)) {
            @Suppress("UNCHECKED_CAST")
            return ParameterBinderResult.normal(EventInstanceBinder(classifier as KClass<Event>))
        }

        // 是不是 Event子类型
        // 是不是 EventListener 子类型
        // 是不是 EventListenerProcessingContext 子类型


        TODO()
    }
}

/**
 * 提供事件本体作为参数。
 */
private class EventInstanceBinder(private val targetEventType: KClass<Event>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        TODO("Not yet implemented")
    }
}

/**
 * 提供 [EventListenerProcessingContext] 作为参数。
 */
private class EventListenerProcessingContextInstanceBinder : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        TODO("Not yet implemented")
    }
}

/**
 * 提供监听函数自身作为参数。
 */
private class EventListenerInstanceBinder(private val targetEventType: KClass<Event>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        TODO("Not yet implemented")
    }
}