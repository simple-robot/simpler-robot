/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.listener

import love.forte.simbot.Context
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.intercept.InterceptionType
import love.forte.simbot.intercept.Interceptor


/**
 *
 * 监听函数拦截器，每一个监听函数之前都会被执行。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ListenerInterceptor : Interceptor<ListenerFunction, ListenerInterceptContext>



/**
 * [监听函数拦截器][ListenerInterceptor]链，用于判断是否放行。
 */
public interface ListenerInterceptorChain {
    /**
     * 检测拦截状态。
     */
    fun intercept(): InterceptionType
}








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
    override val mainValue: ListenerFunction
        get() = listenerFunction
}


/**
 * [ListenerInterceptContext] 构建工厂。
 */
public interface ListenerInterceptContextFactory {

    /**
     * 通过提供的参数构建一个 [ListenerInterceptContext] 实例。
     */
    fun getListenerInterceptContext(
        listenerFunction: ListenerFunction,
        msgGet: MsgGet,
        listenerContext: ListenerContext
    ): ListenerInterceptContext
}

