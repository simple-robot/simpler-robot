/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.common.collection


/**
 * 由平台实现的 [MutableMap] `merge` 操作。
 *
 * 提供 [key] 和 [value]，如果 `map` 中不存在 [key] 对应的值，则存入此键值对。
 * 如果存在与 [key] 冲突的记录，通过 [remapping] 函数计算新值。
 * 当计算新值不为 `null` 时存入新值，否则移除旧值。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.mergeValue(
    key: K,
    value: V & Any,
    remapping: (V & Any, V & Any) -> V?
): V? =
    internalMergeImpl(key, value, remapping)

/**
 * 由平台实现的 [MutableMap] `compute` 操作。
 *
 * 提供 [key] 并从 `map` 中通过 [remapping] 进行计算。
 * 其中 [remapping] 的 [K] 为 [key]，[V] 为 `map` 中已经存在的与 [key] 匹配的值，如果没有则为 `null`。
 * 当 [remapping] 的计算结果不为 `null` 时，插入此值并返回，否则删除原有的值（如果有的话）并返回 `null`。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValue(key: K, remapping: (K, V?) -> V?): V? =
    internalComputeImpl(key, remapping)

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果没有与之匹配的值，
 * 则通过 [remapping] 计算并存入后返回此计算值，否则直接返回得到的匹配值。
 *
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, remapping: (K) -> V): V =
    internalComputeIfAbsentImpl(key, remapping)

/**
 * 由平台实现的 [MutableMap] `computeIfPresent` 操作。
 *
 * 提供 [key] 从 `map` 中检索匹配的值，如果有与之匹配的值，
 * 则通过 [mappingFunction] 计算并存入后返回此计算值，否则直接返回 `null`。
 * 如果 [mappingFunction] 的计算结果为 `null`，则会移除原本的值后返回 `null`。
 */
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfPresent(
    key: K,
    mappingFunction: (K, V & Any) -> V?
): V? = internalComputeIfPresentImpl(key, mappingFunction)

/**
 * 根据 [key] 删除指定的目标 [target]。
 */
public actual inline fun <K, V> MutableMap<K, V>.removeValue(
    key: K,
    crossinline target: () -> V
): Boolean = internalRemoveValueImpl(key, target)

/**
 * 通过 [mutableMapOf] 得到一个允许并发修改的 [MutableMap]，
 */
public actual fun <K, V> concurrentMutableMap(): MutableMap<K, V> =
    JsConcurrentModifyMutableMap(mutableMapOf()) // mutableMapOf()

private class JsConcurrentModifyMutableMap<K, V>(private val source: MutableMap<K, V>) : MutableMap<K, V> by source {
    override val keys: MutableSet<K>
        get() = MutableKeySet(source, source.keys.toSet())

    private class MutableKeySet<K>(private val source: MutableMap<K, *>, private val view: Set<K>) :
        MutableSet<K>,
        Set<K> by view {
        override fun add(element: K): Boolean = throw UnsupportedOperationException()
        override fun addAll(elements: Collection<K>): Boolean = throw UnsupportedOperationException()
        override fun clear() {
            source.clear()
        }

        override fun remove(element: K): Boolean {
            return source.remove(element) != null
        }

        @Suppress("ConvertArgumentToSet")
        override fun removeAll(elements: Collection<K>): Boolean {
            return source.keys.removeAll(elements)
        }

        @Suppress("ConvertArgumentToSet")
        override fun retainAll(elements: Collection<K>): Boolean {
            return source.keys.retainAll(elements)
        }

        override fun iterator(): MutableIterator<K> =
            MutableMapIterator(view = view.iterator(), mapper = { it }, remover = source::remove)
    }


    override val values: MutableCollection<V>
        get() = MutableValueCollection(source, source.values.toList())

    private class MutableValueCollection<V>(private val source: MutableMap<*, V>, private val view: List<V>) :
        MutableCollection<V>, Collection<V> by view {
        override fun add(element: V): Boolean = throw UnsupportedOperationException()
        override fun addAll(elements: Collection<V>): Boolean = throw UnsupportedOperationException()

        override fun clear() {
            source.clear()
        }

        override fun remove(element: V): Boolean {
            return source.values.remove(element)
        }

        @Suppress("ConvertArgumentToSet")
        override fun removeAll(elements: Collection<V>): Boolean {
            return source.values.removeAll(elements)
        }

        @Suppress("ConvertArgumentToSet")
        override fun retainAll(elements: Collection<V>): Boolean {
            return source.values.retainAll(elements)
        }

        override fun iterator(): MutableIterator<V> =
            MutableMapIterator(view = view.iterator(), mapper = { it }, remover = source.values::remove)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = MutableEntrySet(source, source.toMap().entries.toSet())

    private class MutableEntrySet<K, V>(
        private val source: MutableMap<K, V>,
        private val view: Set<Map.Entry<K, V>>
    ) : MutableSet<MutableMap.MutableEntry<K, V>> {
        override val size: Int
            get() = view.size

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean =
            source[element.key] == element.value

        override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            all { contains(it) }

        override fun isEmpty(): Boolean = view.isEmpty()

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
            val (k, v) = element
            if (source.containsKey(k)) return false
            source[k] = v
            return true
        }

        override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
            var added = false
            for (e in elements) {
                if (add(e) && !added) {
                    added = true
                }
            }

            return added
        }

        override fun clear() {
            source.clear()
        }

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
            return source.remove(element.key) != null
        }

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
            var removed = false
            for (e in elements) {
                if (remove(e) && !removed) {
                    removed = true
                }
            }

            return removed
        }

        override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
            return source.keys.retainAll(elements.mapTo(mutableSetOf()) { it.key })
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = MutableMapIterator(
            view.iterator(),
            mapper = { (oldKey, oldValue) ->

                object : MutableMap.MutableEntry<K, V> {
                    override val key: K = oldKey
                    override var value: V = oldValue

                    override fun setValue(newValue: V): V {
                        val oldValue1 = value
                        return source.put(key, newValue).also { value = newValue }
                            ?: oldValue1
                    }
                }
            },
            remover = { target ->
                source.remove(target.key)
            }
        )
    }

    private class MutableMapIterator<T, R>(
        private val view: Iterator<T>,
        private val mapper: (T) -> R,
        private val remover: (T) -> Unit
    ) : MutableIterator<R> {
        @Suppress("ClassName")
        private object NO_VALUE

        private var currentValue: Any? = NO_VALUE
        override fun hasNext(): Boolean = view.hasNext()
        override fun next(): R = view.next().also { currentValue = it }.let(mapper)

        @Suppress("UNCHECKED_CAST")
        override fun remove() {
            val value = currentValue
            if (value is NO_VALUE) {
                throw NoSuchElementException()
            }
            remover(value as T)
        }
    }
}
