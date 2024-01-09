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

package love.forte.simbot.plugin

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.ApplicationEventRegistrar
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.MergeableFactoriesConfigurator
import love.forte.simbot.common.function.MergeableFactory
import love.forte.simbot.component.Component
import love.forte.simbot.component.Components
import love.forte.simbot.event.EventDispatcher

/**
 *
 * 一个 **插件**。
 *
 * [Plugin] 应用于 [Application] 中，
 * 在所有组件 [Component][love.forte.simbot.component.Component]
 * 加载完成后进入配置阶段。
 *
 * 插件同样配置于事件处理器之后，因此 [Plugin] 最主要的职责之一便是与事件打交道——
 * 比如实现通过某种方式产生事件、并推送给事件处理器。
 *
 * [Plugin] 无所谓形式，可以是一个 [BotManager][love.forte.simbot.bot.BotPlugin],
 * 或是一个定时任务、一个http服务, 或者其他任何什么。
 *
 * [BotPlugin][love.forte.simbot.bot.BotPlugin] 是 [Plugin] 的一个特殊类型，详情可参考其说明。
 *
 * @author ForteScarlet
 */
public interface Plugin

/**
 * [Plugin] 的工厂函数，用于配置并预构建 [Plugin] 实例。
 *
 * @see Plugin
 * @param P 目标组件类型
 * @param CONF 配置类型。配置类型应是一个可变类，以便于在 DSL 中进行动态配置。
 */
public interface PluginFactory<P : Plugin, CONF : Any> :
    MergeableFactory<PluginFactory.Key, P, CONF, PluginConfigureContext> {
    /**
     * 用于 [PluginFactory] 在内部整合时的标识类型。
     *
     * 更多说明参阅 [MergeableFactory.Key]。
     *
     * @see PluginFactory.key
     * @see MergeableFactory.key
     */
    public interface Key : MergeableFactory.Key
}

/**
 * 一个 [Plugin] 的安装器接口，
 * 提供用于安装 [Plugin] 的能力。
 */
public interface PluginInstaller {
    /**
     * 注册安装一个插件 [Plugin] 类型，并为其添加一个对应的配置。
     */
    public fun <P : Plugin, CONF : Any> install(
        pluginFactory: PluginFactory<P, CONF>, configurer: ConfigurerFunction<CONF>
    )

    /**
     * 注册安装一个插件 [Plugin] 类型。
     */
    public fun <P : Plugin, CONF : Any> install(pluginFactory: PluginFactory<P, CONF>) {
        install(pluginFactory) {}
    }
}

/**
 * 提供给 [PluginFactoriesConfigurator] 用于配置 [Plugin] 的上下文信息。
 * 可以得到来自 [Application][love.forte.simbot.application.Application] 的初始化配置信息
 * 和 [Component] 的配置信息。
 */
public interface PluginConfigureContext {
    /**
     * 构建 Application 的配置信息
     */
    public val applicationConfiguration: ApplicationConfiguration

    /**
     * Application 的阶段事件注册器。
     */
    public val applicationEventRegistrar: ApplicationEventRegistrar

    /**
     * 目前构建得到的 [Components]
     */
    public val components: Components

    /**
     * 事件调度器。
     */
    public val eventDispatcher: EventDispatcher
}

/**
 * 用于对 [PluginFactory] 进行聚合组装的配置器。
 */
public class PluginFactoriesConfigurator(
    configurators: Map<PluginFactory.Key, ConfigurerFunction<Any>> = emptyMap(),
    factories: Map<PluginFactory.Key, (PluginConfigureContext) -> Plugin> = emptyMap(),
) : MergeableFactoriesConfigurator<PluginConfigureContext, Plugin, PluginFactory.Key>(configurators, factories)

