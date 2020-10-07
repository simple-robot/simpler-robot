/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MsgIntercept.kt
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
 * 消息拦截器，当接收到一个 [love.forte.simbot.core.api.message.MsgGet] 事件的时候触发。
 *
 * 消息拦截器为[链式拦截器][ChainedInterceptor]。
 *
 * 消息拦截器是在触发监听之前拦截的，因此如果被成功拦截，则不会触发任何监听函数。
 *
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface MsgInterceptor : ChainedInterceptor<MsgGet, ListenResult<*>, MsgInterceptChain, MsgInterceptContext>


/**
 * [消息拦截器][MsgInterceptor] 中的 [链][MsgInterceptChain]
 */
public interface MsgInterceptChain : InterceptChain<MsgGet, MsgInterceptContext, ListenResult<*>> {
    override fun pass(context: MsgInterceptContext): ListenResult<*>?
}


/**
 *
 * [MsgInterceptor] 中的消息主体，其中，[消息][MsgGet]可重新修改/变更，但是不可为null。
 *
 */
public interface MsgInterceptContext : Context<MsgGet> {

    /**
     * 消息主体。可重新赋值，但是不可为null。
     * 此消息主体在[MsgInterceptChain.pass]之前执行即可变更当前监听事件所应触发的事件。
     */
    var msgGet: MsgGet

    /** same as [msgGet] */
    @JvmDefault
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
    fun getMsgInterceptContext(msg: MsgGet): MsgInterceptContext
}

