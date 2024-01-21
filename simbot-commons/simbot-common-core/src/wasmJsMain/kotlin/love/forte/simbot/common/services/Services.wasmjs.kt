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

package love.forte.simbot.common.services

import love.forte.simbot.common.collection.computeValueIfAbsent
import kotlin.reflect.KClass
import kotlin.reflect.cast

private val globalProviderCreators =
    mutableMapOf<KClass<*>, MutableList<() -> Any>>()

/**
 * 添加一个用于获取 [T] 的函数。
 */
internal actual fun <T : Any> addProviderInternal(type: KClass<T>, providerCreator: () -> T) {
    globalProviderCreators.computeValueIfAbsent(type) { mutableListOf() }.add(providerCreator)
}

/**
 * 清理所有通过 [addProviderInternal] 添加的某类型的 provider 构建器。
 */
internal actual fun clearProvidersInternal(type: KClass<*>) {
    globalProviderCreators.clear()
}

/**
 * 获取所有通过 [addProviderInternal] 注册的 [T] 类型的 provider 实例。
 */
internal actual fun <T : Any> loadProvidersInternal(type: KClass<T>): Sequence<() -> T> {
    return globalProviderCreators[type]?.toList()?.asSequence()
        ?.map { provider -> { type.cast(provider()) } }
        ?: emptySequence()
}
