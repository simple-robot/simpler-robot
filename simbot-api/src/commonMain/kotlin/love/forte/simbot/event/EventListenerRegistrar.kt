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

package love.forte.simbot.event

import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.common.function.ConfigurerFunction


/**
 * 事件处理器的注册器。
 * 用于承载注册、管理处理器的职责。
 *
 * @author ForteScarlet
 */
public interface EventListenerRegistrar {
    /**
     * 注册一个 [EventListener] 并附加部分额外属性信息。
     *
     * 不同的 [EventListenerRegistrar] 可能会支持属性更丰富的 [EventListenerRegistrationProperties]
     *
     * @return 注册后的句柄。可用于注销此次注册
     */
    public fun register(
        propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?,
        listener: EventListener,
    ): EventListenerRegistrationHandle

    /**
     * 注册一个默认属性的 [EventListener].
     *
     * @return 注册后的句柄。可用于撤销/消除此次注册
     */
    public fun register(listener: EventListener): EventListenerRegistrationHandle = register(null, listener)

    /**
     * 注销所有通过指定的 [EventListener] 所注册的事件处理器。
     *
     * 进行匹配时会使用 [EventListener.equals] 作为匹配是否相等的依据。
     */
    public fun dispose(listener: EventListener)

}

/**
 * 为特定事件类型注册事件处理器函数。
 *
 * @param E The type of event to register the listener for.
 * @param propertiesConsumer An optional function that consumes the configuration properties of the listener registration.
 *        The function should have the signature `ConfigurerFunction<EventListenerRegistrationProperties>`.
 * @param defaultResult The default result function that will be invoked if the event is not of type `E`.
 *        The function should have the signature `EventContext.() -> EventResult`.
 *        By default, it returns an invalid result.
 * @param listenerFunction The listener function that will be invoked when the event is fired.
 *        The function should have the signature `suspend EventContext.(E) -> EventResult`,
 *        where `EventContext` represents the context of the event and `E` is the event type.
 *
 * @throws IllegalArgumentException If the listener function is not provided.
 */
public inline fun <reified E : Event> EventListenerRegistrar.listen(
    propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>? = null,
    crossinline defaultResult: EventListenerContext.() -> EventResult = { EventResult.invalid },
    crossinline listenerFunction: suspend EventListenerContext.(E) -> EventResult,
) {
    register(
        propertiesConsumer = propertiesConsumer,
        listener = {
            val event = this.event
            if (event is E) listenerFunction(this, event) else defaultResult(this)
        })
}

/**
 * 注册事件监听器的额外属性。
 *
 * [EventListenerRegistrationProperties] 可由 [EventListenerRegistrar] 的实现者自由扩展，
 * 但应当至少能够支持最基础的几项属性，并至少在不支持的情况下提供警告日志或异常。
 */
public interface EventListenerRegistrationProperties {
    /**
     * 优先级。数值越小优先级越高。通常默认为 [PriorityConstant.DEFAULT]。
     */
    public var priority: Int

    /**
     * 为此监听函数添加一个独特的拦截器。
     * 拦截器的优先级与最终此监听函数被添加的全局性拦截器共享。
     */
    public fun addInterceptor(
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
        interceptor: EventInterceptor
    )

    /**
     * 为此监听函数添加一个独特的拦截器。
     */
    public fun addInterceptor(interceptor: EventInterceptor) {
        addInterceptor(null, interceptor)
    }
}

/**
 * 监听器注册成功后得到的对应的句柄。
 *
 */
public interface EventListenerRegistrationHandle {
    /**
     * 取消对应监听器的注册。
     */
    public fun dispose()
}



