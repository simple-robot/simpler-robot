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

import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.randomID
import org.slf4j.Logger


/**
 * 用于构建一个 [EventListener]
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class CoreListenerBuilder<E : Event>(private val eventKey: Event.Key<E>) {
    public var id: ID? = null
    public var isAsync: Boolean = false
    
    private val logger: Logger? = null
    private var matcher: (suspend EventListenerProcessingContext.(E) -> Boolean)? = null
    
    /**
     *
     */
    public fun match(matcher: suspend EventListenerProcessingContext.(E) -> Boolean) {
        this.matcher.also { old ->
            if (old == null) {
                this.matcher = matcher
            } else {
                this.matcher = {
                    old(it)
                    matcher(it)
                }
            }
        }
    }
    
    
    private var handle: (suspend EventListenerProcessingContext.(E) -> EventResult)? = null
    
    /**
     * 注册一个事件处理函数。只有一个生效，重复调用会覆盖前者。
     */
    public fun handle(handleFunction: suspend EventListenerProcessingContext.(E) -> EventResult) {
        handle = handleFunction
    }
    
    
    public fun build(): EventListener {
        val id0 = id ?: randomID()
        val logger0 = logger ?: LoggerFactory.getLogger("love.forte.core.listener.$id0")
        val handle0 = handle ?: throw SimbotIllegalStateException("Handle function for Listener is required.")
        return simpleListener(eventKey, id0, isAsync, logger0, null, matcher ?: { true }, handle0)
    }
    
}