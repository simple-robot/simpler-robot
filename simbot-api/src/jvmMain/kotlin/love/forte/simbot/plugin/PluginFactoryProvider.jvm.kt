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

@file:JvmName("PluginFactoryProviders")
@file:JvmMultifileClass

package love.forte.simbot.plugin

import java.util.*
import kotlin.streams.asSequence

private val globalProviderCreators = mutableListOf<() -> PluginFactoryProvider<*>>()

/**
 * 添加一个用于获取 [PluginFactoryProvider] 的函数到全局中。
 */
@JvmSynthetic
public actual fun addProvider(providerCreator: () -> PluginFactoryProvider<*>) {
    synchronized(globalProviderCreators) {
        globalProviderCreators.add(providerCreator)
    }
}

/**
 * 清理所有通过 [addProvider] 添加的 provider 构建器。
 */
@JvmSynthetic
public actual fun clearProviders() {
    synchronized(globalProviderCreators) {
        globalProviderCreators.clear()
    }
}

/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public actual fun loadPluginProviders(): Sequence<PluginFactoryProvider<*>> {
    val globalCopy = synchronized(globalProviderCreators) {
        globalProviderCreators.toList()
    }.asSequence().map { it() }

    val loaded = ServiceLoader.load(PluginFactoryProvider::class.java)
        .stream().map { it.get() }.asSequence()

    return globalCopy + loaded
}

/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginProviders(loader: ClassLoader): Sequence<PluginFactoryProvider<*>> {
    val globalCopy = synchronized(globalProviderCreators) {
        globalProviderCreators.toList()
    }.asSequence().map { it() }

    val loaded = ServiceLoader.load(PluginFactoryProvider::class.java, loader)
        .stream().map { it.get() }.asSequence()

    return globalCopy + loaded
}

/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginFactoriesFromProviders(
    loader: ClassLoader,
    loadConfigurers: Boolean
): Sequence<PluginFactory<*, *>> {
    return loadPluginProviders(loader).map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

