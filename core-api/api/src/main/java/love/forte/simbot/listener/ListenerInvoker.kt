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


@file:JvmName("ListenerInvokers")
package love.forte.simbot.listener

import love.forte.simbot.filter.ListenerFilter


/**
 *
 * 监听函数执行器。其作为监听函数流程的完整包装，
 * 是完成 `监听函数拦截器` -> `过滤器` -> `监听函数` 流程的包装体。
 *
 * 每一个 ListenerInvoker 即对应一个监听函数执行流程。
 * // TODO 还需要考虑
 * @author ForteScarlet
 */
public interface ListenerInvoker {

    /**
     * 当前执行器所对应的监听函数。
     */
    val function: ListenerFunction

    /**
     * 当前监听函数所对应的监听过滤器。
     * 正常情况下，就是 [ListenerFunction.filter].
     */
    val filter: ListenerFilter? get() = function.filter


    /**
     * 进行过滤匹配，然后执行监听函数。
     */
    suspend operator fun invoke(data: ListenerFunctionInvokeData): ListenResult<*>

}





