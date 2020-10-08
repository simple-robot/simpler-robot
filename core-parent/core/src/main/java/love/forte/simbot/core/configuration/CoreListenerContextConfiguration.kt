/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ContextConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.core.listener.ContextMapFactory
import love.forte.simbot.core.listener.CoreContextMapFactory
import love.forte.simbot.core.listener.ListenerContextFactory
import love.forte.simbot.core.listener.ListenerContextFactoryImpl


/**
 *
 * 配置监听上下文相关。
 *
 * @author ForteScarlet
 */
@ConfigBeans
public class CoreListenerContextConfiguration {

    /**
     * 使用单例对象 [CoreContextMapFactory]。
     */
    @CoreBeans
    fun coreContextMapFactory(): ContextMapFactory = CoreContextMapFactory


    /**
     * 配置 [ListenerContextFactoryImpl]
     */
    @CoreBeans
    fun coreListenerContextFactory(): ListenerContextFactory = ListenerContextFactoryImpl

}