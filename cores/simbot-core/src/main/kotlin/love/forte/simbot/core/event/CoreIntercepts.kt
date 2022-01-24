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

@file:JvmName("CoreInterceptUtil")
@file:JvmMultifileClass
package love.forte.simbot.core.event

import love.forte.simbot.event.*

//
// /**
//  * 构建一个独立的监听函数拦截器。
//  *
//  */
// @JvmSynthetic
// public fun coreListenerIntercept(
//     id: ID = UUID.randomUUID().ID,
//     priority: Int = PriorityConstant.NORMAL,
//     interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
// ): EventListenerInterceptor {
//     return CoreFunctionalEventListenerInterceptor(
//         id, priority, interceptFunction
//     )
// }
//
// /**
//  * @see coreListenerIntercept
//  */
// @JvmOverloads
// @JvmName("coreListenerIntercept")
// public fun coreListenerIntercept4J(
//     id: ID = UUID.randomUUID().ID,
//     priority: Int = PriorityConstant.NORMAL,
//     interceptFunction: (EventListenerInterceptor.Context) -> EventResult
// ): EventListenerInterceptor = coreListenerIntercept(id, priority, interceptFunction)

/**
 * 为当前监听函数组合一套拦截器。
 *
 * 假如当前监听函数已经被组合过拦截器，那么本次拦截组合将会直接在原来的基础上进行组合，而不会重新计算优先级。
 *
 * 需要注意，监听函数拼接 [EventListenerInterceptor] 将会直接拼接至 [EventListener.invoke] 函数中，
 * 因此如果你同样需要拼接 [EventFilter], 请在 [拦截器][EventListenerInterceptor] 拼接 **之前** ，否则拦截逻辑将会在过滤器之后执行，
 * 除非你很清楚自己在做什么。
 *
 */
public operator fun EventListener.plus(interceptors: Collection<EventListenerInterceptor>): EventListener {
    return if (interceptors.isEmpty()) return this
    else EventListenerWithInterceptor(this, interceptors.toList())
}


internal class EventListenerWithInterceptor(
    listener: EventListener,
    interceptors: Collection<EventListenerInterceptor>
) : EventListener by listener {
    private val entrance = EventListenerIteratorInterceptEntrance(listener, interceptors)
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return entrance.doIntercept(context)
    }
}

