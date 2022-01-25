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
 *
 */

@file:JvmName("CoreInterceptUtil")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.event.*


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
    private val listener: EventListener,
    interceptors: Collection<EventListenerInterceptor>
) : EventListener by listener {
    private val entrance = EventListenerIteratorInterceptEntrance(listener, interceptors)
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return entrance.doIntercept(context)
    }

}

