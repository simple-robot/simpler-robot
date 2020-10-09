/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerManagerBuilderImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.sender.MsgSenderFactories
import love.forte.simbot.core.bot.BotManager
import love.forte.simbot.core.exception.ExceptionProcessor
import love.forte.simbot.core.filter.AtDetectionFactory
import love.forte.simbot.core.filter.FilterManager
import love.forte.simbot.core.filter.ListenerFilter

/**
 *
 * 监听函数管理器的构建函数。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class CoreListenerManagerBuilder : ListenerManagerBuilder {

    // /** 监听函数列表 */
    // var listenerFunctions: MutableList<ListenerFunction> = mutableListOf()
    // /**
    //  * 注册一个 [监听函数][ListenerFunction]。
    //  */
    // override fun register(listenerFunction: ListenerFunction) {
    //     listenerFunctions.add(listenerFunction)
    // }

    /**
     * at匹配器工厂。
     */
    lateinit var atDetectionFactory: AtDetectionFactory

    /**
     * 异常处理器。
     */
    lateinit var exceptionManager: ExceptionProcessor

    /**
     * 消息拦截内容。
     */
    lateinit var msgInterceptData: MsgInterceptData

    /**
     * 监听函数拦截内容。
     */
    lateinit var listenerInterceptData: ListenerInterceptData

    /**
     * 监听函数上下文内容。
     */
    lateinit var listenerContextData: ListenerContextData

    /**
     * sender factories.
     */
    lateinit var msgSenderFactories: MsgSenderFactories

    /**
     * bot manager.
     */
    lateinit var botManager: BotManager

    /**
     * 得到一个 [ListenerManager] 实例。
     */
    override fun build(): ListenerManager {
        return CoreListenerManager(
            atDetectionFactory,
            exceptionManager,
            msgInterceptData,
            listenerInterceptData,
            listenerContextData,
            msgSenderFactories,
            botManager
        )
    }
}