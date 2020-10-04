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
import love.forte.simbot.core.intercept.Context
import love.forte.simbot.core.intercept.Interceptor


/**
 *
 * 消息拦截器，当接收到一个 [love.forte.simbot.core.api.message.MsgGet] 事件的时候
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface MsgIntercept : Interceptor<MsgGet, MsgInterceptContext>


/**
 *
 * [MsgIntercept] 中的消息主体，其中，[消息][MsgGet]可重新修改/变更，但是不可为null。
 *
 */
public interface MsgInterceptContext : Context<MsgGet> {

    /**
     * 消息主体。可重新赋值，但是不可为null。
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
