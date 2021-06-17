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
import love.forte.simbot.core.listener.CoreListenerResultFactory
import love.forte.simbot.listener.ListenerResultFactory

/**
 *
 * 配置 [ListenerResultFactory]。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreListenResultConfiguration")
public class CoreListenResultConfiguration {


    /**
     * listen result 工厂。
     */
    @CoreBeans("coreListenerResultFactory")
    fun coreListenerResultFactory(): ListenerResultFactory = CoreListenerResultFactory

}