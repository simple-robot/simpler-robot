/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerInterceptorImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.intercept

import love.forte.common.ioc.DependBeanFactory
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.intercept.InterceptionType
import love.forte.simbot.listener.*


/**
 * 监听函数拦截器上下文内容。
 */
public data class ListenerInterceptContextImpl(
    override val listenerFunction: ListenerFunction,
    override val msgGet: MsgGet,
    override val listenerContext: ListenerContext
) : ListenerInterceptContext


/**
 * [ListenerInterceptContextFactory] 实现，以 [ListenerInterceptContextImpl] 作为返回类型。
 */
public object CoreListenerInterceptContextFactory : ListenerInterceptContextFactory {
    override fun getListenerInterceptContext(
        listenerFunction: ListenerFunction,
        msgGet: MsgGet,
        listenerContext: ListenerContext
    ) = ListenerInterceptContextImpl(listenerFunction, msgGet, listenerContext)
}


/**
 * 空的拦截链，总是放行的。
 */
public object EmptyListenerInterceptorChain : ListenerInterceptorChain {
    override fun intercept(): InterceptionType = InterceptionType.PASS
}


/**
 * 拦截器链，遍历所有的拦截器并检测是否放行。
 */
public class ListenerInterceptorChainImpl(
    private val interceptorList: List<ListenerInterceptor>,
    private val context: ListenerInterceptContext
) : ListenerInterceptorChain {
    /**
     * 检测拦截状态。
     */
    override fun intercept(): InterceptionType {
        // 只要一个要拦截，则为拦截，否则代表为放行。
        return InterceptionType.getTypeByPrevent(
            interceptorList.any { it.intercept(context).isPrevent }
        )
    }
}


/**
 * [ListenerInterceptChainFactory] 基础实例，
 * 通过dependBeanFactory 获取所有的拦截器并构建一个拦截链。
 * 拦截器列表仅会被获取一次。
 */
public class CoreListenerInterceptChainFactory(dependBeanFactory: DependBeanFactory) :
    ListenerInterceptChainFactory {

    /**
     * 懒加载所有的拦截器。
     */
    private val interceptorList: List<ListenerInterceptor> by lazy {
        val iType = ListenerInterceptor::class.java
        dependBeanFactory.allBeans.mapNotNull {
            val type = dependBeanFactory.getType(it)
            if(iType.isAssignableFrom(type)) dependBeanFactory[it] as ListenerInterceptor
            else null
        }
    }

    /**
     * 构建拦截器链。
     */
    override fun getInterceptorChain(context: ListenerInterceptContext): ListenerInterceptorChain {
        return with(interceptorList) {
            if (isEmpty()) EmptyListenerInterceptorChain
            else ListenerInterceptorChainImpl(this, context)
        }
    }
}











