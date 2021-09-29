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

@file:JvmName("Plugins")
@file:JvmMultifileClass
package love.forte.simbot.plugin.core


/**
 *
 * 一个标记插件信息类型的注解。
 *
 * @see PluginInfo
 * @see Plugin
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SimbotPlugin(
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
     * 对此插件进行一个描述
     */
    val description: String,

    /**
     * 此插件的版本号
     */
    val version: String,

    /**
     * 开发者信息
     */
    val developers: Array<Dev>,
) {


    /**
     * 开发者信息。
     * @see Developer
     */
    annotation class Dev(
        /**
         * 作者名称
         */
        val name: String,

        /**
         * 此人在开发中所扮演的角色列表
         */
        val roles: Array<String> = [],

        /**
         * 作者的个人网站
         */
        val website: String = "",

        /**
         * 此人的email
         */
        val email: String = "",
    )
}



public fun SimbotPlugin.Dev.toDeveloper(): Developer {
    return Developer(
        name = name,
        roles = roles.toList(),
        website = website.takeIf { it.isNotEmpty() },
        email = email.takeIf { it.isNotEmpty() }
    )
}

public fun SimbotPlugin.toPluginInfo(): PluginInfo {
    return PluginInfo(
        id = id,
        name = name,
        version = version,
        description = description,
        developers = developers.map { it.toDeveloper() }
    )
}


