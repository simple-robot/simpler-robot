/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LazySingleCache.kt
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

import love.forte.simbot.AtomicRef
import java.util.concurrent.TimeUnit


/**
 *  线程安全缓存器。
 *  TODO test
 */
public class AtomicRefreshSingleCache<T>
constructor(
    aging: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    activeInitialization: Boolean = false,
    private val compute: () -> T,
) : BaseAutoRefreshSingleCache<T>(aging, timeUnit) {

    @Volatile
    private var value: AtomicRef<T> = if (activeInitialization) AtomicRef(compute()) else AtomicRef()

    /**
     * 获取缓存值，并决定是否[刷新缓存时间][refresh]. 如果获取不到则刷新值再获取。
     */
    override fun get(refresh: Boolean): T {
        return value.updateAndGet {
            if (isExpired()) {
                nextTime()
                compute()
            } else it
        }
    }

}

