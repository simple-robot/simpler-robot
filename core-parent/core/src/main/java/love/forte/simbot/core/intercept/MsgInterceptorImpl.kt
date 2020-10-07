/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MsgInterceptorImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.intercept

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.listener.*


/**
 * 消息拦截器的[上下文][MsgInterceptContext]实例。
 */
public data class MsgInterceptContextImpl(override var msgGet: MsgGet) : MsgInterceptContext

/**
 * [MsgInterceptContextFactory] 实现，以 [MsgInterceptContextImpl] 作为返回类型。
 */
public object MsgInterceptContextFactoryImpl : MsgInterceptContextFactory {
    override fun getMsgInterceptContext(msg: MsgGet) = MsgInterceptContextImpl(msg)
}



/**
 * 消息拦截器。
 */
public class MsgInterceptorImpl : MsgInterceptor {
    override fun chainedIntercept(context: MsgInterceptContext, chain: MsgInterceptChain) {
        TODO("Not yet implemented")
    }
}


/**
 * 消息拦截链的最终环节，即ListenerInterceptorChain
 */
public class EndOfMsgInterceptChain(
    private val listenerInterceptorChain: ListenerInterceptorChain,
    private val listenerContextFactory: (MsgInterceptContext) -> ListenerContext,
) : MsgInterceptChain {

    /**
     * 执行 listener 拦截器链并返回结果。
     */
    override fun pass(context: MsgInterceptContext): ListenResult<*>? {
        TODO()
    }
}



