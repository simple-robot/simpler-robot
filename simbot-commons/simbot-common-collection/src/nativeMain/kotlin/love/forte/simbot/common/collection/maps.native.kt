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

import kotlin.concurrent.AtomicReference
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * 由平台实现的 [MutableMap] `merge` 操作。
 *
 * 提供 [key] 和 [value]，如果 `map` 中不存在 [key] 对应的值，则存入此键值对。
 * 如果存在与 [key] 冲突的记录，通过 [remapping] 函数计算新值。
 * 当计算新值不为 `null` 时存入新值，否则移除旧值。
 *
 * 无法保证原子操作。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.mergeValue(
    key: K,
    value: V & Any,
    crossinline remapping: (V & Any, V & Any) -> V?
): V? {
    if (this is MutableMapOperators) {
        return mergeOperator(key, value) { k, v -> remapping(k, v) }
    }

    return internalMergeImpl(key, value, remapping)
}


/**
 * 由平台实现的 [MutableMap] `compute` 操作。
 *
 * 提供 [key] 并从 `map` 中通过 [remapping] 进行计算。
 * 其中 [remapping] 的 [K] 为 [key]，[V] 为 `map` 中已经存在的与 [key] 匹配的值，如果没有则为 `null`。
 * 当 [remapping] 的计算结果不为 `null` 时，插入此值并返回，否则删除原有的值（如果有的话）并返回 `null`。
 *
 * 无法保证原子操作。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValue(key: K, crossinline remapping: (K, V?) -> V?): V? {
    if (this is MutableMapOperators) {
        return computeOperator(key) { k, v -> remapping(k, v) }
    }

    return internalComputeImpl(key, remapping)
}

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果没有与之匹配的值，
 * 则通过 [remapping] 计算并存入后返回此计算值，否则直接返回得到的匹配值。
 *
 * 无法保证原子操作。
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, crossinline remapping: (K) -> V): V {
    if (this is MutableMapOperators) {
        return computeIfAbsentOperator(key) { k -> remapping(k) }
    }

    return internalComputeIfAbsentImpl(key, remapping)
}

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果有与之匹配的值，
 * 则通过 [mappingFunction] 计算并存入后返回此计算值，否则直接返回 `null`。
 * 如果 [mappingFunction] 的计算结果为 `null`，则会移除原本的值后返回 `null`。
 *
 * 无法保证原子操作。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfPresent(
    key: K,
    crossinline mappingFunction: (K, V & Any) -> V?
): V? {
    if (this is MutableMapOperators) {
        return computeIfPresentOperator(key) { k, v -> mappingFunction(k, v) }
    }

    return internalComputeIfPresentImpl(key, mappingFunction)
}

/**
 * 得到一个基于 [AtomicReference] 的 CopyOnWrite Map 实现。
 * 此 Map 中的修改操作是弱一致性的。
 *
 * 此 Map 的 [entries][MutableMap.entries]、[keys][MutableMap.keys]、[values][MutableMap.values] 都是瞬时副本，
 * 对它们（以及它们的元素）进行修改不会对 Map 本体产生影响。
 *
 * 此 Map 在进行同时大量的修改且元素数量比较多时可能会有较高的损耗。
 *
 * 此 Map 支持 [mergeValue]、[computeValue]、[computeValueIfAbsent]、[computeValueIfPresent]
 *
 */
public actual fun <K, V> concurrentMutableMap(): MutableMap<K, V> =
    AtomicCopyOnWriteConcurrentMutableMap(emptyMap())

@PublishedApi
internal interface MutableMapOperators {
    fun <K, V> MutableMap<K, V>.mergeOperator(
        key: K,
        value: V & Any,
        remapping: (V & Any, V & Any) -> V?
    ): V?

    fun <K, V> MutableMap<K, V>.computeOperator(key: K, remapping: (K, V?) -> V?): V?

    fun <K, V> MutableMap<K, V>.computeIfAbsentOperator(key: K, remapping: (K) -> V): V

    fun <K, V> MutableMap<K, V>.computeIfPresentOperator(
        key: K,
        mappingFunction: (K, V & Any) -> V?
    ): V?
}

private class AtomicCopyOnWriteConcurrentMutableMap<K, V>(initMap: Map<K, V>) : MutableMap<K, V>, MutableMapOperators {
    private val mapRef = AtomicReference(initMap.toMap())

    private inline val mapValue get() = mapRef.value

    @OptIn(ExperimentalContracts::class)
    private inline fun compareAndSetMap(block: (oldValue: Map<K, V>) -> Map<K, V>) {
        contract {
            callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
        }
        do {
            val oldValue = mapValue
            val newValue = block(oldValue)
        } while (!mapRef.compareAndSet(oldValue, newValue))
    }

    override val size: Int
        get() = mapValue.size

    override fun containsKey(key: K): Boolean =
        mapValue.containsKey(key)

    override fun containsValue(value: V): Boolean =
        mapValue.containsValue(value)

    override fun get(key: K): V? = mapValue[key]

    override fun isEmpty(): Boolean =
        mapValue.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = mapValue.entries.mapTo(mutableSetOf()) { e ->
            val value = AtomicReference(e.value)
            val key = e.key
            object : MutableMap.MutableEntry<K, V> {
                override val key: K
                    get() = key
                override val value: V
                    get() = value.value

                override fun setValue(newValue: V): V {
                    var oldValue: V
                    do {
                        oldValue = value.value
                    } while (value.compareAndSet(oldValue, newValue))

                    return oldValue
                }
            }
        }

    override val keys: MutableSet<K>
        get() = mapValue.keys.toMutableSet()

    override val values: MutableCollection<V>
        get() = mapValue.values.toMutableList()

    override fun clear() {
        compareAndSetMap { emptyMap() }
    }

    override fun put(key: K, value: V): V? {
        var result: V?
        compareAndSetMap { oldValue ->
            oldValue.toMutableMap().apply {
                result = put(key, value)
            }
        }
        return result
    }

    override fun putAll(from: Map<out K, V>) {
        compareAndSetMap { oldValue ->
            oldValue.toMutableMap().apply {
                putAll(from)
            }
        }
    }

    override fun remove(key: K): V? {
        var result: V?
        compareAndSetMap { oldValue ->
            oldValue.toMutableMap().apply {
                result = remove(key)
            }
        }

        return result
    }

    override fun <K, V> MutableMap<K, V>.mergeOperator(
        key: K,
        value: V & Any,
        remapping: (V & Any, V & Any) -> V?
    ): V? {
        var result: V?
        compareAndSetMap { oldMap ->
            oldMap.toMutableMap().apply {
                result = internalMergeImpl(key, value, remapping)
            }
        }

        return result
    }

    override fun <K, V> MutableMap<K, V>.computeOperator(key: K, remapping: (K, V?) -> V?): V? {
        var result: V?
        compareAndSetMap { oldMap ->
            oldMap.toMutableMap().apply {
                result = internalComputeImpl(key, remapping)
            }
        }

        return result
    }

    override fun <K, V> MutableMap<K, V>.computeIfAbsentOperator(key: K, remapping: (K) -> V): V {
        var result: V
        compareAndSetMap { oldMap ->
            oldMap.toMutableMap().apply {
                result = internalComputeIfAbsentImpl(key, remapping)
            }
        }

        return result
    }

    override fun <K, V> MutableMap<K, V>.computeIfPresentOperator(
        key: K,
        mappingFunction: (K, V & Any) -> V?
    ): V? {
        var result: V?
        compareAndSetMap { oldMap ->
            oldMap.toMutableMap().apply {
                result = internalComputeIfPresentImpl(key, mappingFunction)
            }
        }

        return result
    }

}
