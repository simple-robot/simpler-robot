/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("EventListeners")
@file:JvmMultifileClass

package love.forte.simbot.event

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 一个事件 [Event] 的监听器。也可以称之为事件处理器。
 * [EventListener] 是针对一个 [Event] 进行处理的逻辑单元。
 *
 * _Note: Java 可参考使用 [JBlockingEventListener][love.forte.simbot.event.JBlockingEventListener]、[TypedJBlockingEventListener][love.forte.simbot.event.TypedJBlockingEventListener]、[JAsyncEventListener][love.forte.simbot.event.JAsyncEventListener]、[TypedJAsyncEventListener][love.forte.simbot.event.TypedJAsyncEventListener] 等扩展类型。_
 *
 * @author ForteScarlet
 */
public fun interface EventListener {
    /**
     * 处理事件，并得到一个处理的响应。
     *
     * @throws Exception 任何可能在处理过程中抛出的异常
     */
    @JvmSynthetic
    @Throws(Exception::class)
    public suspend fun EventListenerContext.handle(): EventResult
}

/**
 * @see EventListener.handle
 */
public suspend fun EventListener.handleWith(context: EventListenerContext): EventResult {
    return context.run { handle() }
}

