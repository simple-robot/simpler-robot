/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.utils

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference


/**
 *
 * Value值为 weak 的 Map。
 *
 * @author ForteScarlet
 */
public class WeakMap<K, V>(private val delegateMap: MutableMap<K, WeakReference<V>>) : MutableMap<K, V> {
    private val queue = ReferenceQueue<V>()
    private inner class WeakReferenceWithKey<K>(val key: K, value: V) : WeakReference<V>(value, queue)

    private fun checkWeak() {
        var polled: Reference<out V>? = queue.poll()
        while (polled != null) {
            if (polled is WeakMap<*, *>.WeakReferenceWithKey<*>) {
                remove(polled.key)
            }
            polled = queue.poll()
        }
    }



    override val size: Int get() = delegateMap.size
    override fun containsKey(key: K): Boolean = checkWeak().let { delegateMap.containsKey(key) }

    override fun containsValue(value: V): Boolean = checkWeak().let {
        delegateMap.values.any { it.get()?.equals(value) ?: false }
    }

    override fun get(key: K): V? {
        checkWeak()
        return delegateMap[key]?.get()
    }

    override fun isEmpty(): Boolean {
        checkWeak()
        return delegateMap.isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = checkWeak().let {
            delegateMap.entries.mapNotNull {
                val k = it.key
                val v = it.value.get() ?: return@mapNotNull null
                Entry(k, v) as MutableMap.MutableEntry<K, V>
            }.toMutableSet()
        }
    override val keys: MutableSet<K>
        get() = checkWeak().let { delegateMap.keys }

    override val values: MutableCollection<V>
        get() = checkWeak().let { delegateMap.values.mapNotNull { it.get() } }.toMutableList()

    override fun clear() {
        delegateMap.clear()
    }

    override fun put(key: K, value: V): V? {
        return delegateMap.put(key, WeakReferenceWithKey(key, value))?.get()
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (k, u) -> put(k, u) }
    }

    override fun remove(key: K): V? {
        return delegateMap.remove(key)?.get()
    }
}

private class Entry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V {
        val old = value
        value = newValue
        return old
    }
}
