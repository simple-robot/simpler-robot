/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     ListenerInvoker.kt
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


@file:JvmName("ListenerInvoker")

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
     */
    // Need FilterData
    val filter: ListenerFilter


    /**
     * 当前监听函数所前置的监听拦截器。
     */
    // Need ListenerInterceptContext
    val interceptor: ListenerInterceptor






}



public val ListenerInvoker.listenerId: String get() = function.id
public val ListenerInvoker.priority: Int get() = function.priority
public val ListenerInvoker.spare: Boolean get() = function.spare


