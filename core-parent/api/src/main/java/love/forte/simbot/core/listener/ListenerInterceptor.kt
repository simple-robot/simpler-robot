/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerInterceptor.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.intercept.ChainedInterceptor
import love.forte.simbot.core.intercept.Context
import love.forte.simbot.core.intercept.InterceptChain


/**
 *
 * 监听函数拦截器，每一个监听函数之前都会被执行。
 *
 * 监听函数拦截器为链式拦截器
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ListenerInterceptor :
    ChainedInterceptor<ListenerFunction, ListenResult<*>, ListenerInterceptorChain, ListenerInterceptContext>


/**
 * [监听函数拦截器][ListenerInterceptor] 的 [InterceptChain] 实例。
 */
public interface ListenerInterceptorChain : InterceptChain<ListenerFunction, ListenResult<*>>



/**
 * [ListenerInterceptor] 的拦截信息内容。拦截主体为 [listenerFunction].
 *
 */
public interface ListenerInterceptContext : Context<ListenerFunction> {

    /** 被拦截的监听函数 */
    val listenerFunction: ListenerFunction

    /** 触发监听的消息内容 */
    val msgGet: MsgGet

    /** 当前监听上下文 */
    val listenerContext: ListenerContext

    @JvmDefault
    override val mainValue: ListenerFunction get() = listenerFunction
}


