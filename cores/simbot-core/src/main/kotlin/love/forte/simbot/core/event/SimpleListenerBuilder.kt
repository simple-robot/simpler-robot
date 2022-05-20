package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult
import org.slf4j.Logger
import java.util.function.BiFunction
import java.util.function.BiPredicate

/**
 * 用于构建一个 [SimpleListener] 监听函数。
 *
 * 主要服务于不支持使用挂起函数的使用方，使用链式风格构建 [EventListener].
 *
 * kotlin中可以直接使用 [simpleListener] 等函数。
 *
 * [SimpleListenerBuilder] 只能用于配置生成一个具体的事件目标，即只能指定一个具体的 [Event.Key].
 *
 *
 * ```java
 * new SimpleListenerBuilder<>(FooEvent.Key)
 *     .id(...)
 *     .async(...)
 *     .logger(...)
 *     .match((context, event) -> {...})
 *     .handle((context, event) -> { ... })
 *     .build();
 * ```
 *
 * @author ForteScarlet
 */
@Api4J
public class SimpleListenerBuilder<E : Event>(public val target: Event.Key<E>) {
    private var id: ID? = null
    private var isAsync: Boolean = false
    private var logger: Logger? = null
    private var matcher: BiPredicate<EventListenerProcessingContext, E>? = null
    private var handler: BiFunction<EventListenerProcessingContext, E, EventResult>? = null
    
    /**
     * 配置当前id。
     *
     * 如果不配置则id随机。
     *
     * @see EventListener.id
     */
    public fun id(id: ID): SimpleListenerBuilder<E> = also {
        this.id = id
    }
    
    /**
     * 配置是否为异步函数。
     *
     * @see EventListener.isAsync
     */
    @JvmOverloads
    public fun async(isAsync: Boolean = true): SimpleListenerBuilder<E> = also {
        this.isAsync = isAsync
    }
    
    /**
     * 配置 [EventListener.logger].
     *
     * @see EventListener.logger
     */
    public fun logger(logger: Logger): SimpleListenerBuilder<E> = also {
        this.logger = logger
    }
    
    /**
     * 配置监听函数的匹配逻辑。
     *
     * @see EventListener.match
     */
    public fun match(matcher: BiPredicate<EventListenerProcessingContext, E>): SimpleListenerBuilder<E> = also {
        this.matcher.also { old ->
            this.matcher = if (old == null) {
                matcher
            } else {
                old.and(matcher)
            }
        }
    }
    
    /**
     * 配置监听函数的执行逻辑。只能被配置一次，重复配置会导致 [IllegalStateException].
     *
     * @throws IllegalStateException 如果已经被配置过
     *
     * @see EventListener.invoke
     */
    public fun handle(handler: BiFunction<EventListenerProcessingContext, E, EventResult>): SimpleListenerBuilder<E> =
        also {
            if (this.handler != null) {
                throw IllegalStateException("handle has been configured")
            }
            this.handler = handler
        }
    
    
    /**
     * 构建并得到目标结果。
     */
    public fun build(): EventListener {
        TODO()
        // return simpleListener()
    }
}