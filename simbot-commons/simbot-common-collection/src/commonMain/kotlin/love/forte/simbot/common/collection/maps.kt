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

/**
 * 由平台实现的 [MutableMap] `merge` 操作。
 *
 * 提供 [key] 和 [value]，如果 `map` 中不存在 [key] 对应的值，则存入此键值对。
 * 如果存在与 [key] 冲突的记录，通过 [remapping] 函数通过当前值和旧值计算新值。
 * 当计算新值不为 `null` 时存入新值，否则移除旧值。
 *
 * 在 JVM 平台中，会被委托给 `java.util.Map.merge`，
 * 其他平台会有相应的实现，但是可能无法保证原子操作。
 *
 */
public expect inline fun <K, V> MutableMap<K, V>.mergeValue(
    key: K,
    value: V & Any,
    crossinline remapping: (V & Any, V & Any) -> V?
): V?

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
public expect inline fun <K, V> MutableMap<K, V>.computeValue(key: K, crossinline remapping: (K, V?) -> V?): V?

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果没有与之匹配的值，
 * 则通过 [remapping] 计算并存入后返回此计算值，否则直接返回得到的匹配值。
 *
 * 在 JVM 平台中，会被委托给 `java.util.Map.computeIfPresent`，
 * 其他平台会有相应的实现，但是可能无法保证原子操作。
 *
 */
public expect inline fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, crossinline remapping: (K) -> V): V

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
public expect inline fun <K, V> MutableMap<K, V>.computeValueIfPresent(
    key: K,
    crossinline mappingFunction: (K, V & Any) -> V?
): V?

/**
 * 根据 [key] 删除指定的目标 [target]。
 */
public expect inline fun <K, V> MutableMap<K, V>.removeValue(key: K, crossinline target: () -> V): Boolean

/**
 * 如果平台支持，则得到一个可以并发操作的 [MutableMap]。
 *
 * 根据不同的平台实现，得到的 [MutableMap] 允许在迭代过程中
 * （例如使用 [MutableMap.keys]、[MutableMap.values]、[MutableMap.entries]）
 * 对原 map 进行修改，而不会引发 [ConcurrentModificationException]，
 * 但正在迭代的迭代器不保证可以实时感知到已经发生的修改。换言之这种并发修改是弱一致性的。
 *
 * [concurrentMutableMap] 得到的结果可能是弱一致性的。
 */
public expect fun <K, V> concurrentMutableMap(): MutableMap<K, V>

@PublishedApi
internal inline fun <K, V> MutableMap<K, V>.internalMergeImpl(
    key: K,
    value: V & Any,
    remapping: (V & Any, V & Any) -> V?
): V? {
    val old = get(key)
    val newValue = if (old == null) value else remapping(old, value)

    if (newValue == null) {
        if (old != null) {
            remove(key)
        }
    } else {
        put(key, newValue)
    }

    return newValue
}

@PublishedApi
internal inline fun <K, V> MutableMap<K, V>.internalComputeImpl(key: K, remapping: (K, V?) -> V?): V? {
    val value = get(key)
    val newValue = remapping(key, value)
    if (newValue == null) {
        if (value != null) {
            remove(key)
        }
        return null
    }

    put(key, newValue)
    return newValue
}

@PublishedApi
internal inline fun <K, V> MutableMap<K, V>.internalComputeIfAbsentImpl(key: K, remapping: (K) -> V): V {
    val value = get(key)
    if (value == null) {
        val newValue = remapping(key)
        put(key, newValue)
        return newValue
    }

    return value
}

@PublishedApi
internal inline fun <K, V> MutableMap<K, V>.internalComputeIfPresentImpl(key: K, mappingFunction: (K, V & Any) -> V?): V? {
    val old = get(key)
    if (old != null) {
        val newValue = mappingFunction(key, old)
        if (newValue != null) {
            put(key, newValue)
            return newValue
        } else {
            remove(key)
            return null
        }
    }

    return null
}


@PublishedApi
internal inline fun <K, V> MutableMap<K, V>.internalRemoveValueImpl(
    key: K,
    crossinline target: () -> V
): Boolean {
    val targetValue = target()
    val iter = iterator()
    while (iter.hasNext()) {
        val entry = iter.next()
        if (entry.key == key && entry.value == targetValue) {
            iter.remove()
            return true
        }
    }

    return false
}
