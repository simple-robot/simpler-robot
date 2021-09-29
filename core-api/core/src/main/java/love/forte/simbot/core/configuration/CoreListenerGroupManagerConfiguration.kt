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

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.listener.ListenerManager
import love.forte.simbot.listener.SimpleListenerGroupManager


/**
 * 监听函数分组管理器配置类。
 * @author ForteScarlet
 */
@ConfigBeans("coreListenerGroupManagerConfiguration")
public class CoreListenerGroupManagerConfiguration {

    @Depend
    lateinit var listenerManager: ListenerManager

    @Beans
    fun listenerGroupManager() = SimpleListenerGroupManager(listenerManager)

}