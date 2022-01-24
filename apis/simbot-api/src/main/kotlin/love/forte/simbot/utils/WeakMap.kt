/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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

    init {
    }


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