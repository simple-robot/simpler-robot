/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.core.intercept

import love.forte.simbot.intercept.InterceptionType
import love.forte.simbot.listener.ListenerInterceptContext
import love.forte.simbot.listener.ListenerInterceptor
import love.forte.simbot.utils.isEmpty


/**
 *
 * 基于监听函数分组的 [监听拦截器][ListenerInterceptor].
 *
 * @author ForteScarlet
 */
public abstract class GroupedListenerInterceptor : ListenerInterceptor {

    /**
     * 是否同时拦截没有分组的监听函数。
     */
    protected abstract val nonGroupAble: Boolean

    /**
     * 对于那些未命中的分组内容，其默认的放行策略。
     *
     * 可进行选择性重写，默认情况下，没有命中的监听函数将会被直接放行。
     *
     */
    protected open val missInterceptionType: InterceptionType get() = InterceptionType.PASS

    /**
     * 对于那些没有分组的监听函数，其默认的放行策略。
     *
     * 只有 [nonGroupAble] == false 的时候才会生效。
     *
     * 可进行选择性重写，默认情况下，没有命中的监听函数将会被直接放行。
     *
     */
    protected open val nonGroupInterceptionType: InterceptionType get() = InterceptionType.PASS


    /**
     * 对某个监听分组进行验证。
     */
    protected abstract fun groupCheck(groups: String): Boolean


    /**
     * 执行真正的拦截逻辑。
     */
    protected abstract fun doIntercept(context: ListenerInterceptContext): InterceptionType



    final override fun intercept(context: ListenerInterceptContext): InterceptionType {
        val functionGroups = context.listenerFunction.groups
        val nonGroupAble = nonGroupAble
        if (functionGroups.isEmpty()) {
            if (nonGroupAble) return doIntercept(context) else nonGroupInterceptionType
        }
        if (functionGroups.any(::groupCheck)) {
            return doIntercept(context)
        }

        return missInterceptionType
    }
}