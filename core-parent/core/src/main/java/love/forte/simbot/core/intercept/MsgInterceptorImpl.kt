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

import love.forte.common.ioc.DependBeanFactory
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
 * 空的 [MsgInterceptChain] 实现，总是放行的。
 */
public object EmptyMsgInterceptChain : MsgInterceptChain {
    override fun intercept(): InterceptionType = InterceptionType.PASS
}


/**
 *  [MsgInterceptChain] 实现，遍历拦截器列表并尝试寻找第一个拦截点。
 */
public class MsgInterceptChainImpl(
    private val interceptorList: List<MsgInterceptor>,
    private val context: MsgInterceptContext
) : MsgInterceptChain {
    override fun intercept(): InterceptionType {
        return InterceptionType.getTypeByPrevent(
            interceptorList.any {
                it.intercept(context).isPrevent
            }
        )
    }
}


/**
 * [MsgInterceptChain] 工厂，
 * 通过 [DependBeanFactory] 懒加载获取所有的 [MsgInterceptor] 实例并缓存以构建一个 [MsgInterceptChain] 实例。
 */
public class MsgInterceptChainFactoryImpl(
    private val dependBeanFactory: DependBeanFactory
) : MsgInterceptChainFactory {

    /**
     * 懒加载依赖中心中的所有拦截器实例。
     */
    private val interceptorList: List<MsgInterceptor> by lazy {
        dependBeanFactory.getListByType(MsgInterceptor::class.java).toList()
    }


    /**
     * 根据一个消息主体得到一个消息拦截器链实例。
     */
    override fun getInterceptorChain(context: MsgInterceptContext): MsgInterceptChain {
        return with(interceptorList) {
            if(isEmpty()) EmptyMsgInterceptChain
            else MsgInterceptChainImpl(this, context)
        }
    }
}



