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

import love.forte.simbot.api.message.containers.Container


/**
 * 标记一个 [PluginInfo] 的容器。
 */
public interface PluginInfoContainer : Container {
    /**
     * 得到一个 [PluginInfo].
     */
    val pluginInfo: PluginInfo
}


/**
 *
 * 插件的部分信息。
 *
 * @author ForteScarlet
 */
public data class PluginInfo(

    /**
     * 这个插件的唯一ID，你不应令其出现重名。
     *
     * 插件ID **不区分** 大小写。
     *
     * 插件的ID命名方式采用类似于包路径的全限定名称，例如：
     * `forte.example_plugin.xxxx`
     *
     * 这里推荐一个比较好的方案，即 `作者/组织名.作品名(可能一个系列中存在很多插件).当前插件名`
     *
     */
    val id: String,

    /**
     * 这个插件的名称, 或者说简称。一般相关日志会出现此名称
     */
    val name: String,


    /**
     * 此插件的版本号
     */
    val version: String,


    /**
     * 此插件的开发者信息
     */
    val developers: List<Developer>,

    )


/**
 *
 * 开发者信息。
 *
 */
data class Developer
@JvmOverloads
constructor(
    /**
     * 作者名称
     */
    val name: String,

    /**
     * 此人在开发中所扮演的角色列表
     */
    val roles: List<String>,

    /**
     * 作者的个人网站
     */
    val website: String? = null,

    /**
     * 此人的email
     */
    val email: String? = null,
)