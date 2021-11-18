/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.utils


/**
 * Map merge.
 *
 */
public actual fun <K, V : Any> MutableMap<K, V>.doMerge(key: K, value: V, reMapping: (V, V) -> V): V? {
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
public actual fun <K, V : Any> MutableMap<K, V>.doComputeIfPresent(
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