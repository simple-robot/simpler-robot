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
import java.net.URLStreamHandlerFactory


/**
 * 优先加载当前类加载器，如果当前没有再去加载父类加载器的类加载器。
 * @author ForteScarlet
 */
class ThisFirstURLClassLoader : URLClassLoader {
    constructor(urls: Array<out URL>?, parent: ClassLoader?) : super(urls, parent)
    constructor(urls: Array<out URL>?) : super(urls)
    constructor(urls: Array<out URL>?, parent: ClassLoader?, factory: URLStreamHandlerFactory?) : super(urls,
        parent,
        factory)

    override fun loadClass(name: String?, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            val loaded = findLoadedClass(name)
            if (loaded != null) {
                return loaded
            }
            val foundThis = try {
                findClass(name)
            } catch (cnf: ClassNotFoundException) {
                null
            }
            if (foundThis != null) {
                return foundThis
            }
            return super.loadClass(name, resolve)
        }
    }


    override fun getResource(name: String?): URL? {
        return findResource(name) ?: super.getResource(name)
    }


}