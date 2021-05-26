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
 * 消息拦截器，当接收到一个 [love.forte.simbot.core.api.message.MsgGet] 事件的时候触发。
 *
 * 消息拦截器是在触发监听之前拦截的，因此如果被成功拦截，则不会触发任何监听函数。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface MsgInterceptor : Interceptor<MsgGet, MsgInterceptContext>


/**
 * [消息拦截器][MsgInterceptor] 的链，判断是否被拦截，可通过[工厂][MsgInterceptChainFactory]获取。
 */
public interface MsgInterceptChain {
    fun intercept(): InterceptionType
}


/**
 *
 * [MsgInterceptor] 中的消息主体，其中，[消息][MsgGet]可重新修改/变更，但是不可为null。
 *
 */
public interface MsgInterceptContext : Context<MsgGet> {

    /**
     * 消息主体。可重新赋值，但是不可为null。
     */
    var msgGet: MsgGet

    /**
     * 监听上下文。
     */
    val listenerContext: ListenerContext

    /** same as [msgGet] */
    // @JvmDefault
    override var mainValue: MsgGet
        get() = msgGet
        set(value) {
            msgGet = value
        }
}

/**
 * [MsgInterceptContext] 工厂。
 */
public interface MsgInterceptContextFactory {
    /** 通过一个 [MsgGet] 构建一个 [MsgInterceptContext] 实例. */
    fun getMsgInterceptContext(msg: MsgGet, listenerContext: ListenerContext): MsgInterceptContext
}

