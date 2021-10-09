@file:JvmSynthetic
package love.forte.simbot.api.utils

import java.util.concurrent.ConcurrentHashMap

/**
 * Map merge.
 *
 */
public actual fun <K, V : Any> MutableMap<K, V>.merge(key: K, value: V, reMapping: (V, V) -> V): V? {
    return this.merge(key, value, reMapping)
}

/**
 * Map compute.
 *
 */
public actual fun <K, V : Any> MutableMap<K, V>.doCompute(key: K, reMapping: (K, V?) -> V): V {
    return this.compute(key, reMapping)!!
}

/**
 * Map compute if absent.
 */
public actual fun <K, V : Any> MutableMap<K, V>.computeIfAbsent(key: K, mapping: (K) -> V): V {
    return this.computeIfAbsent(key, mapping)
}

/**
 * Map compute if present.
 */
public actual fun <K, V : Any> MutableMap<K, V>.computeIfPresent(
    key: K,
    reMapping: (K, V) -> V?,
): V? {
    return this.computeIfPresent(key, reMapping)
}

/**
 * Create a concurrent map.
 */
public actual fun <K, V> concurrentMap(): MutableMap<K, V> = ConcurrentHashMap()