/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     SingleCache.kt
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
 * 自刷新的单值缓存器, 仅缓存一个单值对象。
 */
public interface AutoRefreshSingleCache<T> {

    /** 时效周期，毫秒值 */
    fun aging(): Long

    /**
     * 获取缓存值，并决定是否[刷新缓存时间][refresh]. 如果获取不到则刷新值再获取。
     */
    fun get(refresh: Boolean): T
}



/**
 * [AutoRefreshSingleCache] 基础抽象类，实现[aging]
 */
public abstract class BaseAutoRefreshSingleCache<T>(aging: Long, timeUnit: TimeUnit) : AutoRefreshSingleCache<T> {
    private val aging = timeUnit.toMillis(aging)
    /** 本次到期时间。 */
    @Volatile
    protected var expireTime: Long = -1L


    override fun aging(): Long = aging


    /**
     * 以当前时间为标准计算下一次过期时间。
     */
    protected fun nextLifeTime(): Long = System.currentTimeMillis() + aging()


    /** 将时间刷新到下一个到期时间。 */
    protected fun nextTime(): Long {
        expireTime = nextLifeTime()
        return expireTime
    }

    /**
     * 判断是否已经到期。
     */
    protected fun isExpired(): Boolean = expireTime < System.currentTimeMillis()




}
