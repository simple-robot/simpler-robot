package love.forte.simbot

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function


/**
 * 以ID为Key的 [Map]。
 */
public interface IDMaps<V> : Map<ID, V>

/**
 * 以ID为Key的 [MutableMap]。
 */
public interface MutableIDMaps<V> : IDMaps<V>, MutableMap<ID, V>

/**
 * 以 ID 为key的 [ConcurrentMap]。
 */
public interface ConcurrentIDMaps<V> : MutableIDMaps<V>, ConcurrentMap<ID, V>


@Suppress("UNCHECKED_CAST")
public fun <V> emptyIDMap(): IDMaps<V> = EmptyIDMap as IDMaps<V>

public fun <V> idMapOf(): IDMaps<V> = emptyIDMap()

public fun <V> idMapOf(vararg pairs: Pair<ID, V>): IDMaps<V> =
    when {
        pairs.isEmpty() -> emptyIDMap()
        pairs.size == 1 -> SignalPairIDMap(pairs[0].first, pairs[0].second)
        else -> CharSequenceIDMap(mutableMapOf(*pairs))
    }

public fun <V> idMapOf(map: Map<ID, V>): IDMaps<V> =
    when {
        map.isEmpty() -> emptyIDMap()
        map.size == 1 -> map.entries.first().let { SignalPairIDMap(it.key, it.value) }
        else -> CharSequenceIDMap(map.toMutableMap())
    }


public fun <V> mutableIDMapOf(): MutableIDMaps<V> = CharSequenceIDMap(mutableMapOf())

public fun <V> mutableIDMapOf(vararg pairs: Pair<ID, V>): MutableIDMaps<V> = CharSequenceIDMap(mutableMapOf(*pairs))

public fun <V> mutableIDMapOf(map: Map<ID, V>): MutableIDMaps<V> = CharSequenceIDMap(map.toMutableMap())


/**
 * [V] 不可为null
 */
public fun <V : Any> concurrentIDMapOf(): ConcurrentIDMaps<V> = CharSequenceConcurrentIDMap(ConcurrentHashMap())

/**
 * [V] 不可为null
 */
public fun <V : Any> concurrentIDMapOf(vararg pairs: Pair<ID, V>): ConcurrentIDMaps<V> =
    CharSequenceConcurrentIDMap(ConcurrentHashMap<ID, V>(pairs.size).apply { putAll(pairs) })
/**
 * [V] 不可为null
 */
public fun <V : Any> concurrentIDMapOf(map: Map<ID, V>): ConcurrentIDMaps<V> =
    CharSequenceConcurrentIDMap(ConcurrentHashMap<ID, V>(map.size).apply { putAll(map) })


private object EmptyIDMap : IDMaps<Any?> {
    override val entries: Set<Map.Entry<ID, Any?>> get() = emptySet()
    override val keys: Set<ID> get() = emptySet()
    override val size: Int get() = 0
    override val values: Collection<Any?> get() = emptySet()
    override fun containsKey(key: ID): Boolean = false
    override fun containsValue(value: Any?): Boolean = false
    override fun get(key: ID): Any? = null
    override fun isEmpty(): Boolean = true
}

private class SignalPairIDMap<V>(val id: ID, val value: V) : IDMaps<V> {
    private inner class Entry : Map.Entry<ID, V> {
        override val key: ID get() = id
        override val value: V get() = this@SignalPairIDMap.value
    }

    private val entry = Entry()
    override val entries: Set<Map.Entry<ID, V>> = setOf(entry)
    override val keys: Set<ID> = setOf(id)
    override val size: Int get() = 1
    override val values: Collection<V> = listOf(value)
    override fun containsKey(key: ID): Boolean = key == id
    override fun containsValue(value: V): Boolean = value == this.value
    override fun get(key: ID): V? = value.takeIf { containsKey(key) }
    override fun isEmpty(): Boolean = false
}


private class CharSequenceIDMap<V>(private val delegate: MutableMap<ID, V>) : MutableIDMaps<V> {
    override val entries: MutableSet<MutableMap.MutableEntry<ID, V>>
        get() = delegate.entries.toMutableSet()

    override val keys: MutableSet<ID>
        get() = delegate.keys.toMutableSet()

    override val size: Int
        get() = delegate.size

    override val values: MutableCollection<V>
        get() = delegate.values

    override fun containsKey(key: ID): Boolean {
        return delegate.containsKey(key.toCharSequenceID())
    }

    override fun containsValue(value: V): Boolean {
        return delegate.containsValue(value)
    }

    override fun get(key: ID): V? {
        return delegate[key.toCharSequenceID()]
    }

    override fun isEmpty(): Boolean {
        return delegate.isEmpty()
    }

    override fun clear() {
        delegate.clear()
    }

    override fun put(key: ID, value: V): V? {
        return delegate.put(key.toCharSequenceID(), value)
    }

    override fun putAll(from: Map<out ID, V>) {
        delegate.putAll(from.mapKeys { it.key.toCharSequenceID() })
    }

    override fun remove(key: ID): V? {
        return delegate.remove(key.toCharSequenceID())
    }

    override fun getOrDefault(key: ID, defaultValue: V): V {
        return delegate.getOrDefault(key, defaultValue)
    }

    override fun remove(key: ID, value: V): Boolean {
        return delegate.remove(key, value)
    }

    override fun forEach(action: BiConsumer<in ID, in V>) {
        delegate.forEach(action)
    }

    override fun compute(key: ID, remappingFunction: BiFunction<in ID, in V?, out V?>): V? {
        return delegate.compute(key, remappingFunction)
    }

    override fun computeIfAbsent(key: ID, mappingFunction: Function<in ID, out V>): V {
        return delegate.computeIfAbsent(key, mappingFunction)
    }

    override fun computeIfPresent(key: ID, remappingFunction: BiFunction<in ID, in V, out V?>): V? {
        return delegate.computeIfPresent(key, remappingFunction)
    }

    override fun putIfAbsent(key: ID, value: V): V? {
        return delegate.putIfAbsent(key, value)
    }

    override fun replace(key: ID, oldValue: V, newValue: V): Boolean {
        return delegate.replace(key, oldValue, newValue)
    }

    override fun replace(key: ID, value: V): V? {
        return delegate.replace(key, value)
    }

    override fun replaceAll(function: BiFunction<in ID, in V, out V>) {
        delegate.replaceAll(function)
    }

    override fun toString(): String {
        return delegate.toString()
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is CharSequenceIDMap<*>) {
            return delegate == other.delegate
        }
        return delegate == other
    }
}

private class CharSequenceConcurrentIDMap<V : Any>(private val delegate: ConcurrentMap<ID, V>) : ConcurrentIDMaps<V> {
    override val entries: MutableSet<MutableMap.MutableEntry<ID, V>>
        get() = delegate.entries.toMutableSet()

    override val keys: MutableSet<ID>
        get() = delegate.keys.toMutableSet()

    override val size: Int
        get() = delegate.size

    override val values: MutableCollection<V>
        get() = delegate.values

    override fun containsKey(key: ID): Boolean {
        return delegate.containsKey(key.toCharSequenceID())
    }

    override fun containsValue(value: V): Boolean {
        return delegate.containsValue(value)
    }

    override fun get(key: ID): V? {
        return delegate[key.toCharSequenceID()]
    }

    override fun isEmpty(): Boolean {
        return delegate.isEmpty()
    }

    override fun clear() {
        delegate.clear()
    }

    override fun put(key: ID, value: V): V? {
        return delegate.put(key.toCharSequenceID(), value)
    }

    override fun putAll(from: Map<out ID, V>) {
        delegate.putAll(from.mapKeys { it.key.toCharSequenceID() })
    }

    override fun remove(key: ID): V? {
        return delegate.remove(key.toCharSequenceID())
    }

    override fun remove(key: ID, value: V): Boolean {
        return delegate.remove(key.toCharSequenceID(), value)
    }

    override fun putIfAbsent(key: ID, value: V): V? {
        return delegate.putIfAbsent(key.toCharSequenceID(), value)
    }

    override fun replace(key: ID, oldValue: V, newValue: V): Boolean {
        return delegate.replace(key.toCharSequenceID(), oldValue, newValue)
    }

    override fun replace(key: ID, value: V): V? {
        return delegate.replace(key.toCharSequenceID(), value)
    }

    override fun merge(key: ID, value: V, remappingFunction: BiFunction<in V, in V, out V?>): V? {
        return delegate.merge(key.toCharSequenceID(), value, remappingFunction)
    }

    override fun compute(key: ID, remappingFunction: BiFunction<in ID, in V?, out V?>): V? {
        return delegate.compute(key, remappingFunction)
    }

    override fun forEach(action: BiConsumer<in ID, in V>) {
        delegate.forEach(action)
    }

    override fun getOrDefault(key: ID, defaultValue: V): V {
        return delegate.getOrDefault(key, defaultValue)
    }

    override fun computeIfAbsent(key: ID, mappingFunction: Function<in ID, out V>): V {
        return delegate.computeIfAbsent(key, mappingFunction)
    }

    override fun computeIfPresent(key: ID, remappingFunction: BiFunction<in ID, in V, out V?>): V? {
        return delegate.computeIfPresent(key, remappingFunction)
    }

    override fun replaceAll(function: BiFunction<in ID, in V, out V>) {
        delegate.replaceAll(function)
    }


    override fun toString(): String {
        return delegate.toString()
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is CharSequenceConcurrentIDMap<*>) {
            return delegate == other.delegate
        }
        return delegate == other
    }
}
