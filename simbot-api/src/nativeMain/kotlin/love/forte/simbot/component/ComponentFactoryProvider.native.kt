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

package love.forte.simbot.component

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

private val globalProviderCreators =
    mutableListOf<() -> ComponentFactoryProvider<*>>()

private val lock = SynchronizedObject()

/**
 * 添加一个 [providerCreator] 到一个全局列表中。
 */
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
public actual fun addProvider(providerCreator: () -> ComponentFactoryProvider<*>) {
    synchronized(lock) {
        globalProviderCreators.add(providerCreator)
    }
}

/**
 * 清理所有通过 [addProvider] 添加的 provider 构建器。
 */
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
public actual fun clearProviders() {
    synchronized(lock) {
        globalProviderCreators.clear()
    }
}


/**
 * 获取通过 [addProvider] 添加的内容的副本序列。
 */
public actual fun loadComponentProviders(): Sequence<ComponentFactoryProvider<*>> {
    return synchronized(lock) {
        globalProviderCreators.toList()
    }.asSequence().map { it() }
}

