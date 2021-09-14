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

package love.forte.simbot.plugin.core.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.listener.ListenerManager
import love.forte.simbot.listener.ListenerRegistered
import love.forte.simbot.plugin.core.SimplePluginManager
import kotlin.io.path.Path


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
@AsConfig(prefix = "simbot.plugin")
public class PluginManagerConfiguration : ListenerRegistered {

    private companion object LOG : TypedCompLogger(PluginManagerConfiguration::class.java)

    /**
     * 监听函数管理器
     */
    @Depend
    lateinit var listenerManager: ListenerManager

    /**
     * 依赖获取器
     */
    @Depend
    lateinit var dependBeanFactory: DependBeanFactory

    /** 插件所处的目录 */
    @ConfigInject
    var pluginRoot: String = "plugins"

    /**
     * 插件公共依赖所处lib目录
     */
    @ConfigInject
    var pluginGlobalLib: String = "pluginsLib"


    @ConfigInject
    var pluginLibName: String = "lib"

    @SpareBeans
    fun pluginManager(): SimplePluginManager {
        return SimplePluginManager(
            this.javaClass.classLoader,
            listenerManager,
            dependBeanFactory,
            Path(pluginRoot),
            Path(pluginGlobalLib),
            pluginLibName
        )
    }


    override fun onRegistered(manager: ListenerManager) {
        logger.warn("")
        logger.warn("你正在使用 'simple-robot-plugins' 模块。此模块尚处于试验阶段，如果发现任何问题，请反馈至issue: https://github.com/ForteScarlet/simpler-robot/issues/new/choose")
        logger.warn("")

        dependBeanFactory[SimplePluginManager::class.java].start()
    }
}