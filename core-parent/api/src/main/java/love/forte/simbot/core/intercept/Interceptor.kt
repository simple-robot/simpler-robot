/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Interceptor.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.intercept

import love.forte.simbot.core.constant.PriorityConstant


/**
 * 拦截器类型接口，提供一个拦截方法。
 */
public interface Interceptor<T, C: Context<T>> : Comparable<Interceptor<T, C>> {


    /**
     * 执行拦截。
     * @return 是否拦截成功。如果为 **true** 则代表被拦截。
     */
    fun intercept(context: C): Boolean


    /**
     * 排序值，默认即为最低值。一般情况下可以不用重写此方法。
     */
    @JvmDefault
    val priority: Int get() = PriorityConstant.LAST

    /**
     * 排序。
     */
    override fun compareTo(other: Interceptor<T, C>): Int = priority.compareTo(other.priority)

}
