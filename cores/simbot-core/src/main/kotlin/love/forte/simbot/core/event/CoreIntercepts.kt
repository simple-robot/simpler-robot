/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

@file:JvmName("CoreInterceptUtil")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.MatchableEventListener


// TODO update doc

/**
 *
 *
 */
public operator fun EventListener.plus(interceptors: Collection<EventListenerInterceptor>): EventListener {
    if (interceptors.isEmpty()) return this
    if (this !is MatchableEventListener) {
        val entrance = EventInterceptEntrance.eventListenerInterceptEntrance(interceptors)
        return proxy { listener ->
            entrance.doIntercept(this) { context0 ->
                listener(context0)
            }
        }
    }
    
    TODO()
    
   return plus(interceptors)
}

// TODO update doc

/**
 *
 *
 */
public operator fun MatchableEventListener.plus(interceptors: Collection<EventListenerInterceptor>): MatchableEventListener {
    if (interceptors.isEmpty()) return this
    
    val groupByIsAfterMatch = interceptors.groupBy { it.point == EventListenerInterceptor.Point.AFTER_MATCH }
    
    val normalInterceptors = groupByIsAfterMatch[false] ?: emptyList()
    val afterMatchInterceptors = groupByIsAfterMatch[true] ?: emptyList()
    
    
    val normalEntrance = EventInterceptEntrance.eventListenerInterceptEntrance(normalInterceptors)
    val afterMatchEntrance = EventInterceptEntrance.eventListenerInterceptEntrance(afterMatchInterceptors)
    
    return proxy({ listener ->
        TODO()
    }) { listener ->
        TODO()
    }
    
    // return proxy { listener ->
    //     normalEntrance.doIntercept(this) { context0 ->
    //         if (listener.match(context0)) {
    //             afterMatchEntrance.doIntercept(context0) { context1 ->
    //                 listener(context1)
    //             }
    //         } else {
    //             EventResult.invalid()
    //         }
    //     }
    // }
}


// internal class EventListenerWithInterceptor(
//     private val listener: EventListener,
//     interceptors: Collection<EventListenerInterceptor>
// ) : EventListener by listener {
//     private val entrance = EventListenerIteratorInterceptEntrance(listener, interceptors)
//     override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
//         return entrance.doIntercept(context)
//     }
//
// }

