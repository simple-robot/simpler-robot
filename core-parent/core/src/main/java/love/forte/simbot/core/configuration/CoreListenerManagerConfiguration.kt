/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreListenerManagerConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.core.exception.ExceptionProcessor
import love.forte.simbot.core.filter.AtDetectionFactory
import love.forte.simbot.core.listener.*

/**
 *
 * 监听函数管理器配置器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreListenerManagerConfiguration {


    /**
     * at匹配器工厂。
     */
    @Depend
    lateinit var atDetectionFactory: AtDetectionFactory

    /**
     * 异常处理器。
     */
    @Depend
    lateinit var exceptionManager: ExceptionProcessor


    @Depend lateinit var msgInterceptContextFactory: MsgInterceptContextFactory // for msgInterceptData
    @Depend lateinit var msgInterceptChainFactory: MsgInterceptChainFactory // for msgInterceptData


    @Depend lateinit var listenerInterceptContextFactory: ListenerInterceptContextFactory // for ListenerInterceptData
    @Depend lateinit var listenerInterceptChainFactory: ListenerInterceptChainFactory // for ListenerInterceptData

    @Depend lateinit var listenerContextFactory: ListenerContextFactory // for ListenerContextData
    @Depend lateinit var contextMapFactory: ContextMapFactory // for ListenerContextData


    /**
     * 监听函数管理器builder。
     * 需要的参数真多啊。
     */
    @CoreBeans
    fun coreListenerManagerBuilder(): ListenerManagerBuilder {

        // 消息拦截内容。
        val msgInterceptData = MsgInterceptData(
            msgInterceptContextFactory, msgInterceptChainFactory
        )

        // 监听函数拦截内容。
        val listenerInterceptData = ListenerInterceptData(
            listenerInterceptContextFactory, listenerInterceptChainFactory
        )

        // 监听函数上下文内容。
        val listenerContextData = ListenerContextData(
            listenerContextFactory, contextMapFactory
        )


        return CoreListenerManagerBuilder().also {
            it.atDetectionFactory = atDetectionFactory
            it.exceptionManager = exceptionManager

            it.msgInterceptData = msgInterceptData
            it.listenerInterceptData = listenerInterceptData
            it.listenerContextData = listenerContextData
        }
    }


    /**
     * 获取监听函数实例。通过builder构建。
     */
    @CoreBeans
    fun coreListenerManager(builder: ListenerManagerBuilder): ListenerManager = builder.build()
}