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

import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path


/**
 *
 * 一个插件的类加载器
 *
 * @author ForteScarlet
 */
class PluginClassLoader(parent: ClassLoader = getSystemClassLoader(), vararg jarFile: Path) :
    URLClassLoader(jarFile.map { it.toURL() }.toTypedArray(), parent) {

    override fun loadClass(name: String): Class<*> {
        return super.loadClass(name)
    }


}


internal fun Path.toURL(): URL {
    return this.toUri().toURL()
}
