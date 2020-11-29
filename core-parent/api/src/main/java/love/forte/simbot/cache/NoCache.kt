/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     NoCache.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */
@file:JvmName("Caches")
@file:JvmMultifileClass
package love.forte.simbot.cache

import java.util.concurrent.TimeUnit


/**
 * 使用 [NoCache] 作为 [Cache] 实例。
 */
@Suppress("UNCHECKED_CAST")
public fun <K, V> noCache(): Cache<K, V> = NoCache as Cache<K, V>

/**
 * [Cache] 的无效化实现。
 *
 * @see noCache
 */
private object NoCache : BaseCache<Any, Any>(1, TimeUnit.MILLISECONDS) {
    /** 永远获取不到缓存。 */
    override fun get(key: Any, refreshLife: Boolean): Nothing? = null

    /**
     * 设置无效。
     */
    override fun set(key: Any, value: Any) {}


    /**
     * 获取一个缓存，如果缓存不存在则[计算][computeFunction]并存入缓存。
     */
    override fun compute(key: Any, refreshLife: Boolean, computeFunction: (Any) -> Any): Any {
        return computeFunction(key)
    }
}










