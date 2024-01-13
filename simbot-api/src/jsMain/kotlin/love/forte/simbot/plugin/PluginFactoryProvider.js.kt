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

import love.forte.simbot.component.addProvider

private val globalProviderCreators = mutableListOf<() -> PluginFactoryProvider<*>>()

/**
 * 添加一个用于获取 [PluginFactoryProvider] 的函数到全局。
 */
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
public actual fun addProvider(providerCreator: () -> PluginFactoryProvider<*>) {
    globalProviderCreators.add(providerCreator)
}

/**
 * 清理所有通过 [addProvider] 添加的 provider 构建器。
 */
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
public actual fun clearProviders() {
    globalProviderCreators.clear()
}

/**
 * 加载所有通过 [addProvider] 添加的函数构建出来的 [PluginFactoryProvider] 实例。
 */
public actual fun loadPluginProviders(): Sequence<PluginFactoryProvider<*>> {
    return globalProviderCreators.toList().asSequence().map { it() }
}
