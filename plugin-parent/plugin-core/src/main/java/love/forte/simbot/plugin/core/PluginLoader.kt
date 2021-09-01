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
import java.nio.file.Path


/**
 *
 * 一个插件的类加载器.
 *
 * 插件的类加载器应当对应一个指定插件，包括其中的主体插件和专属依赖。
 *
 * 插件加载器是 [ClassLoader] 的一种，通过加载Jar文件来加载插件文件。
 * 需要做到当插件文件的内容发生变动的时候，类加载器也进行相应变化。
 *
 * 由于当更新的时候涉及到对类加载器以及其加载类的释放，尽可能避免对此类及其衍生类的强引用。
 *
 *
 * @author ForteScarlet
 */
public class PluginLoader(parent: ClassLoader = getSystemClassLoader(), vararg jarFile: Path) : ClassLoader() {
    // TODO


}







fun Path.toURL(): URL {
    return this.toUri().toURL()
}
