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

@file:JvmSynthetic
package love.forte.simbot.utils

import java.util.concurrent.ConcurrentHashMap

/**
 * Map merge.
 *
 */
public actual fun <K, V : Any> MutableMap<K, V>.doMerge(key: K, value: V, reMapping: (V, V) -> V): V? {
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
public actual fun <K, V : Any> MutableMap<K, V>.doComputeIfPresent(
    key: K,
    reMapping: (K, V) -> V?,
): V? {
    return this.computeIfPresent(key, reMapping)
}

/**
 * Create a concurrent map.
 */
public actual fun <K, V> concurrentMap(): MutableMap<K, V> = ConcurrentHashMap()