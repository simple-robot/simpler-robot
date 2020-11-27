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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.api.sender.MsgSenderFactories
import love.forte.simbot.bot.BotManager
import love.forte.simbot.core.listener.CoreListenerManagerBuilder
import love.forte.simbot.core.listener.ListenerContextData
import love.forte.simbot.core.listener.ListenerInterceptData
import love.forte.simbot.core.listener.MsgInterceptData
import love.forte.simbot.exception.ExceptionProcessor
import love.forte.simbot.filter.AtDetectionFactory
import love.forte.simbot.listener.*

/**
 *
 * 监听函数管理器配置器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreListenerManagerConfiguration")
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

    @Depend
    lateinit var msgSenderFactories: MsgSenderFactories

    @Depend
    lateinit var botManager: BotManager

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
    @CoreBeans("coreListenerManagerBuilder")
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

            it.msgSenderFactories = msgSenderFactories

            it.botManager = botManager
        }
    }


    /**
     * 获取监听函数实例。通过builder构建。
     */
    @CoreBeans("coreListenerManager")
    fun coreListenerManager(builder: ListenerManagerBuilder): ListenerManager = builder.build()


}