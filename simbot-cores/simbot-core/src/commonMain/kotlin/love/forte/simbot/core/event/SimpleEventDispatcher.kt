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

package love.forte.simbot.core.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.Application
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.core.application.Simple
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.core.event.impl.SimpleEventDispatcherImpl
import love.forte.simbot.event.*

/**
 * 通过 [Simple] 构建 [Application] 时的实际调度器类型。
 *
 * [SimpleEventDispatcher] 实现 [EventDispatcher] 并提供最基础的完整功能实现。
 */
public interface SimpleEventDispatcher : EventDispatcher

/**
 * 构建一个 [SimpleEventDispatcher].
 */
@ExperimentalSimbotAPI
public fun createSimpleEventDispatcherImpl(configurer: ConfigurerFunction<SimpleEventDispatcherConfiguration>): SimpleEventDispatcher {
    val configuration = SimpleEventDispatcherConfigurationImpl()
    configurer.invokeWith(configuration)
    return SimpleEventDispatcherImpl(configuration)
}

/**
 * 构建一个 [SimpleEventDispatcher].
 */
@ExperimentalSimbotAPI
public fun createSimpleEventDispatcherImpl(configuration: SimpleEventDispatcherConfigurationImpl): SimpleEventDispatcher {
    return SimpleEventDispatcherImpl(configuration)
}


/**
 * Represents the type alias SimpleLP which is an abbreviation for SimpleEventListenerRegistrationProperties.
 * This type alias can be used to define variables or parameters that are expected to have the same semantic meaning as
 * SimpleEventListenerRegistrationProperties.
 *
 * @see SimpleEventListenerRegistrationProperties
 */
public typealias SimpleLP = SimpleEventListenerRegistrationProperties

/**
 * Represents the registration properties for a SimpleEventInterceptor.
 *
 * The SimpleIP (SimpleEventInterceptorRegistrationProperties) typealias is introduced
 * to provide a shorthand notation for the SimpleEventInterceptor registration properties.
 */
public typealias SimpleIP = SimpleEventInterceptorRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件处理器的注册属性的扩展类型。
 */
public interface SimpleEventListenerRegistrationProperties : EventListenerRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件处理拦截器的注册属性的扩展类型。
 */
public interface SimpleEventInterceptorRegistrationProperties : EventInterceptorRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件调度拦截器的注册属性的扩展类型。
 */
public interface SimpleEventDispatchInterceptorRegistrationProperties : EventDispatchInterceptorRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件调度器的配置类型的扩展。
 */
public interface SimpleEventDispatcherConfiguration : EventDispatcherConfiguration

