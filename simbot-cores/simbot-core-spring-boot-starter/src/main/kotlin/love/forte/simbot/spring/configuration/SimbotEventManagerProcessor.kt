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

package love.forte.simbot.spring.configuration

import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerRegistrar
import love.forte.simbot.quantcat.common.annotations.Listener


/**
 * 针对 [EventDispatcher] 的处理器。
 * 默认会加载使用 [SimbotEventListenerRegistrarProcessor] 和所有 [SimbotEventDispatcherPostConfigurer]。
 *
 * @author ForteScarlet
 */
public interface SimbotEventDispatcherProcessor {
    /**
     * 处理 [dispatcher]
     */
    public fun process(dispatcher: EventDispatcher)
}

/**
 * 默认行为下对 [EventDispatcher] 的配置接口。
 *
 * 可注册多个。
 *
 */
public interface SimbotEventDispatcherPostConfigurer {
    /**
     * 配置 [dispatcher]
     */
    public fun configure(dispatcher: EventDispatcher)
}

/**
 * 针对 [EventListenerRegistrar] 的处理器。
 * 默认会：
 * - 加载使用扫描所有 bean 的函数（标记了 [Listener] 的）并转化、
 * - 所有的注册的 [EventListener] 实例
 * - 所有 [SimbotEventListenerRegistrarPostConfigurer]。
 */
public interface SimbotEventListenerRegistrarProcessor {
    /**
     * 处理 [registrar]
     */
    public fun process(registrar: EventListenerRegistrar)
}

/**
 * 默认行为下对 [EventListenerRegistrar] 的配置接口。
 *
 * 可注册多个。
 *
 */
public interface SimbotEventListenerRegistrarPostConfigurer {
    /**
     * 配置 [registrar]
     */
    public fun configure(registrar: EventListenerRegistrar)
}
