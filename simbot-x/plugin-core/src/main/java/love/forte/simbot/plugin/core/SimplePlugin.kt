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

package love.forte.simbot.plugin.core


/**
 *
 * @author ForteScarlet
 */
public class SimplePlugin(
    override val pluginLoader: PluginLoader,
    resetFirst: Boolean = true,
) : Plugin {

    @Volatile
    private lateinit var _pluginDetails: PluginDetails

    @Volatile
    private lateinit var _pluginInfo: PluginInfo

    init {
        if (resetFirst) {
            reset()
        }
    }


    override val pluginInfo: PluginInfo get() = _pluginInfo
    override val pluginDetails: PluginDetails get() = _pluginDetails

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimplePlugin

        return pluginInfo.id == other.pluginInfo.id
    }

    override fun hashCode(): Int {
        return pluginInfo.hashCode()
    }

    @Synchronized
    override fun reset() {
        _pluginDetails = pluginLoader.extractDetails()
        _pluginInfo = _pluginDetails.extractInformation()
    }


}

