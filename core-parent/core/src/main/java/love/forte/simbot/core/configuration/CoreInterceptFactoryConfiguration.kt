/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreInterceptFactoryConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.core.intercept.CoreListenerInterceptChainFactory
import love.forte.simbot.core.intercept.CoreMsgInterceptChainFactory
import love.forte.simbot.core.intercept.CoreListenerInterceptContextFactory
import love.forte.simbot.core.intercept.CoreMsgInterceptContextFactory
import love.forte.simbot.core.listener.ListenerInterceptChainFactory
import love.forte.simbot.core.listener.ListenerInterceptContextFactory
import love.forte.simbot.core.listener.MsgInterceptChainFactory
import love.forte.simbot.core.listener.MsgInterceptContextFactory

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreInterceptFactoryConfiguration {

    /**
     * 依赖工厂，用于获取所有的异常处理器。
     */
    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory


    /**
     * 配置 [ListenerInterceptChainFactory] 。
     */
    @CoreBeans
    fun coreListenerInterceptChainFactory(): ListenerInterceptChainFactory =
        CoreListenerInterceptChainFactory(dependBeanFactory)


    /**
     * 配置 [ListenerInterceptContextFactory]。
     */
    @CoreBeans
    fun coreListenerInterceptContextFactory(): ListenerInterceptContextFactory =
        CoreListenerInterceptContextFactory



    /**
     * 配置 [MsgInterceptChainFactory] 。
     */
    @CoreBeans
    fun coreMsgInterceptChainFactory(): MsgInterceptChainFactory =
        CoreMsgInterceptChainFactory(dependBeanFactory)


    /**
     * 配置 [MsgInterceptContextFactory]。
     */
    @CoreBeans
    fun coreMsgInterceptContextFactory(): MsgInterceptContextFactory =
        CoreMsgInterceptContextFactory

}