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

@file:kotlin.jvm.JvmSynthetic
package love.forte.simbot.utils

/**
 * Map merge.
 * 当存入时，[key] 对应的元素已经存在，则通过 [reMapping] 进行重新计算。
 *
 */
public expect fun <K, V : Any> MutableMap<K, V>.doMerge(key: K, value: V, reMapping: (V, V) -> V): V?


/**
 * Map compute.
 * 通过 [reMapping] 提供 key 和 旧值（可能为null）来计算一个新值。
 *
 */
public expect fun <K, V : Any> MutableMap<K, V>.doCompute(key: K, reMapping: (K, V?) -> V): V


/**
 * Map compute if absent.
 * 如果没有旧值，则计算一个新值，否则不变。
 * @return 得到的值
 */
public expect fun <K, V : Any> MutableMap<K, V>.computeIfAbsent(key: K, mapping: (K) -> V): V


/**
 * Map compute if present.
 * 如果有旧值，计算并存入新值。如果得到的新值为null，移除旧值。
 */
public expect fun <K, V : Any> MutableMap<K, V>.doComputeIfPresent(key: K, reMapping: (K, V) -> V?): V?


/**
 * Create a concurrent map.
 * 得到一个能够并发的map。
 */
public expect fun <K, V> concurrentMap(): MutableMap<K, V>

