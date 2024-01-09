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

package love.forte.simbot.common.collection

import java.util.concurrent.ConcurrentHashMap


/**
 * 由平台实现的 [MutableMap] `merge` 操作。
 *
 * 提供 [key] 和 [value]，如果 `map` 中不存在 [key] 对应的值，则存入此键值对。
 * 如果存在与 [key] 冲突的记录，通过 [remapping] 函数计算新值。
 * 当计算新值不为 `null` 时存入新值，否则移除旧值。
 *
 * 在 JVM 平台中，会被委托给 `java.util.Map.merge`，
 * 其他平台会有相应的实现，但是可能无法保证原子操作。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.mergeValue(
    key: K,
    value: V & Any,
    crossinline remapping: (V & Any, V & Any) -> V?
): V? =
    merge(key, value) { k, v -> remapping(k, v) }

/**
 * 由平台实现的 [MutableMap] `compute` 操作。
 *
 * 提供 [key] 并从 `map` 中通过 [remapping] 进行计算。
 * 其中 [remapping] 的 [K] 为 [key]，[V] 为 `map` 中已经存在的与 [key] 匹配的值，如果没有则为 `null`。
 * 当 [remapping] 的计算结果不为 `null` 时，插入此值并返回，否则删除原有的值（如果有的话）并返回 `null`。
 *
 * 在 JVM 平台中，会被委托给 `java.util.Map.compute`，
 * 其他平台会有相应的实现，但是可能无法保证原子操作。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValue(key: K, crossinline remapping: (K, V?) -> V?): V? =
    compute(key) { k, v -> remapping(k, v) }

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作，
 * 会被委托给 [Map.computeIfPresent][java.util.Map.computeIfPresent]。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果没有与之匹配的值，
 * 则通过 [remapping] 计算并存入后返回此计算值，否则直接返回得到的匹配值。
 *
 *
 * @see java.util.Map.computeIfPresent
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, crossinline remapping: (K) -> V): V =
    computeIfAbsent(key) { k -> remapping(k) }

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果有与之匹配的值，
 * 则通过 [mappingFunction] 计算并存入后返回此计算值，否则直接返回 `null`。
 * 如果 [mappingFunction] 的计算结果为 `null`，则会移除原本的值后返回 `null`。
 *
 * 在 JVM 平台中，会被委托给 `java.util.Map.computeIfPresent`，
 * 其他平台会有相应的实现，但是可能无法保证原子操作。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfPresent(
    key: K,
    crossinline mappingFunction: (K, V & Any) -> V?
): V? = computeIfPresent(key) { k, v -> mappingFunction(k, v) }

/**
 * 得到 [ConcurrentHashMap].
 */
public actual fun <K, V> concurrentMutableMap(): MutableMap<K, V> = ConcurrentHashMap()
