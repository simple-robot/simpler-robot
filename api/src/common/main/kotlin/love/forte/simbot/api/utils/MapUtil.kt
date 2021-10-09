@file:kotlin.jvm.JvmSynthetic
package love.forte.simbot.api.utils

/**
 * Map merge.
 *
 */
public expect fun <K, V : Any> MutableMap<K, V>.merge(key: K, value: V, reMapping: (V, V) -> V): V?


/**
 * Map compute.
 *
 */
public expect fun <K, V : Any> MutableMap<K, V>.compute(key: K, reMapping: (K, V?) -> V): V?


/**
 * Map compute if absent.
 */
public expect fun <K, V : Any> MutableMap<K, V>.computeIfAbsent(key: K, mapping: (K) -> V): V


/**
 * Map compute if present.
 */
public expect fun <K, V : Any> MutableMap<K, V>.computeIfPresent(key: K, reMapping: (K, V) -> V): V?


/**
 * Create a concurrent map.
 */
public expect fun <K, V> concurrentMap(): MutableMap<K, V>




