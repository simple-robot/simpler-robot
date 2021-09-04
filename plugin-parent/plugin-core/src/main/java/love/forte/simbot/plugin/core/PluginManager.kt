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
 * 动态插件的管理器类。
 *
 * @see Plugin
 *
 * @author ForteScarlet
 */
public interface PluginManager {

    /**
     * 得到当前已加载的插件列表。
     */
    val plugins: List<Plugin>


    /**
     * 公共lib的类加载器。
     */
    val globalLoader: ClassLoader


    /**
     * 根据ID得到一个对应的插件。
     */
    fun getPlugin(id: String): Plugin?
}