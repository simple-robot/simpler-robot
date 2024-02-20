/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

@file:JvmName("EventContinuousSessionContexts")
@file:JvmMultifileClass

package love.forte.simbot.extension.continuous.session

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.common.coroutines.mergeWith
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventResult
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 以事件为中心的 [ContinuousSessionContext] 子类型。
 *
 * ## 插件
 *
 * [EventContinuousSessionContext] 实现 [Plugin], 可以作为 [Application] 的插件安装使用。
 *
 * ```kotlin
 * launchSimpleApplication {
 *     install(EventContinuousSessionContext) {
 *         // 一些可选的配置...
 *     }
 * }
 * ```
 *
 * [EventContinuousSessionContext] 暂时**不支持SPI**，它需要用户明确的按需加载。
 *
 * ## 持续会话
 *
 * 有关持续会话等详细说明参阅 [ContinuousSessionContext] 的文档说明。
 *
 * @see ContinuousSessionContext
 */
public interface EventContinuousSessionContext : ContinuousSessionContext<Event, EventResult>, Plugin {

    public companion object Factory :
        PluginFactory<EventContinuousSessionContext, EventContinuousSessionContextConfiguration> {
        override val key: PluginFactory.Key = object : PluginFactory.Key {}

        override fun create(
            context: PluginConfigureContext,
            configurer: ConfigurerFunction<EventContinuousSessionContextConfiguration>
        ): EventContinuousSessionContext {
            val config = EventContinuousSessionContextConfiguration()
            configurer.invokeWith(config)
            val newCoroutineContext =
                config.coroutineContext.mergeWith(context.applicationConfiguration.coroutineContext)

            return EventContinuousSessionContextImpl(newCoroutineContext)
        }
    }
}

private class EventContinuousSessionContextImpl(coroutineContext: CoroutineContext) :
    EventContinuousSessionContext,
    ContinuousSessionContext<Event, EventResult> by ContinuousSessionContext(coroutineContext)

/**
 * [EventContinuousSessionContext.Factory] 使用的配置类。
 */
public class EventContinuousSessionContextConfiguration {
    /**
     * 用于 [EventContinuousSessionContext] 中的协程上下文。
     *
     * 值来自 [ApplicationConfiguration.coroutineContext]，但不包含 Job。
     * 如果配置后 [coroutineContext] 存在 [Job], 则会基于此以及 [ApplicationConfiguration.coroutineContext]
     * 合成一个新的 [Job]。
     *
     * 在 Java 中时，如果你打算在逻辑中使用 **阻塞** API，那么建议为其配置虚拟线程调度器；
     * 否则，建议在其中使用异步 API，例如 `InSessions.async`。
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 为 [coroutineContext] 配置调度器。
     * 如果为 `null` 则移除调度器。
     */
    @OptIn(ExperimentalStdlibApi::class)
    public var coroutineDispatcher: CoroutineDispatcher?
        get() = coroutineContext[CoroutineDispatcher]
        set(value) {
            if (value == null) {
                coroutineContext = coroutineContext.minusKey(CoroutineDispatcher)
            } else {
                coroutineContext += value
            }
        }
}
