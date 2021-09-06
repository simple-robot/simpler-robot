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
    override val pluginInfo: PluginInfo,
    override val pluginDetails: PluginDetails
) : Plugin {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimplePlugin

        return pluginInfo.id == other.pluginInfo.id
    }

    override fun hashCode(): Int {
        return pluginInfo.hashCode()
    }
}

