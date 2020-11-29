/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Cache.kt
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

package love.forte.simbot.cache

import java.util.concurrent.TimeUnit


/**
 * [Cache] 接口定义一种简单的缓存接口标准。
 * 此接口存在一个固定大小的时间周期，例如 **7小时** 之类的，来定义一个键值对的有效期。
 * 在存入的时候不允许存入null值。
 */
public interface Cache<K, V> {

    /** 时效周期，毫秒值 */
    fun aging(): Long

    /**
     * 获取一个缓存，并决定获取的时候是否要 [刷新存活时间][refreshLife]
     */
    operator fun get(key: K, refreshLife: Boolean): V?


    /**
     * 设置一个缓存值。
     */
    operator fun set(key: K, value: V)


    /**
     * 获取一个缓存，如果缓存不存在则[计算][computeFunction]并存入缓存。
     */
    fun compute(key: K, refreshLife: Boolean, computeFunction: (K) -> V): V
}


/**
 * [Cache] 基础抽象类，实现 [aging].
 */
public abstract class BaseCache<K, V>(aging: Long, timeUnit: TimeUnit) : Cache<K, V> {
    private val aging = timeUnit.toMillis(aging)
    override fun aging(): Long = aging

    /**
     * 以当前时间为标准计算下一次过期时间。
     */
    protected fun nextLifeTime(): Long = System.currentTimeMillis() + aging()
}
