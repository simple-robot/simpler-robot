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
 * [PluginManager] 基础实现类。
 *
 * @author ForteScarlet
 */
public class SimplePluginManager : PluginManager {



    override val plugins: List<Plugin>
        get() = TODO("Not yet implemented")



    override val globalLoader: ClassLoader
        get() = TODO("Not yet implemented")



    override fun getPlugin(id: String): Plugin {
        TODO("Not yet implemented")
    }
}