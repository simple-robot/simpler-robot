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

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.MergeableFactoriesConfigurator

/**
 * 用于对 [PluginFactory] 进行聚合组装的配置器。
 */
public class PluginFactoriesConfigurator(
    configurators: Map<PluginFactory.Key, ConfigurerFunction<Any>> = emptyMap(),
    factories: Map<PluginFactory.Key, (PluginConfigureContext) -> Plugin> = emptyMap(),
) : MergeableFactoriesConfigurator<PluginConfigureContext, Plugin, PluginFactory.Key>(configurators, factories)
