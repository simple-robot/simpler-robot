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

package love.forte.simbot.common.api.intercept

import love.forte.simbot.common.constant.PriorityConstant


/**
 *
 * 定义一个拦截器接口
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface Interceptor<T, C: _root_ide_package_.love.forte.simbot.common.api.intercept.Context<T>> : Comparable<Interceptor<*, *>> {

    /**
     * 执行拦截，当返回值为 `true` 的时候则会被放行。
     */
    fun intercept(context: C): Boolean


    /**
     * 得到优先级, 默认为 [PriorityConstant.CORE_TENTH]
     */
    @JvmDefault
    val priority: Int get() = PriorityConstant.CORE_LAST


    /**
     * 排序规则。正常情况下不要重写此方法。
     */
    @JvmDefault
    override fun compareTo(other: Interceptor<*, *>): Int {
        return this.priority.compareTo(other.priority)
    }


}