/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.plugin

import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.ApplicationEventRegistrar
import love.forte.simbot.component.Components
import love.forte.simbot.event.EventDispatcher

/**
 * 提供给 [PluginFactoriesConfigurator] 用于配置 [Plugin] 的上下文信息。
 * 可以得到来自 [Application][love.forte.simbot.application.Application] 的初始化配置信息
 * 和 [love.forte.simbot.component.Component] 的配置信息。
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
     * 目前构建得到的 [love.forte.simbot.component.Components]
     */
    public val components: Components

    /**
     * 事件调度器。
     */
    public val eventDispatcher: EventDispatcher
}
