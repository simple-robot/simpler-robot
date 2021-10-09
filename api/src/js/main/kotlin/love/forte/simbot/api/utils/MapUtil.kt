package love.forte.simbot.api.utils


/**
 * Map merge.
 *
 */
public actual fun <K, V : Any> MutableMap<K, V>.merge(key: K, value: V, reMapping: (V, V) -> V): V? {
    val old = get(key)
    if (old == null) {
        put(key, value)
        return null
    }
    put(key, reMapping(old, value))
    return old
}

/**
 * Map compute.
 *
 */
public actual fun <K, V : Any> MutableMap<K, V>.doCompute(key: K, reMapping: (K, V?) -> V): V {
    val newValue = reMapping(key, get(key))
    return put(key, newValue) ?: newValue
}

/**
 * Map compute if absent.
 */
public actual fun <K, V : Any> MutableMap<K, V>.computeIfAbsent(key: K, mapping: (K) -> V): V {
    val old = get(key)
    if (old != null) {
        return old
    }

    val new = mapping(key)
    put(key, new)
    return new
}

/**
 * Map compute if present.
 */
public actual fun <K, V : Any> MutableMap<K, V>.computeIfPresent(
    key: K,
    reMapping: (K, V) -> V?,
): V? {
    val old = get(key)
    if (old != null) {
        val new = reMapping(key, old)
        return if (new == null) {
            remove(key)
        } else {
            put(key, new)
        }
    }

    return null

}

/**
 * Create a concurrent map.
 */
public actual fun <K, V> concurrentMap(): MutableMap<K, V> {
    return mutableMapOf()
}