/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.core.listener.CoreListenerContextFactory
import love.forte.simbot.dispatcher.ContinuousSessionDispatcherFactory
import java.util.concurrent.TimeUnit


/**
 *
 * 配置监听上下文相关。
 *
 * @author ForteScarlet
 */
@ConfigBeans("coreListenerContextConfiguration")
@AsCoreConfig
public class CoreListenerContextConfiguration {


    @ConfigInject("continuousSession.defaultTimeout", orIgnore = true)
    var defaultTimeout: Long = TimeUnit.MINUTES.toMillis(1)

    /**
     * 配置 [CoreListenerContextFactory]
     */
    @OptIn(SimbotExperimentalApi::class)
    @CoreBeans("coreListenerContextFactory")
    fun coreListenerContextFactory(continuousSessionDispatcherFactory: ContinuousSessionDispatcherFactory) =
        CoreListenerContextFactory(continuousSessionDispatcherFactory, defaultTimeout)

}