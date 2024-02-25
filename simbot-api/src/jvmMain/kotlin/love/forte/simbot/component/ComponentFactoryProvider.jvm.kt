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

@file:JvmName("ComponentFactoryProviders")
@file:JvmMultifileClass

package love.forte.simbot.component

import love.forte.simbot.common.services.Services
import java.util.*
import kotlin.streams.asSequence

/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public actual fun loadComponentProviders(): Sequence<ComponentFactoryProvider<*>> {
    val globalsCopy = Services.loadProviders<ComponentFactoryProvider<*>>().map { it() }
    val loaded = ServiceLoader.load(ComponentFactoryProvider::class.java)
        .stream().map { it.get() }.asSequence()

    return globalsCopy + loaded
}

/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentProviders(loader: ClassLoader): Sequence<ComponentFactoryProvider<*>> {
    val globalsCopy = Services.loadProviders<ComponentFactoryProvider<*>>().map { it() }
    val loaded = ServiceLoader.load(ComponentFactoryProvider::class.java, loader)
        .stream().map { it.get() }.asSequence()

    return globalsCopy + loaded
}

/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentFactoriesFromProviders(
    loader: ClassLoader,
    loadConfigurers: Boolean
): Sequence<ComponentFactory<*, *>> {
    return loadComponentProviders(loader).map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

