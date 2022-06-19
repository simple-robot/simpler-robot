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

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
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
    private var id: String? = null
    private var isAsync: Boolean = false
    private var matcher: BiPredicate<EventListenerProcessingContext, E>? = null
    private var handler: BiFunction<EventListenerProcessingContext, E, EventResult>? = null
    
    /**
     * 配置当前id。
     *
     * 如果不配置则id随机。
     *
     * @see EventListener.id
     */
    public fun id(id: String): SimpleListenerBuilder<E> = also {
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
    @Suppress("UNUSED_PARAMETER")
    @Deprecated("Will be remove", ReplaceWith("this"))
    public fun logger(logger: Logger): SimpleListenerBuilder<E> = this
    
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
        // return simpleListener(id = id.ID, )
        TODO()
        // return simpleListener()
    }
}