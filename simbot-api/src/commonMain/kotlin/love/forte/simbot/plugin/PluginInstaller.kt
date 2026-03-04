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
